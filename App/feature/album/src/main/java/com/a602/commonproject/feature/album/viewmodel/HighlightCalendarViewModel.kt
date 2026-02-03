package com.a602.commonproject.feature.album.viewmodel


import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class HighlightCalendarUiState(
    val selectedStartMillis: Long? = null,
    val selectedEndMillis: Long? = null,
    val isDateRangeValid: Boolean = false
)

@HiltViewModel
class HighlightCalendarViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HighlightCalendarUiState())
    val uiState: StateFlow<HighlightCalendarUiState> = _uiState.asStateFlow()

    fun selectDateRange(startMillis: Long, endMillis: Long) {
        _uiState.value = HighlightCalendarUiState(
            selectedStartMillis = startMillis,
            selectedEndMillis = endMillis,
            isDateRangeValid = startMillis <= endMillis
        )
    }

    fun clearSelection() {
        _uiState.value = HighlightCalendarUiState()
    }
}
