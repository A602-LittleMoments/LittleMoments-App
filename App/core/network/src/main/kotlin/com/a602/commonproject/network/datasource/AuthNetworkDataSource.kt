package com.a602.commonproject.network.datasource

import com.a602.commonproject.network.api.RetrofitAuthApi
import com.a602.commonproject.network.model.AuthResponse
import com.a602.commonproject.network.model.ChangePasswordRequest
import com.a602.commonproject.network.model.FcmTokenRequest
import com.a602.commonproject.network.model.LoginRequest
import com.a602.commonproject.network.model.SignupRequest
import com.a602.commonproject.network.model.TokenResponse
import com.a602.commonproject.network.model.UpdateProfileRequest
import com.a602.commonproject.network.model.UserResponse
import com.a602.commonproject.network.util.toJsonRequestBody
import com.a602.commonproject.network.util.toMultipartPart
import java.io.File
import javax.inject.Inject
import kotlinx.serialization.json.Json


/**
 * 🔐 AuthNetworkDataSource 인터페이스
 *
 * Repository(데이터 계층)가 네트워크 모듈을 사용할 때 바라보는 명세서입니다.
 * Retrofit의 기술적인 디테일(Call, Response 등)을 숨기고,
 * 순수한 데이터 결과값(AuthResponse, NetworkUser 등)만 반환하도록 정의합니다.
 */
interface AuthNetworkDataSource {
    suspend fun signUp(signupRequest: SignupRequest, imageFile: File?): AuthResponse
    suspend fun login(loginRequest: LoginRequest): AuthResponse
    suspend fun logout()
    suspend fun getMyProfile(): UserResponse
    suspend fun updateMyProfile(request: UpdateProfileRequest, imageFile: File?): UserResponse
    suspend fun withdraw()
    suspend fun changePassword(request: ChangePasswordRequest)
    suspend fun refreshToken(refreshToken: String): TokenResponse
    suspend fun updateFcmToken(fcmTokenRequest: String)
}

/**
 * 🏭 RetrofitAuthNetwork (구현체)
 *
 * 실제 Retrofit API(전화기)를 사용하여 서버와 통신하는 작업반장입니다.
 * @Inject를 통해 Hilt로부터 필요한 도구(Json 변환기, Retrofit API)를 주입받습니다.
 */
internal class RetrofitAuthNetwork @Inject constructor(
    private val networkJson: Json,
    private val authApi: RetrofitAuthApi,
) : AuthNetworkDataSource {

    override suspend fun signUp(signupRequest: SignupRequest, imageFile: File?): AuthResponse {
        // 객체를 JSON RequestBody로 반환
        // val dataPart = signupRequest.toJsonRequestBody(networkJson) -> ConverterFactory를 이용해서 자동으로 JSON으로 바꿔주는 기능 추가
        // 프로필 이미지 파일이 있다면 MultipartBody.Part로 변환합니다.
        val imagePart= imageFile?.toMultipartPart("profile_image")
        // 조립된 두 조각(데이터, 이미지)을 서버로 전송합니다.
        return authApi.signUp(signupRequest, imagePart)
    }

    override suspend fun login(loginRequest: LoginRequest) = authApi.login(loginRequest)
    override suspend fun logout() = authApi.logout()
    override suspend fun getMyProfile() = authApi.getMyProfile()
    override suspend fun updateMyProfile(request: UpdateProfileRequest, imageFile: File?): UserResponse {
        // val dataPart = request.toJsonRequestBody(networkJson)
        val imagePart = imageFile?.toMultipartPart("profile_image")
        return authApi.updateMyProfile(request, imagePart)
    }

    override suspend fun withdraw() = authApi.withdraw() // 탈퇴
    override suspend fun changePassword(request: ChangePasswordRequest) = authApi.changePassword(request)
    override suspend fun refreshToken(refreshToken: String) = authApi.refreshToken(refreshToken)
    override suspend fun updateFcmToken(fcmTokenRequest: String) = authApi.updateFcmToken(FcmTokenRequest(fcmTokenRequest))
}
