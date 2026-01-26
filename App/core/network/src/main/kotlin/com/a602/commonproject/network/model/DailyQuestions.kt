package com.a602.commonproject.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

// 8.1 오늘의 질문 응답
@InternalSerializationApi
@Serializable
data class DailyQuestionResponse(
    val questionId: String,
    val content: String
)
