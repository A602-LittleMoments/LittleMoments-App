package com.a602.commonproject.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

// 4.1 배치 업로드 메타데이터 (Multipart 'items' 파트 JSON용)
@InternalSerializationApi
@Serializable
data class MediaUploadMetadataWrapper(
    val items: List<MediaUploadMetadata>
)

@InternalSerializationApi
@Serializable
data class MediaUploadMetadata(
    val clientMediaKey: String, // 로컬 DB 매칭용 키
    val mediaType: String,      // "PHOTO", "VIDEO"
    val takenAt: String,        // 시점 날짜
    val cameraFacing: String? = "DUAL",  // "FRONT", "REAR", "DUAL"
    val orientation: Int,       // 0이면 가로, 1이면 세로
    val caption: String? = null,
    val files: Map<String, MediaFileKey> // ex: "front" -> { "clientFileKey": "..." }
)

@InternalSerializationApi
@Serializable
data class MediaFileKey(
    val clientFileKey: String
)


// 4.1 배치 업로드 응답
@InternalSerializationApi
@Serializable
data class BatchUploadResponse(
    val total: Int,
    val successCount: Int,
    val failCount: Int,
    val results: List<BatchUploadResult>
)

@InternalSerializationApi
@Serializable
data class BatchUploadResult(
    val clientMediaKey: String,
    val status: String, // "SUCCESS", "FAIL"
    val mediaId: String? = null,
    val coverThumbUrl: String? = null,
    val files: Map<String, UploadFileResult>? = null
)

@InternalSerializationApi
@Serializable
data class UploadFileResult(
    val status: String,
    val storageUrl: String? = null,
    val thumbUrl: String? = null
)

// 4.3 미디어 리스트 응답, 6.1.2 클러스터 카드 클릭
@InternalSerializationApi
@Serializable
data class MediaListResponse(
    val medias: List<MediaResponse>,
    val nextCursor: String? = null
)

@InternalSerializationApi
@Serializable
data class MediaResponse(
    val mediaId: String,
    val groupId: String,
    val uploadedBy: MediaUserResponse, // 아래 JSON 맞춤형 클래스 사용
    val mediaType: String,

    val storageUrl: String,
    val thumbUrl: String?,

    val subStorageUrl: String?,
    val subThumbUrl: String?,

    val takenAt: String,
    val cameraFacing: String,
    val orientation: Int,
    val caption: String?,
    val createdAt: String,
    val updatedAt: String
)

/**
 * JSON의 uploadedBy 구조에 맞춘 전용 객체
 * (기존 UserResponse와 형태가 다르므로 새로 정의하거나 수정해서 사용)
 */
@InternalSerializationApi
@Serializable
data class MediaUserResponse(
    val userId: String,
    val nickname: String,
    val profileImageUrl: String?
)
// 4.4 미디어 상세 응답
@InternalSerializationApi
@Serializable
data class MediaDetailResponse(
    val mediaId: String,
    val groupId: String,
    val uploadedBy: UserResponse, // auth 패키지의 User 재사용
    val mediaType: String,
    val storageUrl: String,
    val thumbUrl: String?,
    val takenAt: String,
    val cameraFacing: String?,
    val orientation: Int,
    val durationMs: Long?,
    val caption: String?,
    val createdAt: String,
    val updatedAt: String
)

// 4.5 캡션 수정 요청
@InternalSerializationApi
@Serializable
data class UpdateCaptionRequest(
    val caption: String
)
