package com.a602.commonproject.model.data


/**
 * 🏷️ AI가 분류한 사진 모음 (키워드)
 * (예: "웃는 모습", "해변", "반려동물")
 */
data class Collection(
    val categoryId: String,
    val categoryValue: String,
    val keywordId: String,
    val keywordValue: String,
    val collectionSize : Int
)
