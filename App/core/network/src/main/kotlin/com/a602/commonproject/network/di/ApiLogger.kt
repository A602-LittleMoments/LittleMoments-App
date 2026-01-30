package com.a602.commonproject.network.di

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class ApiLogger : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // 1. Retrofit이 심어둔 태그(Invocation)를 꺼냅니다.
        val invocation = request.tag(Invocation::class.java)
        var location = "Unknown"

        // 2. 태그가 있다면 로그를 찍습니다.
        if (invocation != null) {
            val method = invocation.method()
            val className = method.declaringClass.simpleName // 예: AuthApi
            val methodName = method.name                  // 예: login

            // 💡 눈에 잘 띄게 이모지랑 같이 찍어줍니다.
            Log.d("API_TRACE", "🚀 요청 위치: $className.$methodName()")
        }

        val response = chain.proceed(request)
        // 3. 에러가 났는지 확인 (!isSuccessful)
        if (!response.isSuccessful) {
            // ⚠️ 주의: response.body?.string()을 쓰면 안됨! (스트림 소비됨)
            // 대신 peekBody()를 사용해서 복사본을 읽어야 함 (최대 1MB 정도만 읽기)
            val errorBody = response.peekBody(1024 * 1024).string()

            Log.e("API_ERROR", "❌ 에러 발생 [$location]")
            Log.e("API_ERROR", "   code: ${response.code}")
            Log.e("API_ERROR", "   message: ${response.message}")
            Log.e("API_ERROR", "   body: $errorBody")
        }

        return response
    }
}
