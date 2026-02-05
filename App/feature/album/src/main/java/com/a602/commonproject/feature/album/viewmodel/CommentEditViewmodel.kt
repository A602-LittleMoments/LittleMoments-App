package com.a602.commonproject.feature.album.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.data.repository.TempMediaRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.AuthState
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
    private val repository: SharedMediaRepository,
    private val tempRepository: TempMediaRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val mediaIdFlow = MutableStateFlow<String?>(null)
    private val isTempFlow = MutableStateFlow(false)
    private val captionFlow = MutableStateFlow<String?>(null)
    private val savingFlow = MutableStateFlow(false)
    private val errorFlow = MutableStateFlow<String?>(null)

    private val userNicknameFlow = userRepository.authState
        .map { state ->
            if (state is AuthState.LoggedIn) state.user.nickname else "나"
        }

    val uiState: StateFlow<CommentEditUiState> =
        combine(
            mediaIdFlow,
            isTempFlow
        ) { id, isTemp -> Pair(id, isTemp) }
            .flatMapLatest { (mediaId, isTemp) ->
                if (mediaId == null) {
                    flowOf(CommentEditUiState(isLoading = true))
                } else {
                    val mediaStream = if (isTemp) {
                        combine(
                            tempRepository.getTempMediaStream().map { list -> list.find { it.id == mediaId } },
                            userNicknameFlow
                        ) { tm, nickname ->
                            tm?.let {
                                SharedMedia(
                                    id = it.id,
                                    type = SharedMedia.MediaType.PHOTO,
                                    localUri = it.localUri,
                                    remoteUrl = null,
                                    thumbnailUrl = null,
                                    subLocalUri = it.subLocalUri,
                                    subRemoteUrl = null,
                                    subThumbnailUrl = null,
                                    cameraFacing = if (it.subLocalUri != null) "DUAL" else "REAR",
                                    caption = null, // Temp media caption starts empty/null
                                    dateTaken = it.takenAt,
                                    orientation = 0,
                                    uploaderName = nickname,
                                    syncStatus = SharedMedia.SyncStatus.SYNCED
                                )
                            }
                        }
                    } else {
                        repository.getSharedAlbumStream().map { list ->
                            list.find { it.id == mediaId }
                        }
                    }

                    combine(
                        mediaStream,
                        captionFlow,
                        savingFlow,
                        errorFlow
                    ) { media, caption, isSaving, error ->
                         if (media != null) {
                             CommentEditUiState(
                                 media = media,
                                 caption = caption ?: (media.caption ?: ""),
                                 isLoading = false,
                                 isSaving = isSaving,
                                 error = error
                             )
                         } else {
                             CommentEditUiState(
                                 media = null,
                                 caption = caption ?: "",
                                 isLoading = false,
                                 isSaving = isSaving,
                                 error = "미디어를 찾을 수 없어요"
                             )
                         }
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CommentEditUiState(isLoading = true)
            )

    fun setMediaId(mediaId: String, isTemp: Boolean = false) {
        mediaIdFlow.value = mediaId
        isTempFlow.value = isTemp
        captionFlow.value = null
        errorFlow.value = null
    }

    fun updateCaption(newCaption: String) {
        captionFlow.value = newCaption
    }

    fun saveCaption(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentMediaId = mediaIdFlow.value ?: return
        val currentIsTemp = isTempFlow.value
        val currentCaption = uiState.value.caption

        viewModelScope.launch {
            savingFlow.value = true
            errorFlow.value = null

            if (currentIsTemp) {
                // Temp Media -> Upload/Move to Shared
                tempRepository.moveToShared(
                    listOf(currentMediaId),
                    mapOf(currentMediaId to currentCaption)
                ).onSuccess {
                    savingFlow.value = false
                    onSuccess()
                }.onFailure { e ->
                    val msg = e.message ?: "저장(업로드)에 실패했어요"
                    errorFlow.value = msg
                    savingFlow.value = false
                    onError(msg)
                }

            } else {
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
    }
    
    fun clearError() {
        errorFlow.value = null
    }
}
