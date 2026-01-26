package com.a602.commonproject.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

// 6.1.1 모음(태그) 목록 응답
@InternalSerializationApi
@Serializable
data class CollectionListResponse(
    val keywords: List<CollectionKeyword>
)

@InternalSerializationApi
@Serializable
data class CollectionKeyword(
    val keywordId: String,
    val value: String
)
