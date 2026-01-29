package com.a602.commonproject.network.api

import com.a602.commonproject.network.model.BabyResponse
import com.a602.commonproject.network.model.BabyListResponse
import com.a602.commonproject.network.model.BabyRequest
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

internal interface RetrofitBabyApi {
    // 3.1 아기 등록 (Multipart)
    @Multipart
    @POST("groups/{groupId}/babies")
    suspend fun addBaby(
        @Path("groupId") groupId: String,
        @Part("data") data: RequestBody,
        @Part babyPicture: MultipartBody.Part?
    ): BabyResponse // 👈 ✨ 반환 타입을 NetworkBaby로 변경!

    // 3.2 아기 목록 조회
    @GET("groups/{groupId}/babies")
    suspend fun getBabies(
        @Path("groupId") groupId: String
    ): BabyListResponse

    // 3.2 아기 정보 수정
    // ✨ [수정] 반환 타입 제거 (Response Body 없음)
    @Multipart
    @PUT("groups/{groupId}/babies/{babyId}")
    suspend fun updateBaby(
        @Path("groupId") groupId: String,
        @Path("babyId") babyId: String,
        @Part("data") data: RequestBody,
        @Part babyPicture: MultipartBody.Part?
    ) :BabyResponse

    // 3.2 아기 삭제
    @DELETE("groups/{groupId}/babies/{babyId}")
    suspend fun deleteBaby(
        @Path("groupId") groupId: String,
        @Path("babyId") babyId: String
    )

}
