package com.a602.commonproject.feature.gallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class CalendarUiState(
    val medias: List<SharedMedia> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: SharedMediaRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val uiState: StateFlow<CalendarUiState> =
        repository.getSharedAlbumStream()
            .map { medias ->
                CalendarUiState(
                    medias = medias,
                    isLoading = false,
                    error = null
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CalendarUiState(isLoading = true)
            )

    fun refresh() {
        viewModelScope.launch {
            try {
                // groupId 필요
                val groupId = userRepository.getCurrentGroupId()
                if (groupId.isNullOrBlank()) return@launch

                val ok = repository.syncWithServer(groupId)
                if (!ok) {

                }
            } catch (e: Exception) {
                // errorFlow가 필요
            }
        }
    }
}


