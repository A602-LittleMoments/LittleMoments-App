package com.a602.commonproject.model.data

import java.util.concurrent.TimeUnit

/**
 * 🗑️ 앱 DB에 있는 미디어
 */
data class TempMedia(
    val id: String,           // tempId
    val localUri: String,     // 원본 경로
    val subLocalUri: String? = null, // 전면 카메라 (PIP) 경로
    val takenAt: Long,        // 촬영일
    val expirationDate: Long  // 완전 삭제 예정일 (Timestamp)
) {
    /**
     * ⏳ 남은 기간 계산 (UI 표시용)
     * 예: "30일 남음", "오늘 삭제됨"
     */
    fun getDaysLeft(): Long {
        val now = System.currentTimeMillis()
        val diff = expirationDate - now
        return if (diff < 0) 0 else TimeUnit.MILLISECONDS.toDays(diff)
    }
}
