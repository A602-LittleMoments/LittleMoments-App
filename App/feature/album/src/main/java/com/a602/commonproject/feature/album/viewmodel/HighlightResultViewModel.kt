
package com.a602.commonproject.feature.album.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.SlideshowRepository
import com.a602.commonproject.model.data.Slideshow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HighlightResultUiState(
    val slideshow: Slideshow? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
@HiltViewModel
class HighlightResultViewModel @Inject constructor(
    private val repository: SlideshowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HighlightResultUiState())
    val uiState: StateFlow<HighlightResultUiState> = _uiState.asStateFlow()

    fun observeSlideshow(slideshowId: String) {
        viewModelScope.launch {
            repository.getSlideshowsStream().collect { slideshows ->
                val slideshow = slideshows.find { it.id == slideshowId }
                _uiState.value = _uiState.value.copy(
                    slideshow = slideshow,
                    isLoading = false,
                    errorMessage = if (slideshow == null) "슬라이드쇼를 찾을 수 없습니다" else null
                )
            }
        }
    }

    fun refreshSlideshow(slideshowId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.syncSlideshowDetail(slideshowId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "새로고침 실패: ${error.message}"
                    )
                }
        }
    }

    fun downloadSlideshow(slideshowId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.downloadSlideshow(slideshowId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onFailure(error.message ?: "다운로드 실패")
                }
        }
    }

    fun deleteSlideshow(slideshowId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.deleteSlideshow(slideshowId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onFailure(error.message ?: "삭제 실패")
                }
        }
    }
}
