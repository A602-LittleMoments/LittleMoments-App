package com.a602.commonproject.feature.memory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.model.data.Collection
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.feature.memory.data.MemoryRepoProvider
import com.a602.commonproject.feature.memory.navigation.MemoryMainKey

private const val MAKE_SCREEN_MS = 5000L

sealed interface MemoryMainUiState {
    data object Loading : MemoryMainUiState
    data object Empty : MemoryMainUiState
    data class Make(val collections: List<Collection>) : MemoryMainUiState
    data class Main(val collections: List<Collection>) : MemoryMainUiState
    data class Error(val message: String) : MemoryMainUiState
}

@HiltViewModel(assistedFactory = MemoryMainViewModel.Factory::class)
class MemoryMainViewModel @AssistedInject constructor(
    @Assisted val key: MemoryMainKey,
) : ViewModel() {

    private val collectionRepository =
        MemoryRepoProvider.collectionRepository

    private val _uiState =
        MutableStateFlow<MemoryMainUiState>(MemoryMainUiState.Loading)
    val uiState: StateFlow<MemoryMainUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = MemoryMainUiState.Loading

            collectionRepository.getCollections()
                .onSuccess { list ->
                    if (list.isEmpty()) {
                        _uiState.value = MemoryMainUiState.Empty
                    } else {
                        _uiState.value = MemoryMainUiState.Make(list)
                        delay(5_000)
                        _uiState.value = MemoryMainUiState.Main(list)
                    }
                }
                .onFailure {
                    _uiState.value =
                        MemoryMainUiState.Error(it.message ?: "error")
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: MemoryMainKey): MemoryMainViewModel
    }
}
