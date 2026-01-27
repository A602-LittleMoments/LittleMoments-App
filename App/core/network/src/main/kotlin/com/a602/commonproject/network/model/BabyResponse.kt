package com.a602.commonproject.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable


// 3.1 등록, 3.2 수정 요청 (Multipart 'data' 파트)
@InternalSerializationApi
@Serializable
data class BabyRequest(
    val babyId: String? = null, // 등록시에는 null, 수정시에는 필수
    val babyName: String,
    val birthDate: String, // "YYYY-MM-DD"
    val gender: String,    // "F", "M"
    val pictureUrl : String? = null // 수정시 사용
)

// 3.2 아기 목록 조회 응답
@InternalSerializationApi
@Serializable
data class BabyListResponse(
    val babies: List<BabyResponse>
)

@InternalSerializationApi
@Serializable
data class BabyResponse(
    val babyId: String,
    val babyName: String,
    val birthDate: String,
    val gender: String,
    val pictureUrl: String?
)
