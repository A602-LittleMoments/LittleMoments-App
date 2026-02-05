package com.a602.commonproject.data.repository.impl

import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.datastore.datastore.UserPreferencesDataSource
import com.a602.commonproject.model.data.AuthState
import com.a602.commonproject.model.data.User
import com.a602.commonproject.network.datasource.AuthNetworkDataSource
import com.a602.commonproject.network.datasource.GroupNetworkDataSource
import com.a602.commonproject.network.model.ChangePasswordRequest
import com.a602.commonproject.network.model.LoginRequest
import com.a602.commonproject.network.model.SignupRequest
import com.a602.commonproject.network.model.UpdateProfileRequest
import com.a602.commonproject.database.LMDatabase
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OfflineFirstUserRepository @Inject constructor(
    private val userPreferences: UserPreferencesDataSource,
    private val authDataSource: AuthNetworkDataSource,
    private val groupDataSource: GroupNetworkDataSource,
    private val database: LMDatabase
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

    // =================================================================
    // 2. 로그인 (순서: 로그인 -> 토큰 저장 -> 내 정보 조회 -> 그룹ID 저장)
    // =================================================================
    override suspend fun login(
        email: String,
        password: String,
        fcmToken: String,
    ): Result<Unit> {
        return try {
            // [STEP 1] 로그인 요청 -> 토큰 획득
            val loginResponse = authDataSource.login(LoginRequest(email, password, fcmToken))

            // [STEP 2] ✨ 토큰 먼저 저장 (중요!)
            // 이걸 먼저 해야 다음 API(getMyProfile) 호출 시 Interceptor가 헤더에 토큰을 넣을 수 있음
            userPreferences.setAuthData(
                accessToken = loginResponse.accessToken,
                refreshToken = loginResponse.refreshToken,
                email = loginResponse.user.email,
                nickname = loginResponse.user.nickname,
                profileImageUrl = loginResponse.user.profileImageUrl,
                groupId = null // 아직 모름
            )

            // [STEP 3] 그룹 정보 조회 (안전하게!)
            // 토큰이 저장되었으므로 Interceptor가 작동합니다.
            try {
                val groupResponse = groupDataSource.getMyGroup()

                userPreferences.setGroupId(id = groupResponse.groupId )// ✨ (필드명이 id인지 groupId인지 확인 필요))

            } catch (e: Exception) {
                // ⚠️ 그룹이 없거나 가져오기 실패해도 로그인은 성공으로 처리!
                // 그냥 groupId가 null인 상태로 앱에 진입하게 됨 (그룹 생성/가입 화면으로 유도)
                e.printStackTrace()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            // 실패 시 어설프게 저장된 데이터 삭제
            userPreferences.clear()
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

            // [STEP 1] 회원가입 요청 -> 토큰 획득
            val response = authDataSource.signUp(SignupRequest(email, password, nickname, fcmToken), imageFile)

            // [STEP 2] 토큰 및 유저 정보 먼저 저장
            // ✨ setAuthData에 groupId 파라미터가 추가되었으므로 값을 넘겨줘야 합니다.
            // (가입 직후라 아직 모르면 null)
            userPreferences.setAuthData(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                email = response.user.email,
                nickname = response.user.nickname,
                profileImageUrl = response.user.profileImageUrl,
                groupId = null // 👈 여기가 핵심! (그룹 없음 상태로 시작)
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
            
            // Room 데이터베이스 전체 삭제 (다른 계정 로그인 시 기존 데이터 잔존 방지)
            // Main Thread 에서 실행하면 터지므로 IO 스레드로 변경
            withContext(Dispatchers.IO) {
                database.clearAllTables()
            }

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

            // [STEP 2] ✨ 그룹 정보도 최신으로 확인 (Group 도메인)
            // (혹시 다른 폰에서 그룹에 가입했을 수도 있으니까요)
            var currentGroupId: String? = null
            try {
                val groupResponse = groupDataSource.getMyGroup()
                currentGroupId = groupResponse.groupId
            } catch (e: Exception) {
                // 그룹이 없거나 탈퇴했을 수 있음 -> null 유지
            }

            // [STEP 3] 유저 정보 + 그룹 ID 함께 업데이트
            userPreferences.setUserData(
                email = userResponse.email,
                nickname = userResponse.nickname,
                profileImageUrl = userResponse.profileImageUrl,
            )

            if (currentGroupId != null) {
                userPreferences.setGroupId(currentGroupId)
            }

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
                UpdateProfileRequest(nickname, null),
                imageFile
            )

            // 3. 로컬 반영
            userPreferences.setUserData(
                email = null, // 이메일 변경 없음
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

    override suspend fun updateFcmToken(token: String): Result<Unit> {
        return try {
            // 1. 서버 API 호출 (AuthNetworkDataSource에 함수가 있어야 함)
            authDataSource.updateFcmToken(fcmTokenRequest = token)

            // 2. (선택) 필요하다면 로컬에도 저장할 수 있지만,
            // 보통은 서버에만 잘 가면 되므로 성공 리턴
            Result.success(Unit)
        } catch (e: Exception) {
            // 실패 시 호출부에서 로그 정도만 찍음
            Result.failure(e)
        }
    }


    // 앱 마켓 심사 필수 요소입니다.
    override suspend fun deleteAccount(): Result<Unit> {
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

    override suspend fun getCurrentGroupId(): String? {
        // userGroupId Flow의 가장 최신 값 하나만 가져옴 (동기적)
        return userPreferences.userGroupId.first()
    }
}
