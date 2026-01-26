package com.a602.commonproject.network.api

import com.a602.commonproject.network.model.DailyQuestionResponse
import retrofit2.http.GET

internal interface RetrofitQuestionApi {
    // 8.1 오늘의 질문
    @GET("daily-questions/today")
    suspend fun getTodayQuestion(): DailyQuestionResponse
}
