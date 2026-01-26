package com.a602.commonproject.network.api

import com.a602.commonproject.network.model.CreateSlideshowRequest
import com.a602.commonproject.network.model.CreateSlideshowResponse
import com.a602.commonproject.network.model.ExportSlideshowRequest
import com.a602.commonproject.network.model.ExportSlideshowResponse
import com.a602.commonproject.network.model.SlideshowDetailResponse
import com.a602.commonproject.network.model.SlideshowSummaryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface RetrofitSlideshowApi {
    // 7.1 슬라이드쇼 생성
    @POST("groups/{groupId}/slideshows")
    suspend fun createSlideshow(
        @Path("groupId") groupId: String,
        @Body request: CreateSlideshowRequest
    ): CreateSlideshowResponse

    // 7.2 슬라이드쇼 상세 조회 (ID 없음, 상세 정보)
    @GET("groups/{groupId}/slideshows/{slideshowId}")
    suspend fun getSlideshowDetail(
        @Path("groupId") groupId: String,
        @Path("slideshowId") slideshowId: String
    ): SlideshowDetailResponse

    // 7.4 슬라이드쇼 목록 조회 (ID 필수, 요약 정보)
    @GET("groups/{groupId}/slideshows")
    suspend fun getSlideshowList(
        @Path("groupId") groupId: String
    ): List<SlideshowSummaryResponse>

    // 7.5 다운로드 링크 발급
    @POST("groups/{groupId}/slideshows/{slideshowId}/export")
    suspend fun exportSlideshow(
        @Path("groupId") groupId: String,
        @Path("slideshowId") slideshowId: String,
        @Body request: ExportSlideshowRequest
    ): ExportSlideshowResponse
}
