package com.a602.commonproject.data.repository

import com.a602.commonproject.datastore.datastore.UserPreferencesDataSource
import com.a602.commonproject.model.data.AuthState
import com.a602.commonproject.model.data.User
import com.a602.commonproject.network.datasource.AuthNetworkDataSource
import com.a602.commonproject.network.model.ChangePasswordRequest
import com.a602.commonproject.network.model.LoginRequest
import com.a602.commonproject.network.model.SignupRequest
import com.a602.commonproject.network.model.UpdateProfileRequest
import java.io.File
import javax.inject.Inject
import kotlin.math.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class OfflineFirstUserRepository @Inject constructor(
    private val userPreferences: UserPreferencesDataSource,
    private val authDataSource: AuthNetworkDataSource,
) : UserRepository {

    // =================================================================
    // 1. 상태 관찰 (DataStore -> AuthState 변환)
    // =================================================================
    override val authState: Flow<AuthState> = combine(
        userPreferences.accessToken,
        userPreferences.userEmail,
        userPreferences.userNickname,
        userPreferences.userProfileImage,
    ) { token, email, nickname, profileImage ->
        if (token.isNullOrBlank()) {
            AuthState.NotLoggedIn
        } else {
            val validEmail = email ?: "unknown"
            val user = User(
                id = validEmail,
                email = validEmail,
                nickname = nickname ?: "알 수 없음",
                profileImageUrl = profileImage,
            )
            AuthState.LoggedIn(user)
        }
    }

    // 2. 로그인
    override suspend fun login(
        email: String,
        password: String,
        fcmToken: String,
    ): Result<Unit> {
        return try {
            // 실재로 로그인 하는 부분
            val response = authDataSource.login(LoginRequest(email, password, fcmToken))

            // datastore에 저장
            userPreferences.setAuthData(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                email = response.user.email,
                nickname = response.user.nickname,
                profileImageUrl = response.user.profileImageUrl,
            )
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. 회원가입
    override suspend fun signUp(
        email: String,
        password: String,
        nickname: String,
        fcmToken: String,
        profileImageUrl: String?,
    ): Result<Unit> {
        return try {
            val imageFile = profileImageUrl?.let { File(it) }
            // 실재로 로그인 하는 부분
            val response = authDataSource.signUp(SignupRequest(email, password, nickname, fcmToken), imageFile)

            // datastore에 저장
            userPreferences.setAuthData(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                email = response.user.email,
                nickname = response.user.nickname,
                profileImageUrl = response.user.profileImageUrl,
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // 로그 아웃
    override suspend fun logout(): Result<Unit> {
        return try {
            // dataStore 초기화
            userPreferences.clear()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 유저 정보 리프레시 라프레시 가져오기
    override suspend fun refreshUserInfo(): Result<Unit> {
        return try {
            // 내 정보 받아 오기
            val userResponse = authDataSource.getMyProfile()

            // ✨ 토큰 제외하고 유저 정보만 업데이트
            userPreferences.setUserData(
                email = userResponse.email,
                nickname = userResponse.nickname,
                profileImageUrl = userResponse.profileImageUrl,
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✨ 4-1. 회원 탈퇴 (Point 3: 추가됨)
    // 앱 마켓 심사 필수 요소입니다.
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            // 1. 서버에 계정 삭제 요청
            authDataSource.withdraw()

            // 2. 성공 시 로컬 데이터 삭제 (로그아웃 처리)
            logout()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 프로필 업데이트
    override suspend fun updateProfile(
        nickname: String,
        profileImageUrl: String?,
    ): Result<Unit> {
        return try {
            // 사진 변경 되는지 확인
            val imageFile = if (profileImageUrl != null && !profileImageUrl.startsWith("http")) {
                File(profileImageUrl)
            } else {
                null
            }

            // ✨ 토큰 제외하고 유저 정보만 업데이트
            val response = authDataSource.updateMyProfile(
                UpdateProfileRequest(
                    nickname,
                    profileImageUrl,
                ),imageFile
            )

            //로컬 반영 (이메일은 유지)
            userPreferences.setUserData(
                email = null,
                nickname = response.nickname,
                profileImageUrl = response.profileImageUrl,
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 비밀번호 변경
    override suspend fun changePassword(
        current: String,
        new: String,
        confirm: String,
    ): Result<Unit> {
        return try {
            // 새로운 비밀번호 요청 생성
            val request = ChangePasswordRequest(current,new,confirm)
            // 서버 비밀번호 변경
            authDataSource.changePassword(request)
            // 로그 아웃 -> 토큰 재발행 됨
            logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun renewSession(): Result<Unit> {
        return try {
            val currentRefreshToken = userPreferences.refreshToken.first()

            if(currentRefreshToken.isNullOrBlank()){
                // 리프레시 토큰이 없으면 갱신 불가능 -> 실패 처리 (로그인 화면으로 튕기게 됨)
                logout()
                return Result.failure(IllegalStateException("No refresh token available"))
            }
            // access Token 갱신 요청
            val response = authDataSource.refreshToken(currentRefreshToken)

            // 새로 받은 Access Token 갈아 끼우기
            userPreferences.updateAccessToken(response.accessToken)

            Result.success(Unit)
        } catch (e: Exception) {
            logout()
            Result.failure(e)
        }
    }
}
