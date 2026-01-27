package com.a602.commonproject.model.data

/**
 * 📝 매일 도착하는 육아 질문
 */
data class DailyQuestion(
    val questionId: String,      // Network: questionId
    val content: String, // 질문 내용
    // (나중에 '답변 여부' 같은 필드가 필요하면 여기에 추가)
)
