package com.a602.commonproject.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.feature.home.navigation.MemoryNavArgsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemoryGridUiState(
    val isLoading: Boolean = false,
    val keywordId: String? = null,
    val medias: List<SharedMedia> = emptyList(),
    val errorMessage: String? = null,
)

@HiltViewModel
class MemoryGridViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
) : ViewModel() {

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
}
