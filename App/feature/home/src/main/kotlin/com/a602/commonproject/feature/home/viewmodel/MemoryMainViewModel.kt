package com.a602.commonproject.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.model.data.Collection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.serialization.Serializable


@HiltViewModel
class MemoryMainViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MemoryMainUiState>(MemoryMainUiState.Loading)
    val uiState: StateFlow<MemoryMainUiState> = _uiState

    private val _sideEffect = MutableSharedFlow<MemoryMainSideEffect>()
    val sideEffect: SharedFlow<MemoryMainSideEffect> = _sideEffect.asSharedFlow()

    init {
        refreshCollections()
    }

    fun refreshCollections() {
        viewModelScope.launch {
            // [Fix] Clear existing data to prevent showing stale planets from previous user
            _uiState.value = MemoryMainUiState.Loading

            collectionRepository.getCollections(limit = 7)
                .onSuccess { list ->
                    if (list.isEmpty()) {
                        _uiState.value = MemoryMainUiState.Empty
                    } else {
                        _uiState.value = MemoryMainUiState.Main(list)
                    }
                }
                .onFailure {
                    _uiState.value = MemoryMainUiState.Main(emptyList()) 
                }
        }
    }

    fun onCameraAction(isPhoto: Boolean) {
        viewModelScope.launch {
            _sideEffect.emit(MemoryMainSideEffect.NavigateToCamera(isPhoto))
        }
    }

    fun onSlideshowAction(request: SlideshowRequest) {
        viewModelScope.launch {
            _sideEffect.emit(MemoryMainSideEffect.NavigateToSlideshow(request))
        }
    }
}

sealed interface MemoryMainUiState {
    data object Loading : MemoryMainUiState
    data object Empty : MemoryMainUiState
    data class Main(val collections: List<Collection>) : MemoryMainUiState
    data class Error(val message: String) : MemoryMainUiState
}


sealed interface MemoryMainSideEffect {
    data class NavigateToCamera(val isPhoto: Boolean) : MemoryMainSideEffect
    data class NavigateToSlideshow(val request: SlideshowRequest) : MemoryMainSideEffect
}

@Serializable
sealed interface SlideshowRequest {
    @Serializable
    data class ByKeyword(val keyword: String, val label: String) : SlideshowRequest
    @Serializable
    data class ByDateRange(val startDate: Long, val endDate: Long, val label: String) : SlideshowRequest
}
