package com.a602.commonproject.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temp_media")
data class TempMediaEntity(
    @PrimaryKey val tempId: String, // UUID
    val localUri : String,          // 내 폰에 원본 파일이 있음
    val takenAt: Long,              // 촬영 시간
    val expirationDate: Long        // 삭제 예정일 (D-Day)
    )
