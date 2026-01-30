package com.a602.commonproject.network.di

import android.provider.Settings
import com.a602.commonproject.datastore.datastore.UserPreferencesDataSource
import javax.inject.Inject
import kotlin.jvm.Throws
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException

class AuthInterceptor @Inject constructor(
    // private val tokenManager: TokenManager
    private val userPreferences: UserPreferencesDataSource

) : Interceptor{
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // 1. 공통 헤더 설정
        requestBuilder.addHeader("X-OS-Type", "Android")
        // Device ID가 DataStore에 저장되어 있다면 꺼내서 넣습니다.
        val deviceId = runBlocking { userPreferences.getOrCreateDeviceId() }
        requestBuilder.addHeader("X-Device-Id", deviceId) // 서버와 약속한 헤더 키값

        // 2. "Auth: No" 마커 헤더가 있는지 확인
        // (회원가입, 로그인처럼 토큰이 필요 없는 요청인지 체크)
        val noAuthHeader = originalRequest.header("Auth")

        if(noAuthHeader == "No"){
            // 🛑 토큰 필요 없음!
            // "Auth" 마커만 제거하고 그대로 서버로 보냅니다.
            requestBuilder.removeHeader("Auth")
        }
        else {
            // [CASE 2] 토큰 필요함 (일반적인 경우)
            // ⚠️ 중요: Interceptor는 동기(Synchronous) 방식인데, DataStore는 비동기(Flow)입니다.
            // 그래서 'runBlocking'을 사용해 데이터를 꺼낼 때까지 잠시 기다리게 합니다.
            // (네트워크 요청은 이미 백그라운드 스레드에서 돌고 있어서 안전합니다.)
            val accessToken = runBlocking {
                userPreferences.accessToken.first()
            }

            // 토큰이 있으면 넣고, 없으면 빈 문자열(또는 처리 안 함)
            // (토큰 없이 보내면 서버가 401 에러를 줄 테니 자연스럽게 처리됨)
            if (!accessToken.isNullOrEmpty()) {
                requestBuilder.header("Authorization", "Bearer $accessToken")
            }
        }
        return chain.proceed(requestBuilder.build())
    }

}
