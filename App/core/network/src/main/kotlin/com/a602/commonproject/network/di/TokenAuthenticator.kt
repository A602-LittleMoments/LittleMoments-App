package com.a602.commonproject.network.di

import com.a602.commonproject.datastore.datastore.UserPreferencesDataStore
import com.a602.commonproject.network.api.RetrofitAuthApi
import com.a602.commonproject.network.datasource.AuthNetworkDataSource
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator @Inject constructor(
    private val userPreferences: UserPreferencesDataStore,
    private val authDataSourceProvider: Provider<AuthNetworkDataSource>,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. 401 에러가 아니면 토큰 갱신을 시도하지 않습니다.
        if (response.code != 401) return null

        // 2. DataStore에서 저장된 Refresh Token을 가져옵니다.
        val refreshToken = runBlocking {
            userPreferences.refreshToken.first()
        } ?: return null // 리프레시 토큰이 없으면 갱신 불가

        // 3. 여러 요청이 동시에 만료되었을 때 중복 갱신을 방지하기 위해 동기화 처리합니다.
        val newAccessToken = synchronized(this) {
            // 로컬에 저장된 현재 액세스 토큰을 가져옴 (DataStore 접근이 suspend라면 runBlocking 사용)
            val currentAccessToken = runBlocking { userPreferences.accessToken.first() }
            // 401 에러가 났던 원래 요청(Request)의 헤더에 들어있던 토큰
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            // 이미 다른 스레드에서 토큰을 갱신했다면, 갱신된 토큰으로 재시도합니다.
            if (currentAccessToken != requestToken) {
                currentAccessToken
            } else {
                try {
                    // 1. 서버에서 토큰 갱신 요청
                    val tokenResponse = runBlocking {
                        authDataSourceProvider.get().refreshToken(refreshToken)
                    }

                    // 2. DataStore에 새 토큰 저장
                    runBlocking {
                        userPreferences.updateAccessToken(tokenResponse.accessToken)
                    }

                    tokenResponse.accessToken
                } catch (e: Exception) {
                    // 토큰 갱신을 실패 하면 로그아웃
                    runBlocking { userPreferences.clear() }
                    null
                }
            }
        }
        return if (newAccessToken != null) {
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        } else {
            null
        }
    }
}
