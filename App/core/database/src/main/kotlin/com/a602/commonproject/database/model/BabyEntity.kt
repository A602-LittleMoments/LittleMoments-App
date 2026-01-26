package com.a602.commonproject.database.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "babies")
data class BabyEntity(
    @PrimaryKey val babyId : String,
    val babyName : String,
    val birthDate : Long,
    val profileUrl: String,           // 서버 URL (없으면 기본 아이콘 표시)

    val gender: String = "U"         // 선택 사항, M(남), F(여), U(미정) - 테마 색상 결정용
)
