package com.a602.commonproject.network.model


import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable


// 공통 User 객체 (로그인, 회원가입, 내 정보 조회 등에서 재사용)
@InternalSerializationApi
@Serializable
data class User(
    val email: String,
    val nickname: String,
    val profileImageUrl: String? = null
)

// 1.1 회원가입 (Multipart 내의 'data' 파트에 들어갈 JSON)
@InternalSerializationApi
@Serializable
data class SignupRequest(
    val email: String,
    val password: String,
    val nickname: String,
    val fcmToken: String
)

// 1.2 로그인 요청
@InternalSerializationApi
@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val fcmToken: String
)

// 1.1, 1.2 로그인/회원가입 공통 응답
@InternalSerializationApi
@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

// 토큰 갱신 응답
@InternalSerializationApi
@Serializable
data class TokenResponse(
    val accessToken: String
)

// 1.3 내 정보 수정 요청
@InternalSerializationApi
@Serializable
data class UpdateProfileRequest(
    val email: String,
    val nickname: String,
    val profileImageUrl: String?
)

// 1.5 비밀번호 변경 요청
@InternalSerializationApi
@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val newPasswordConfirm: String
)
