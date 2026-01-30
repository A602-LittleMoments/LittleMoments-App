package com.a602.commonproject.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 프로필 편집 화면에 필요한 모든 UI 상태를 담는 데이터 클래스입니다.
 */
data class ProfileEditUiState(
    val initialNickname: String = "",
    val nickname: String = "",
    val email: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    init {
        // 뷰모델 생성 시, 현재 로그인된 사용자의 정보로 초기 상태를 설정합니다.
        viewModelScope.launch {
            val authState = userRepository.authState.first() // 현재 상태를 한 번만 가져옵니다.
            if (authState is AuthState.LoggedIn) {
                _uiState.value = ProfileEditUiState(
                    initialNickname = authState.user.nickname,
                    nickname = authState.user.nickname,
                    email = authState.user.email
                )
            }
        }
    }

    // --- UI 이벤트 처리 함수들 ---

    fun onNicknameChanged(newNickname: String) {
        _uiState.update { it.copy(nickname = newNickname) }
    }

    fun onCurrentPasswordChanged(password: String) {
        _uiState.update { it.copy(currentPassword = password) }
    }

    fun onNewPasswordChanged(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun onConfirmNewPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmNewPassword = password) }
    }

    /**
     * 프로필 변경사항을 저장하는 메인 로직입니다.
     */
    fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val currentState = _uiState.value
            var isSuccess = true

            // 1. 닉네임이 변경되었으면 프로필 업데이트 API를 호출합니다.
            if (currentState.nickname != currentState.initialNickname) {
                val profileResult = userRepository.updateProfile(
                    nickname = currentState.nickname,
                    profileImageUrl = null // 이미지 변경은 이 화면에서 다루지 않습니다.
                )
                if (profileResult.isFailure) {
                    _uiState.update { it.copy(errorMessage = "닉네임 변경에 실패했습니다.") }
                    isSuccess = false
                }
            }

            // 2. 새 비밀번호를 입력했으면 비밀번호 변경 API를 호출합니다.
            if (currentState.newPassword.isNotEmpty() && isSuccess) {
                if (currentState.newPassword != currentState.confirmNewPassword) {
                    _uiState.update { it.copy(errorMessage = "새 비밀번호가 일치하지 않습니다.") }
                    isSuccess = false
                } else {
                    val passwordResult = userRepository.changePassword(
                        current = currentState.currentPassword,
                        new = currentState.newPassword,
                        confirm = currentState.confirmNewPassword
                    )
                    if (passwordResult.isFailure) {
                        _uiState.update { it.copy(errorMessage = "비밀번호 변경에 실패했습니다. 현재 비밀번호를 확인해주세요.") }
                        isSuccess = false
                    }
                }
            }

            // 모든 작업의 최종 결과를 UI 상태에 반영합니다.
            _uiState.update { it.copy(isLoading = false, isSaveSuccess = isSuccess) }
        }
    }

    /**
     * 화면 이동 후, 성공 상태를 다시 리셋하기 위한 함수입니다.
     */
    fun onSaveSuccessConsumed() {
        _uiState.update { it.copy(isSaveSuccess = false) }
    }
}
