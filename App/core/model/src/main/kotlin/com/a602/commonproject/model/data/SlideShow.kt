package com.a602.commonproject.model.data

/**
 * 🎬 추억 영상 (슬라이드쇼) 데이터 모델
 */
data class Slideshow(
    val id: String,                // slideshowId
    val title: String,             // 영상 제목

    val thumbnailUrl: String?,     // 서버 썸네일
    val remoteVideoUrl: String?,   // 서버 스트리밍 주소
    val localVideoPath: String?,   // 다운로드된 파일 경로

    val status: MakeStatus,        // 제작 상태
    val mediaCount: Int = 0,       // 사진 개수
    val durationSec: Long = 0,     // 영상 길이(초)
    val createdAt: Long,            // 생성일

    // 다운로드 링크 만료일 (UI 전용, DB 저장 굳이 안 해도 됨)
    val downloadExpiresAt: String? = null
) {

    /**
     * 🚦 제작 상태 (서버: QUEUED -> PROCESSING -> COMPLETED)
     */
    enum class MakeStatus {
        QUEUED, PROCESSING, COMPLETED, DOWNLOADED, FAILED, UNKNOWN
    }

    /**
     * 💡 재생 가능한 URL (로컬 우선, 없으면 서버)
     */
    // 💡 UI 편의 기능
    val isDownloaded: Boolean
        get() = status == MakeStatus.DOWNLOADED && !localVideoPath.isNullOrBlank()

    // 재생 우선순위: 로컬 -> 리모트
    val playableUrl: String?
        get() = localVideoPath ?: remoteVideoUrl
}
