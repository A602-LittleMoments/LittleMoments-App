package com.a602.commonproject.network.api

import com.a602.commonproject.network.model.AuthResponse
import com.a602.commonproject.network.model.ChangePasswordRequest
import com.a602.commonproject.network.model.LoginRequest
import com.a602.commonproject.network.model.TokenResponse
import com.a602.commonproject.network.model.UpdateProfileRequest
import com.a602.commonproject.network.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

internal interface RetrofitAuthApi {

    // 1.1 회원가입 (Multipart)
    @Headers("Auth: No")
    @Multipart
    @POST("auth/signup")
    suspend fun signUp(
        // 'data': JSON 문자열
        @Part("data") data: RequestBody,
        // 'profile_image': 이미지 파일 (선택 사항일 수 있으니 Nullable)
        @Part profileImage: MultipartBody.Part?,
    ): AuthResponse

    // 1.2 로그인
    @Headers("Auth: No")
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    // 1.2.1 로그아웃
    @POST("auth/logout")
    suspend fun logout()

    // 1.3 내 정보 조회
    @GET("users/me")
    suspend fun getMyProfile(): User

    // 1.3 내 정보 수정
    @PUT("users/me")
    suspend fun updateMyProfile(
        @Body request: UpdateProfileRequest
    ): User

    // 1.4 회원 탈퇴
    @DELETE("users/me")
    suspend fun withdraw()

    // 1.5 비밀번호 변경
    @PUT("users/me/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    )

    // 토큰 갱신
    @Headers("Auth: No")
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): TokenResponse
}
