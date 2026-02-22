package com.a602.commonproject.network.api

import com.a602.commonproject.network.model.BatchUploadResponse
import com.a602.commonproject.network.model.MediaDetailResponse
import com.a602.commonproject.network.model.MediaListResponse
import com.a602.commonproject.network.model.MediaUploadMetadataWrapper
import com.a602.commonproject.network.model.UpdateCaptionRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

internal interface RetrofitMediaApi {
    // 4.1 다중 업로드(선택된 사진/영상만 업로드)
    @Multipart
    @POST("groups/{groupId}/media/batch")
    suspend fun uploadMediaBatch(
        @Path("groupId") groupId: String,
        @Part files: List<MultipartBody.Part>,
        @Part("items") items: MediaUploadMetadataWrapper
    ): BatchUploadResponse

    // 4.3 미디어 리스트 조회
    // ✨ 변경: day 제거 -> startDate, endDate 추가
    // cursor: "20260126131824_media123" 형식의 문자열 (없으면 첫 페이지)
    @GET("groups/{groupId}/albums")
    suspend fun getAlbums(
        @Path("groupId") groupId: String,
        @Query("limit") limit: Int = 1000,
        @Query("cursor") cursor: String? = null,
        @Query("startDate") startDate: String? = null, // "YYYY-MM-DD"
        @Query("endDate") endDate: String? = null,     // "YYYY-MM-DD"
        @Query("babyId") babyId: String? = null,
        @Query("filterByUserId") filterByUserId: String? = null
    ): MediaListResponse

    // 4.4 미디어 상세 조회
    @GET("groups/{groupId}/media/{mediaId}")
    suspend fun getMediaDetail(
        @Path("groupId") groupId: String,
        @Path("mediaId") mediaId: String
    ): MediaDetailResponse

    // 4.5 캡션 수정
    @PUT("groups/{groupId}/media/{mediaId}/caption")
    suspend fun updateCaption(
        @Path("groupId") groupId: String,
        @Path("mediaId") mediaId: String,
        @Body request: UpdateCaptionRequest
    )

    // 4.6 삭제
    @DELETE("groups/{groupId}/media/{mediaId}")
    suspend fun deleteMedia(
        @Path("groupId") groupId: String,
        @Path("mediaId") mediaId: String
    )
}
