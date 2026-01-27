package com.a602.commonproject.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "slideshows")
data class SlideshowEntity(
    @PrimaryKey val slideshowId: String,

    // 💾 로컬 저장 경로 (다운로드 전이면 null)
    val localVideoPath: String? = null,

    // ☁️ 서버 정보 캐싱 (오프라인에서도 목록을 예쁘게 보여주기 위함)
    val remoteVideoUrl: String? = null,
    val thumbnailUrl: String? = null,
    val mediaCount: Int = 0,

    // 📝 메타 데이터
    val title: String,          // 제목
    val duration: Long = 0,     // 재생 시간 (초)
    val createAt: Long,         // 생성일

    // 🚦 상태 관리 (Making, Downloaded, etc)
    // "QUEUED", "PROCESSING", "COMPLETED", "DOWNLOADED"
    val status: String = "UNKNOWN"
)
