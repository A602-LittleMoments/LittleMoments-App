package com.a602.commonproject.feature.mypage.viewmodel

import android.util.Log
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
    val profileImageUrl: String? = null,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    // 비밀번호 보이기/숨기기 상태
    val isCurrentPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val isPasswordChanged: Boolean = false,
    val showLogoutConfirmDialog: Boolean = false,
    val isLogoutSuccess: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val isDeleteSuccess: Boolean = false,
    // 필드별 오류 메시지
    val nicknameError: String? = null,
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
)

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val authState = userRepository.authState.first()
            if (authState is AuthState.LoggedIn) {
                _uiState.value = ProfileEditUiState(
                    initialNickname = authState.user.nickname,
                    nickname = authState.user.nickname,
                    email = authState.user.email,
                    profileImageUrl = authState.user.profileImageUrl
                )
            }
        }
    }

    fun onNicknameChanged(newNickname: String) {
        _uiState.update { it.copy(nickname = newNickname, nicknameError = null) }
    }

    fun onCurrentPasswordChanged(password: String) {
        _uiState.update { it.copy(currentPassword = password, currentPasswordError = null) }
    }

    fun onNewPasswordChanged(password: String) {
        _uiState.update { it.copy(newPassword = password, newPasswordError = null) }
    }

    fun onConfirmNewPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmNewPassword = password, newPasswordError = null) } // 새 비밀번호 확인도 newPasswordError를 초기화
    }

    fun onToggleCurrentPasswordVisibility() { _uiState.update { it.copy(isCurrentPasswordVisible = !it.isCurrentPasswordVisible) } }
    fun onToggleNewPasswordVisibility() { _uiState.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) } }
    fun onToggleConfirmPasswordVisibility() { _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) } }
    fun onSaveSuccessConsumed() { _uiState.update { it.copy(isSaveSuccess = false) } }
    fun onPasswordChangedConsumed() { _uiState.update { it.copy(isPasswordChanged = false) } }

    // --- 로그아웃 관련 ---
    fun onLogoutClicked() { _uiState.update { it.copy(showLogoutConfirmDialog = true) } }
    fun onLogoutDismissed() { _uiState.update { it.copy(showLogoutConfirmDialog = false) } }
    fun onLogoutSuccessConsumed() { _uiState.update { it.copy(isLogoutSuccess = false) } }
    fun onDeleteAccountClicked() { _uiState.update { it.copy(showDeleteConfirmDialog = true) } }
    fun onDeleteAccountDismissed() { _uiState.update { it.copy(showDeleteConfirmDialog = false) } }
    fun onDeleteSuccessConsumed() { _uiState.update { it.copy(isDeleteSuccess = false) } }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.update { it.copy(showLogoutConfirmDialog = false, isLogoutSuccess = true) }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val result = userRepository.deleteAccount()
            if (result.isSuccess) {
                _uiState.update { it.copy(showDeleteConfirmDialog = false, isDeleteSuccess = true) }
            } else {
                // 이제 errorMessage 대신 snackbar를 사용하거나 다른 방식으로 처리
            }
        }
    }

    // --- 프로필 저장 로직 ---
    fun saveProfile() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val isNicknameChanged = currentState.nickname != currentState.initialNickname
            val isPasswordChangeAttempted = currentState.newPassword.isNotEmpty()

            if (!isNicknameChanged && !isPasswordChangeAttempted) {
                // 변경된 내용이 없을 때 처리 (예: 스낵바 메시지)
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            // 1. 닉네임 변경 처리 (이미지 URL과 함께 전송)
            if (isNicknameChanged) {
                val profileResult = userRepository.updateProfile(nickname = currentState.nickname, profileImageUrl = currentState.profileImageUrl)
                if (profileResult.isFailure) {
                    _uiState.update { it.copy(isLoading = false, nicknameError = "닉네임 변경에 실패했습니다.") }
                    return@launch
                }
            }

            // 2. 비밀번호 변경 처리
            if (isPasswordChangeAttempted) {
                if (currentState.newPassword != currentState.confirmNewPassword) {
                    _uiState.update { it.copy(isLoading = false, newPasswordError = "새 비밀번호가 일치하지 않습니다.") }
                    return@launch
                }

                val passwordResult = userRepository.changePassword(current = currentState.currentPassword, new = currentState.newPassword, confirm = currentState.confirmNewPassword)
                if (passwordResult.isSuccess) {
                    // 비밀번호 변경 성공 시, 재로그인 필요 상태로 변경
                    _uiState.update { it.copy(isLoading = false, isPasswordChanged = true) }
                    return@launch
                } else {
                    _uiState.update { it.copy(isLoading = false, currentPasswordError = "현재 비밀번호를 확인해주세요.") }
                    return@launch
                }
            }

            // 닉네임만 변경된 경우
            _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
        }
    }
}
