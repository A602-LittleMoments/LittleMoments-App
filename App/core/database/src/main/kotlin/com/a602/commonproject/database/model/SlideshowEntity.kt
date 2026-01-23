package com.a602.commonproject.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "slideshows")
data class SlideshowEntity(
    @PrimaryKey val slideshowId: String,
    val localVideoPath : String,        // 렌더링된 mp4 파일 경로
    val title : String,                 // 영상 제목
    val duration : Long,                // 재생 시간
    val createAt : Long                 // 생성일
)
