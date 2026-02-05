package com.a602.commonproject.feature.album.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentEditUiState(
    val media: SharedMedia? = null,
    val caption: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CommentEditViewModel @Inject constructor(
    private val repository: SharedMediaRepository
) : ViewModel() {

    private val mediaIdFlow = MutableStateFlow<String?>(null)


    private val captionFlow = MutableStateFlow<String?>(null)

    private val savingFlow = MutableStateFlow(false)
    private val errorFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CommentEditUiState> =
        combine(
            repository.getSharedAlbumStream(),
            mediaIdFlow,
            captionFlow,
            savingFlow,
            errorFlow
        ) { medias, mediaId, caption, isSaving, error ->
            if (mediaId == null) {
                return@combine CommentEditUiState(
                    media = null,
                    caption = "",
                    isLoading = true,
                    isSaving = isSaving,
                    error = null
                )
            }

            val media = medias.firstOrNull { it.id == mediaId }

            when {
                media != null -> CommentEditUiState(
                    media = media,
                    caption = caption ?: (media.caption ?: ""),
                    isLoading = false,
                    isSaving = isSaving,
                    error = error
                )

                medias.isEmpty() -> CommentEditUiState(
                    media = null,
                    caption = caption ?: "",
                    isLoading = true,
                    isSaving = isSaving,
                    error = null
                )

                else -> CommentEditUiState(
                    media = null,
                    caption = caption ?: "",
                    isLoading = false,
                    isSaving = isSaving,
                    error = "미디어를 찾을 수 없음"
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CommentEditUiState(isLoading = true)
        )

    fun setMediaId(mediaId: String) {
        mediaIdFlow.value = mediaId
        captionFlow.value = null
        errorFlow.value = null
    }

    fun updateCaption(newCaption: String) {
        captionFlow.value = newCaption
    }

    fun saveCaption(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentMediaId = mediaIdFlow.value ?: return
        // uiState.value.caption은 이미 null 체크와 미디어 원본 캡션 처리가 되어있습니다.
        val currentCaption = uiState.value.caption

        viewModelScope.launch {
            savingFlow.value = true
            errorFlow.value = null

            val result = repository.updateCaption(currentMediaId, currentCaption)

            savingFlow.value = false

            result.onSuccess { onSuccess() }
                .onFailure { e ->
                    val msg = e.message ?: "저장 실패"
                    errorFlow.value = msg
                    onError(msg)
                }
        }
    }
    fun clearError() {
        errorFlow.value = null
    }
}
