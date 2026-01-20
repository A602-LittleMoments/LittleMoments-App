package com.a602.commonproject.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "shared_media")
data class ShareMediaEntity(
    @PrimaryKey val mediaId : String,   // UUID
    val localUri : String?,             // 업로드 전 : 내부 경로 / 업로드 후 : NULL
    val remoteUrl : String?,            // 업로드 전 : NULL / 업로드 후: URL 잇음
    val thumbnailUrl : String,          // 리스트용 저와질 URL

    val type : String,                  // "PHOTO" or "VIDEO"
    val takenAt : Long,                 // 촬영 시간
    val uploaderName : String,          // 업로드 사람 이름

    val syncStatus : String             // "NOT_UPLOADED", "SYNCED"
)
