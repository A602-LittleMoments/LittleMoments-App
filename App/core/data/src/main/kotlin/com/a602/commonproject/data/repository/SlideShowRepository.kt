package com.a602.commonproject.data.repository

import com.a602.commonproject.model.data.Slideshow
import com.a602.commonproject.network.model.CreateSlideshowRequest
import java.io.File
import kotlinx.coroutines.flow.Flow

interface SlideshowRepository {

    // 1. 슬라이드쇼 목록 조회 (DB 관찰)
    fun getSlideshowsStream(): Flow<List<Slideshow>>

    // 2. 목록 새로고침 (서버 -> DB 동기화)
    suspend fun refreshSlideshows(): Result<Unit>

    // 3. 슬라이드쇼 생성 요청
    suspend fun createSlideshow(request: CreateSlideshowRequest): Result<Unit>

    // 4. 상세 정보 갱신 (제작 상태 확인용 polling 등)
    suspend fun syncSlideshowDetail(slideshowId: String): Result<Unit>

    // 5. 영상 다운로드 (서버 URL 발급 -> 파일 다운로드 -> DB 경로 업데이트)
    suspend fun downloadSlideshow(slideshowId: String): Result<File>

    // 5-1. 영상 다운로드 (진행률 콜백 포함)
    // onProgress: 0.0f ~ 1.0f (0% ~ 100%)
    suspend fun downloadSlideshowWithProgress(
        slideshowId: String,
        onProgress: (Float) -> Unit
    ): Result<File>

    // 6. 슬라이드쇼 삭제 (DB + 파일 삭제)
    suspend fun deleteSlideshow(slideshowId: String): Result<Unit>
}

