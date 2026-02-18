package com.a602.commonproject.data.model

import com.a602.commonproject.database.model.ShareMediaEntity
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.network.model.MediaFileKey
import com.a602.commonproject.network.model.MediaResponse
import com.a602.commonproject.network.model.MediaUploadMetadata
import kotlin.time.Instant

/**
 *  [DB -> UI] 화면 표시용 (DB에 없는 필드는 기본값 처리)
 */
fun ShareMediaEntity.asExternalModel(): SharedMedia {
    return SharedMedia(
        id = mediaId,
        type = SharedMedia.MediaType.from(type), // "PHOTO"/"VIDEO" -> Enum

        localUri = localUri,
        remoteUrl = remoteUrl,
        thumbnailUrl = thumbnailUrl,

        subLocalUri = subLocalUri,
        subRemoteUrl = subRemoteUrl,
        subThumbnailUrl = subThumbnailUrl,

        cameraFacing = cameraFacing,

        caption = caption,
        dateTaken = takenAt,

        orientation = orientation,

        uploaderName = uploaderName,

        syncStatus = when (syncStatus) {
            "SYNCED" -> SharedMedia.SyncStatus.SYNCED
            "TO_BE_DELETE" -> SharedMedia.SyncStatus.TO_BE_DELETED // 오타 주의: TO_BE_DELETE
            else -> SharedMedia.SyncStatus.NOT_UPLOADED
        }
    )
}

/**
 * [UI -> DB] 로컬 저장용
 * 지금 당장 사용하지 않음 saveNewMedia에서 사용 가능
 */
fun SharedMedia.toEntity(): ShareMediaEntity {
    return ShareMediaEntity(
        mediaId = id,
        localUri = localUri,
        remoteUrl = remoteUrl,
        thumbnailUrl = thumbnailUrl,
        subLocalUri = subLocalUri,
        subRemoteUrl = subRemoteUrl,
        subThumbnailUrl = subThumbnailUrl,
        cameraFacing = cameraFacing,
        caption = caption,
        type = type.name, // Enum -> String
        takenAt = dateTaken,
        uploaderName = uploaderName ?: "나",
        orientation = orientation,
        syncStatus = when (syncStatus) {
            SharedMedia.SyncStatus.SYNCED -> "SYNCED"
            else -> "NOT_UPLOADED"
        }
    )
}

// =================================================================
// 3-1. [Network(List) -> DB] 서버 리스트를 DB에 쏟아부을 때 (FetchWorker용)
// =================================================================
fun MediaResponse.toEntity(): ShareMediaEntity {
    return ShareMediaEntity(
        mediaId = mediaId,
        localUri = null,
        remoteUrl = storageUrl,
        thumbnailUrl = thumbUrl,
        subLocalUri = null,
        subRemoteUrl = subStorageUrl,
        subThumbnailUrl = subThumbUrl,
        caption = null,
        type = mediaType,
        takenAt = try { Instant.parse(takenAt).toEpochMilliseconds() } catch(e:Exception){ 0L },
        uploaderName = "알 수 없음",
        orientation = orientation,
        syncStatus = "SYNCED"
    )
}


// =================================================================
// 4. [Network(List) -> UI] 리스트 조회 결과 바로 변환 (DB 안 거칠 때)
// =================================================================
fun MediaResponse.asExternalModel(): SharedMedia {
    return SharedMedia(
        id = mediaId,
        type = SharedMedia.MediaType.from(mediaType),
        localUri = null,
        remoteUrl = storageUrl,
        thumbnailUrl = thumbUrl,
        subRemoteUrl = subStorageUrl,
        caption = caption,
        dateTaken = try { Instant.parse(takenAt).toEpochMilliseconds() } catch(e:Exception){ 0L },
        orientation = orientation,
        uploaderName = uploadedBy.nickname,
        syncStatus = SharedMedia.SyncStatus.SYNCED
    )
}

// DB(Entity) -> Network(Upload Metadata)
fun ShareMediaEntity.toModel(): MediaUploadMetadata {
    // 1. 파일 맵 구성 (예시대로 front/rear 키와 접미사 사용)
    val fileMap = mutableMapOf<String, MediaFileKey>()

    when (cameraFacing) {
        "DUAL" -> {
            fileMap["rear"] = MediaFileKey(clientFileKey = "$mediaId-rear")
            fileMap["front"] = MediaFileKey(clientFileKey = "$mediaId-front")
        }
        "FRONT" -> {
            fileMap["front"] = MediaFileKey(clientFileKey = "$mediaId-front")
        }
        else -> { // REAR 또는 기본값
            fileMap["rear"] = MediaFileKey(clientFileKey = "$mediaId-rear")
        }
    }

    // 2. 날짜 변환 (Long -> ISO 8601 String)
    // 예시: "2026-01-20T10:12:00Z"
    val dateString = try {
        Instant.fromEpochMilliseconds(takenAt).toString()
    } catch (e: Exception) {
        ""
    }

    return MediaUploadMetadata(
        clientMediaKey = mediaId,
        mediaType = type,
        takenAt = dateString,
        cameraFacing = cameraFacing,
        orientation = orientation,
        caption = caption,
        files = fileMap
    )
}
