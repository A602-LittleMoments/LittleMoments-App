package com.a602.commonproject.feature.album.viewmodel


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

data class HighlightCalendarUiState(
    val selectedStartMillis: Long? = null,
    val selectedEndMillis: Long? = null,
    val isDateRangeValid: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val createdSlideshowId: String? = null // 생성 성공 시 ID
)

@HiltViewModel
class HighlightCalendarViewModel @Inject constructor(
    private val slideshowRepository: SlideshowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HighlightCalendarUiState())
    val uiState: StateFlow<HighlightCalendarUiState> = _uiState.asStateFlow()

    fun selectDateRange(startMillis: Long, endMillis: Long) {
        _uiState.value = _uiState.value.copy(
            selectedStartMillis = startMillis,
            selectedEndMillis = endMillis,
            isDateRangeValid = startMillis <= endMillis
        )
    }

    fun clearSelection() {
        _uiState.value = HighlightCalendarUiState()
    }

    /**
     * 하이라이트 생성 API 호출
     */
    fun createHighlight(
        startMillis: Long,
        endMillis: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Long → ISO 8601 String 변환
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

                slideshowRepository.createSlideshow(request)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        onSuccess()
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "하이라이트 생성 실패"
                        )
                        onFailure(error.message ?: "하이라이트 생성 실패")
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "하이라이트 생성 실패"
                )
                onFailure(e.message ?: "하이라이트 생성 실패")
            }
        }
    }
}
