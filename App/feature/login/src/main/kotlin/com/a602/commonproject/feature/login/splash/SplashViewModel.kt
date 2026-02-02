package com.a602.commonproject.feature.login.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // 인증 상태 방출 (로딩 즉시 처리)
    val uiState: StateFlow<SplashUiState> = userRepository.authState
        .map { authState ->
            // delay(1500) 제거: 로딩 완료되면 즉시 전환
            when (authState) {
                is AuthState.LoggedIn -> SplashUiState.NavigateToHome
                is AuthState.NotLoggedIn -> SplashUiState.NavigateToLogin
                is AuthState.Loading -> SplashUiState.Loading
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SplashUiState.Loading
        )
}

sealed interface SplashUiState {
    data object Loading : SplashUiState
    data object NavigateToHome : SplashUiState
    data object NavigateToLogin : SplashUiState
}
