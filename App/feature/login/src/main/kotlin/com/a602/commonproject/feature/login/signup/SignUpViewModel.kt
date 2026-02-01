package com.a602.commonproject.feature.login.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.data.repository.GroupRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.Baby
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.a602.commonproject.model.data.AuthState
import kotlinx.coroutines.flow.first

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val babyRepository: BabyRepository,
    private val groupRepository: GroupRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // "3번은 하지 말고" -> init 블록 로직 제거
    }


    // =================================================================
    // 1️⃣ User Info Step
    // =================================================================
    fun updateUserInfo(email: String, password: String, nickname: String, fcmToken: String) {
        _uiState.update { it.copy(email = email, password = password, nickname = nickname, fcmToken = fcmToken) }
    }




    // =================================================================
    // 🚀 EXECUTE: Finish & Submit
    // =================================================================
    // =================================================================
    // 🚀 EXECUTE: 1. Sign Up & Login
    // =================================================================
    /**
     * 회원가입 진행
     * 1. FCM 토큰 획득
     * 2. 회원가입 API 호출
     * 3. 자동 로그인 호출 (토큰 저장)
     * 4. 그룹 유무 확인 (그룹 없으면 다이얼로그 표시)
     */
    /**
     * 회원가입 프로세스 실행
     *
     * 1. [fetchFcmToken] : FCM 토큰을 비동기로 가져옵니다.
     * 2. [userRepository.signUp] : 서버에 회원가입을 요청합니다.
     * 3. [userRepository.login] : 가입 후 자동 로그인을 수행하여 인증 토큰을 확보합니다.
     * 4. [checkGroupStatus] : 로그인 성공 후, 사용자가 이미 그룹에 속해있는지 확인합니다.
     *    - 그룹이 없으면 [checkGroupStatus] 내부에서 [showGroupDialog] 상태를 true로 변경하여 그룹 생성/참여 다이얼로그를 띄웁니다.
     */
    fun signUp() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // 0. FCM 토큰 가져오기 (없으면 가져옴)
                val token = fetchFcmToken()
                _uiState.update { it.copy(fcmToken = token) }

                // 1. 회원가입 요청
                userRepository.signUp(
                    email = currentState.email,
                    password = currentState.password,
                    nickname = currentState.nickname,
                    fcmToken = token,
                ).onFailure { throw Exception("회원가입 실패: ${it.message}") }



                // 3. 그룹 가입 여부 확인
                // 그룹이 없으면 showGroupDialog = true가 되어 다이얼로그가 뜸
                checkGroupStatus()

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    /**
     * Firebase Cloud Messaging 토큰 비동기 획득 로직
     * 실패 시에도 앱 흐름이 끊기지 않도록 더미 토큰을 반환하도록 예외 처리가 되어 있습니다.
     */
    private suspend fun fetchFcmToken(): String = suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                continuation.resume("dummy_token_failed_fetch") // 실패 시 더미 혹은 에러 처리
                return@addOnCompleteListener
            }
            val token = task.result
            continuation.resume(token ?: "dummy_token_null")
        }
    }

    /**
     * 현재 사용자의 그룹 정보를 확인합니다.
     * 로그인 직후 호출되며, 사용자가 속한 그룹이 없다면 그룹 생성/참여 다이얼로그를 표시하도록 상태를 업데이트합니다.
     */
    private suspend fun checkGroupStatus() {
        try {
            val groupId = userRepository.getCurrentGroupId()
            if (groupId.isNullOrBlank()) {
                // 그룹 없음 -> 다이얼로그 표시 (로딩 끔)
                _uiState.update { it.copy(isLoading = false, showGroupDialog = true) }
            } else {
                // 그룹 있음 -> 성공 처리 (홈 이동)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }
        } catch (e: Exception) {
            // 그룹 정보 확인 실패 시 안전하게 다이얼로그 띄움 (사용자가 다시 시도하거나 그룹을 생성하도록 유도)
            _uiState.update { it.copy(isLoading = false, showGroupDialog = true) }
        }
    }

    // =================================================================
    // 🚀 EXECUTE: 2. Group Creation / Join
    // =================================================================

    // =================================================================
    // 🚀 EXECUTE: 2. Group Creation / Join
    // =================================================================

    /**
     * 그룹 생성 (1단계)
     * 성공 시 아기 등록 화면([showBabyForm] = true)으로 전환
     */
    fun createGroup(
        groupName: String,
        relation: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 1. 그룹 생성 요청
                groupRepository.createGroup(groupName, relation)
                    .onFailure { throw Exception("그룹 생성 실패: ${it.message}") }

                // 성공: 아기 등록 화면으로 이동
                _uiState.update { it.copy(isLoading = false, showBabyForm = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    /**
     * 아기 등록 (2단계)
     * 성공 시 메인 진입 ([isSuccess] = true)
     */
    fun addBaby(
        babyName: String,
        babyBirthDate: String,
        babyGender: Baby.Gender,
        babyImageFile: File?
    ) {
         viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 2. 아기 등록 요청
                babyRepository.addBaby(
                    name = babyName,
                    birthDate = babyBirthDate,
                    gender = babyGender,
                    imageFile = babyImageFile,
                ).onFailure { throw Exception("아기 등록 실패: ${it.message}") }

                // 성공: 메인으로 이동
                _uiState.update { it.copy(isSuccess = true, isLoading = false, showGroupDialog = false, showBabyForm = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    /**
     * 그룹 참여 (Dialog에서 호출)
     */
    fun joinGroup(inviteCode: String, relation: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // 그룹 참여 요청
                groupRepository.joinGroup(inviteCode, relation)
                    .onFailure { throw Exception("그룹 가입 실패: ${it.message}") }

                // 성공
                _uiState.update { it.copy(isSuccess = true, isLoading = false, showGroupDialog = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    fun checkLoggedInAndGroupStatus() {
        viewModelScope.launch {
            val authState = userRepository.authState.first()
            if (authState is AuthState.LoggedIn) {
                val groupId = userRepository.getCurrentGroupId()
                if (groupId.isNullOrBlank()) {
                    _uiState.update { it.copy(showGroupDialog = true) }
                }
            }
        }
    }
}

// =================================================================
// 🏗️ State Definitions
// =================================================================
data class SignUpUiState(
    // Status
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val showGroupDialog: Boolean = false, // 그룹 생성/참여 초기 화면 진입 여부
    val showBabyForm: Boolean = false,    // 그룹 생성 후 아기 등록 화면 표시 여부

    // User Info
    val email: String = "",
    val password: String = "",
    val nickname: String = "",
    val fcmToken: String = "dummy_token",
)
