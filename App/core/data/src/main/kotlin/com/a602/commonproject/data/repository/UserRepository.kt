package com.a602.commonproject.data.repository

import com.a602.commonproject.model.data.AuthState
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /**
     * 🕵️‍♀️ 현재 로그인 상태를 실시간으로 관찰
     * (DataStore의 토큰 유무에 따라 LoggedIn / NotLoggedIn 방출)
     */
    val authState: Flow<AuthState>

    /**
     * 🔑 로그인
     * 성공 시 DataStore에 토큰 저장 -> authState가 자동으로 LoggedIn으로 변경됨
     */
    suspend fun login(email: String, password: String, fcmToken: String): Result<Unit>

    /**
     * 📝 회원가입
     * 성공 시 바로 로그인 처리까지 진행 (토큰 저장)
     */
    suspend fun signUp(email: String, password: String, nickname: String, fcmToken: String, profileImageUrl: String? = null ): Result<Unit>

    /**
     * 🚪 로그아웃
     * DataStore의 토큰 삭제 -> authState가 자동으로 NotLoggedIn으로 변경됨
     */
    suspend fun logout(): Result<Unit>

    /**
     * 🔄 내 정보 최신화 (새로고침)
     * 서버에서 최신 유저 정보를 받아와 DataStore 업데이트
     */
    suspend fun refreshUserInfo(): Result<Unit>

    /**
     * 🖼️ 프로필 수정
     */
    suspend fun updateProfile(nickname: String, profileImageUrl: String?): Result<Unit>

    /**
     * 🔐 비밀번호 변경
     * - 현재 비번, 새 비번, 새 비번 확인을 보냅니다.
     */
    suspend fun changePassword(
        current: String,
        new: String,
        confirm: String
    ): Result<Unit>

    /**
     * 🔄 토큰 갱신 (Session Renewal)
     * - 저장된 RefreshToken을 사용해 새로운 AccessToken을 발급받고 저장합니다.
     * - 앱 시작 시 호출하거나, 401 에러 발생 시 Authenticator에서 호출될 수 있습니다.
     */
    suspend fun renewSession(): Result<Unit>

    /**
     * ✨ [추가] FCM 기기 토큰 갱신
     * - FirebaseService의 onNewToken()에서 호출됩니다.
     * - 앱 삭제 후 재설치하거나, 토큰이 만료되었을 때 서버에 새 주소를 알려줍니다.
     */
    suspend fun updateFcmToken(token: String): Result<Unit>

    // ✨ [추가] 회원 탈퇴 (이것도 있으면 좋습니다)
    suspend fun deleteAccount(): Result<Unit>

    suspend fun getCurrentGroupId(): String?
}

