package com.a602.commonproject.network.datasource

import com.a602.commonproject.network.api.RetrofitAuthApi
import com.a602.commonproject.network.model.AuthResponse
import com.a602.commonproject.network.model.ChangePasswordRequest
import com.a602.commonproject.network.model.LoginRequest
import com.a602.commonproject.network.model.SignupRequest
import com.a602.commonproject.network.model.TokenResponse
import com.a602.commonproject.network.model.UpdateProfileRequest
import com.a602.commonproject.network.model.UserResponse
import java.io.File
import javax.inject.Inject
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


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
        // [Step 1] 회원가입 정보(DTO)를 JSON 문자열로 변환합니다.
        // 예: SignupRequest("test", "1234") -> '{"id":"test", "pw":"1234"}'
        val jsonString = networkJson.encodeToString(signupRequest)

        // [Step 2] JSON 문자열을 RequestBody로 포장합니다.
        // "이건 그냥 글자가 아니라 JSON 데이터야!"라고 명찰(ContentType)을 붙여줍니다.
        val dataPart = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        // [Step 3] 프로필 이미지 파일이 있다면 MultipartBody.Part로 변환합니다.
        // 파일이 없으면(null) 그냥 null을 보냅니다.
        val imagePart = imageFile?.let { file ->
            // 3-1. 파일을 읽을 수 있는 RequestBody로 변환 (타입: image/*)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

            // 3-2. 서버가 요구한 파라미터 이름("profile_image")과 파일명, 데이터를 합쳐서 파트 생성
            MultipartBody.Part.createFormData("profile_image", file.name, requestFile)
        }

        // [Step 4] 조립된 두 조각(데이터, 이미지)을 서버로 전송합니다.
        return authApi.signUp(dataPart, imagePart)
    }

    override suspend fun login(loginRequest: LoginRequest) = authApi.login(loginRequest)
    override suspend fun logout() = authApi.logout()
    override suspend fun getMyProfile() = authApi.getMyProfile()
    override suspend fun updateMyProfile(request: UpdateProfileRequest, imageFile: File?): UserResponse {
        // [Step 1] 회원가입 정보(DTO)를 JSON 문자열로 변환합니다.
        // 예: SignupRequest("test", "1234") -> '{"id":"test", "pw":"1234"}'
        val jsonString = networkJson.encodeToString(request)

        // [Step 2] JSON 문자열을 RequestBody로 포장합니다.
        // "이건 그냥 글자가 아니라 JSON 데이터야!"라고 명찰(ContentType)을 붙여줍니다.
        val dataPart = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        // [Step 3] 프로필 이미지 파일이 있다면 MultipartBody.Part로 변환합니다.
        // 파일이 없으면(null) 그냥 null을 보냅니다.
        val imagePart = imageFile?.let { file ->
            // 3-1. 파일을 읽을 수 있는 RequestBody로 변환 (타입: image/*)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

            // 3-2. 서버가 요구한 파라미터 이름("profile_image")과 파일명, 데이터를 합쳐서 파트 생성
            MultipartBody.Part.createFormData("profile_image", file.name, requestFile)
        }
        return authApi.updateMyProfile(dataPart, imagePart)
    }

    override suspend fun withdraw() = authApi.withdraw()
    override suspend fun changePassword(request: ChangePasswordRequest) = authApi.changePassword(request)
    override suspend fun refreshToken(refreshToken: String) = authApi.refreshToken(refreshToken)
}
