package com.a602.commonproject.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.NotificationRepository
import com.a602.commonproject.database.model.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    notificationRepository: NotificationRepository
) : ViewModel() {

    val uiState: StateFlow<NotificationUiState> = notificationRepository.getNotifications()
        .map { notifications ->
            if (notifications.isEmpty()) {
                NotificationUiState.Empty
            } else {
                NotificationUiState.Success(notifications)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotificationUiState.Loading
        )
}

sealed interface NotificationUiState {
    data object Loading : NotificationUiState
    data object Empty : NotificationUiState
    data class Success(val notifications: List<NotificationEntity>) : NotificationUiState
}
