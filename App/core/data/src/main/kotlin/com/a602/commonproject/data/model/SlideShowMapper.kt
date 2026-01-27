package com.a602.commonproject.data.model

import com.a602.commonproject.database.model.SlideshowEntity
import com.a602.commonproject.model.data.Slideshow
import com.a602.commonproject.network.model.ExportSlideshowResponse
import com.a602.commonproject.network.model.SlideshowDetailResponse
import com.a602.commonproject.network.model.SlideshowSummaryResponse

// =================================================================
// 1. [DB -> UI] (이제 DB에 다 있으니 아주 깔끔해짐!)
// =================================================================
fun SlideshowEntity.asExternalModel(): Slideshow {
    return Slideshow(
        id = slideshowId,
        title = title,
        thumbnailUrl = thumbnailUrl,  // DB에 캐싱된 썸네일 사용
        remoteVideoUrl = remoteVideoUrl,
        localVideoPath = localVideoPath,

        status = try {
            Slideshow.MakeStatus.valueOf(status)
        } catch (e: Exception) {
            Slideshow.MakeStatus.UNKNOWN
        },

        mediaCount = mediaCount,
        durationSec = duration,
        createdAt = createAt
    )
}
// =================================================================
// 2. [Network(Summary) -> DB] 서버 목록 -> DB 캐싱
// =================================================================
fun SlideshowSummaryResponse.toEntity(): SlideshowEntity {
    return SlideshowEntity(
        slideshowId = slideshowId,
        localVideoPath = null, // 아직 다운로드 안 됨
        remoteVideoUrl = null, // Summary엔 URL 없음
        thumbnailUrl = resultThumbUrl,
        mediaCount = 0,        // Summary엔 개수 없음
        title = "추억 영상",     // 기본 제목
        duration = 0,
        createAt = System.currentTimeMillis(), // 서버가 날짜 안 주면 현재시간
        status = "COMPLETED"   // 목록에 떴다는 건 제작은 완료됐다는 뜻
    )
}

// =================================================================
// 3. [Network(Detail) -> DB] 상세 조회 -> DB 업데이트
// =================================================================
fun SlideshowDetailResponse.toEntity(id: String): SlideshowEntity {
    return SlideshowEntity(
        slideshowId = id,
        localVideoPath = null, // 기존 경로 유지하려면 Repository 처리 필요
        remoteVideoUrl = resultUrl,
        thumbnailUrl = resultThumbUrl,
        mediaCount = mediaCount,
        title = "추억 영상",
        duration = 0,
        createAt = System.currentTimeMillis(),
        status = if (resultUrl != null) "COMPLETED" else "PROCESSING"
    )
}
// =================================================================
// 4. [Network(Export) -> DB] 다운로드 정보 -> DB 업데이트 준비
// (보통 Repository에서 다운로드 성공 후 Entity를 업데이트합니다)
// =================================================================
fun ExportSlideshowResponse.toEntity(localPath: String): SlideshowEntity {
    return SlideshowEntity(
        slideshowId = data.slideshowId,
        localVideoPath = localPath, // 다운로드된 실제 경로
        remoteVideoUrl = data.downloadUrl,
        thumbnailUrl = null, // 기존 썸네일 유지 필요 (Repository에서 처리)
        mediaCount = 0,
        title = data.fileName,
        duration = 0, // 파일에서 추출 필요
        createAt = System.currentTimeMillis(),
        status = "DOWNLOADED" // 상태 변경!
    )
}
