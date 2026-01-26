package com.a602.commonproject.network.di

import javax.inject.Inject
import kotlin.jvm.Throws
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException

class AuthInterceptor @Inject constructor(
    // ⚠️ TODO: 나중에 :core:datastore 모듈 만들면 TokenManager 주입 받아야 함
    // private val tokenManager: TokenManager
) : Interceptor{
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // 1. "Auth: No" 마커 헤더가 있는지 확인
        // (회원가입, 로그인처럼 토큰이 필요 없는 요청인지 체크)
        val noAuthHeader = originalRequest.header("Auth")

        if(noAuthHeader == "No"){
            // 🛑 토큰 필요 없음!
            // "Auth" 마커만 제거하고 그대로 서버로 보냅니다.
            requestBuilder.removeHeader("Auth")
        }
        else {
            // [CASE 2] 토큰 필요함 (일반적인 경우)
            // TODO: 실제로는 DataStore에서 저장된 토큰을 꺼내와야 함
            val accessToken = "TEMP_ACCESS_TOKEN"
            requestBuilder.header("Authorization","Bearer $accessToken")
        }
        return chain.proceed(requestBuilder.build())
    }

}
