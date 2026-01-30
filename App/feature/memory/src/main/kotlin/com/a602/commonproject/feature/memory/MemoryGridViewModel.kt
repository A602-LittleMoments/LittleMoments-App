package com.a602.commonproject.feature.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.feature.memory.data.MemoryRepoProvider
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.feature.memory.navigation.MemoryGridKey
import com.a602.commonproject.feature.memory.navigation.MemoryNavArgsStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MemoryGridUiState(
    val isLoading: Boolean = false,
    val keywordId: String? = null,
    val medias: List<SharedMedia> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel(assistedFactory = MemoryGridViewModel.Factory::class)
class MemoryGridViewModel @AssistedInject constructor(
    @Assisted val key: MemoryGridKey,
) : ViewModel() {

    private val collectionRepository =
        MemoryRepoProvider.collectionRepository

    private val _uiState = MutableStateFlow(MemoryGridUiState())
    val uiState: StateFlow<MemoryGridUiState> = _uiState

    init {
        viewModelScope.launch {
            MemoryNavArgsStore.keywordId.collect { keywordId ->
                if (keywordId == null) return@collect

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    keywordId = keywordId,
                    errorMessage = null
                )

                collectionRepository.getCollectionDetail(keywordId)
                    .onSuccess { medias ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            medias = medias,
                            errorMessage = null
                        )
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(key: MemoryGridKey): MemoryGridViewModel
    }
}
