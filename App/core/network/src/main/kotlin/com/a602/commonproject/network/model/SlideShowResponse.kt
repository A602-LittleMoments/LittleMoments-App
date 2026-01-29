package com.a602.commonproject.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

// 7.1 슬라이드쇼 생성 요청
@InternalSerializationApi
@Serializable
data class CreateSlideshowRequest(
    val projectType: String, // "PERIOD", "KEYWORD"
    val startDate: String? = null,
    val endDate: String? = null,
    val keywordId: String? = null
)

// 7.1 생성 응답
@InternalSerializationApi
@Serializable
data class CreateSlideshowResponse(
    val slideshowId: String,
    val status: String // "QUEUED", "PROCESSING", "COMPLETED"
)

// 7.2 상세 조회 응답 (ID 없음, 상세 데이터 위주)
@InternalSerializationApi
@Serializable
data class SlideshowDetailResponse(
    val mediaCount: Int,
    val resultUrl: String? = null,     // 아직 생성 중이면 null일 수 있음
    val resultThumbUrl: String? = null
)

// 7.4 목록 조회 응답 (ID 필수, 요약 데이터 위주)
@InternalSerializationApi
@Serializable
data class SlideshowSummaryResponse(
    val slideshowId: String, // 👈 이제 당당하게 String (Non-null)!
    val userId: String,
    val resultThumbUrl: String? = null
)

// 7.5 다운로드 요청
@InternalSerializationApi
@Serializable
data class ExportSlideshowRequest(
    val expiresInSeconds: Long = 600
)

// 7.5 다운로드 응답
@InternalSerializationApi
@Serializable
data class ExportSlideshowResponse(
    val data: ExportData
) {
    @Serializable
    data class ExportData(
        val slideshowId: String,
        val downloadUrl: String,
        val expiresAt: String,
        val mimeType: String,
        val fileName: String,
        val fileSizeBytes: Long
    )
}
