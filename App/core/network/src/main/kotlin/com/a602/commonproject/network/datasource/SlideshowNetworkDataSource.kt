package com.a602.commonproject.network.datasource

import com.a602.commonproject.network.api.RetrofitSlideshowApi
import com.a602.commonproject.network.model.CreateSlideshowRequest
import com.a602.commonproject.network.model.CreateSlideshowResponse
import com.a602.commonproject.network.model.ExportSlideshowRequest
import com.a602.commonproject.network.model.ExportSlideshowResponse
import com.a602.commonproject.network.model.SlideshowDetailResponse
import com.a602.commonproject.network.model.SlideshowSummaryResponse
import javax.inject.Inject


/**
 * 🎬 SlideshowNetworkDataSource 인터페이스
 *
 * 사진들을 모아서 '성장 영상(슬라이드쇼)'을 만드는 기능입니다.
 * 영상은 서버에서 비동기로 생성되므로, 생성 요청 후 상태를 확인하거나
 * 완성된 목록을 조회하는 API들로 구성됩니다.
 */
interface SlideshowNetworkDataSource {
    // 7.1 슬라이드쇼 생성 요청
    // - 반환값: 생성 작업의 ID (나중에 상태 확인용)
    suspend fun createSlideshow(groupId: String, request: CreateSlideshowRequest): CreateSlideshowResponse

    // 7.2 슬라이드쇼 상세 정보 조회
    suspend fun getSlideshowDetail(groupId: String, slideshowId: String): SlideshowDetailResponse

    // 7.4 슬라이드쇼 목록 조회 (제작 완료된 영상들)
    suspend fun getSlideshowList(groupId: String): List<SlideshowSummaryResponse>

    // 7.5 다운로드 링크 발급 (외부 공유용)
    suspend fun exportSlideshow(
        groupId: String,
        slideshowId: String,
        request: ExportSlideshowRequest
    ): ExportSlideshowResponse
}

/**
 * 🏗️ RetrofitSlideshowNetwork (구현체)
 */
internal class RetrofitSlideshowNetwork @Inject constructor(
    private val slideshowApi: RetrofitSlideshowApi
) : SlideshowNetworkDataSource {

    override suspend fun createSlideshow(groupId: String, request: CreateSlideshowRequest) =
        slideshowApi.createSlideshow(groupId, request)

    override suspend fun getSlideshowDetail(groupId: String, slideshowId: String) =
        slideshowApi.getSlideshowDetail(groupId, slideshowId)

    override suspend fun getSlideshowList(groupId: String) =
        slideshowApi.getSlideshowList(groupId)

    override suspend fun exportSlideshow(groupId: String, slideshowId: String, request: ExportSlideshowRequest) =
        slideshowApi.exportSlideshow(groupId, slideshowId, request)
}
