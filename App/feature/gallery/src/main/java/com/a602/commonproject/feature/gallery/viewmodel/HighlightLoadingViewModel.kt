package com.a602.commonproject.feature.gallery.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.SlideshowRepository
import com.a602.commonproject.network.model.CreateSlideshowRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

data class HighlightLoadingUiState(
    val isLoading: Boolean = true,
    val slideshowId: String? = null,
    val error: String? = null
)

@HiltViewModel
class HighlightLoadingViewModel @Inject constructor(
    private val repository: SlideshowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HighlightLoadingUiState())
    val uiState: StateFlow<HighlightLoadingUiState> = _uiState.asStateFlow()

    fun createSlideshow(
        startMillis: Long,
        endMillis: Long,
        onSuccess: (String) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = HighlightLoadingUiState(isLoading = true)

            // 밀리초를 ISO 8601 날짜 문자열로 변환
            val startDate = Instant.ofEpochMilli(startMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .toString()

            val endDate = Instant.ofEpochMilli(endMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .toString()

            val request = CreateSlideshowRequest(
                projectType = "PERIOD",
                startDate = startDate,
                endDate = endDate,
                keywordId = null
            )

            repository.createSlideshow(request)
                .onSuccess {
                    // TODO:
                    // slideshowId를 얻을 수 없습니다.
                    // 현재는 임시로 빈 문자열 사용
                    val slideshowId = ""
                    _uiState.value = HighlightLoadingUiState(
                        isLoading = false,
                        slideshowId = slideshowId
                    )
                    onSuccess(slideshowId)
                }
                .onFailure { error ->
                    _uiState.value = HighlightLoadingUiState(
                        isLoading = false,
                        error = error.message
                    )
                    onFailure(error)
                }
        }
    }
}
