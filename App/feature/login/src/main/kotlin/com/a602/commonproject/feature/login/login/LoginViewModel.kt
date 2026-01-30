package com.a602.commonproject.feature.login.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.a602.commonproject.sync.status.SyncManager
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val syncManager: SyncManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    /**
     * 로그인 요청 처리
     * 1. 이메일/비밀번호 유효성 검사
     * 2. FCM 토큰 비동기 가져오기
     * 3. 서버 로그인 API 호출
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { LoginUiState.Error("이메일과 비밀번호를 입력해주세요.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { LoginUiState.Loading }

            // 실제 기기의 FCM 토큰을 가져옵니다.
            val fcmToken = fetchFcmToken()

            // 로그인 시도 (이메일, 비밀번호, FCM 토큰 전송)
            userRepository.login(email, password, fcmToken)
                .onSuccess {
                    syncManager.requestSync()
                    _uiState.update { LoginUiState.Success }
                }
                .onFailure { e ->
                    _uiState.update { LoginUiState.Error(e.message ?: "로그인에 실패했습니다.") }
                }
        }
    }

    /**
     * Firebase Messaging Service를 이용하여 현재 기기의 고유 FCM 토큰을 비동기적으로 가져옵니다.
     * 이 토큰은 서버가 특정 기기(사용자)에게 푸시 알림을 보낼 때 식별자로 사용됩니다.
     *
     * @return 성공 시 FCM 토큰 문자열, 실패 시 더미 토큰 반환
     */
    private suspend fun fetchFcmToken(): String = suspendCoroutine { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                // 토큰 가져오기 실패 시 로그 출력 혹은 기본값 사용
                // 실제 배포 환경에서는 Retry 로직이나 에러 처리가 필요할 수 있습니다.
                continuation.resume("dummy_token_failed_fetch_login") 
                return@addOnCompleteListener
            }
            // 성공적으로 토큰을 가져온 경우
            val token = task.result
            continuation.resume(token ?: "dummy_token_null_login")
        }
    }

    fun clearError() {
        _uiState.update { LoginUiState.Idle }
    }
}

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}
