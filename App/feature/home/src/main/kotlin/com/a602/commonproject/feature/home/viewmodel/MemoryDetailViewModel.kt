package com.a602.commonproject.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.feature.home.navigation.MemoryNavArgsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class MemoryDetailUiState(
    val isLoading: Boolean = false,
    val mediaId: String? = null,
    val media: SharedMedia? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class MemoryDetailViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(MemoryDetailUiState(isLoading = true))
    val uiState: StateFlow<MemoryDetailUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                MemoryNavArgsStore.keywordId,
                MemoryNavArgsStore.mediaId
            ) { keywordId, mediaId ->
                keywordId to mediaId
            }.collectLatest { (keywordId, mediaId) ->
                if (keywordId == null || mediaId == null) return@collectLatest

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    mediaId = mediaId,
                    errorMessage = null
                )

                collectionRepository.getCollectionDetail(keywordId)
                    .onSuccess { medias ->
                        val found = medias.firstOrNull { it.id == mediaId }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            media = found,
                            errorMessage = if (found == null) "media not found" else null
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

    fun onDelete() {}
    fun onDownload() {}
    fun onEdit() {}
}

