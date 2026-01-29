package com.a602.commonproject.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "shared_media")
data class ShareMediaEntity(
    @PrimaryKey val mediaId : String,   // UUID
    val localUri : String?,             // 업로드 전 : 내부 경로 / 업로드 후 : NULL
    val remoteUrl : String?,            // 업로드 전 : NULL / 업로드 후: URL 잇음
    val thumbnailUrl : String?,          // 리스트용 저와질 URL

    // Map 대신 이렇게 변수를 직접 만들면 Converter가 필요 없습니다!
    val subLocalUri: String? = null,
    val subRemoteUrl: String? = null,
    val subThumbnailUrl: String? = null,

    // 카메라 방향 정보 (서버 명세 연동용)
    // "REAR"(기본), "FRONT", "DUAL"(앞뒤 모두)
    val cameraFacing: String = "DUAL",
    val orientation : Int,

    val caption: String? = null,     // ✨ 추가됨: 임시 저장 단계에서도 메모 가능하게

    val type : String,                  // "PHOTO" or "VIDEO"
    val takenAt : Long,                 // 촬영 시간
    val uploaderName : String,          // 업로드 사람 이름

    val syncStatus : String             // "NOT_UPLOADED", "SYNCED", "TO_BE_DELETE"
)
