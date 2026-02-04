package com.a602.commonproject.feature.album.viewmodel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MediaDetailUiState(
    val mediaId: String? = null,
    val media: SharedMedia? = null,
    val allMedias: List<SharedMedia> = emptyList(),

    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val isDownloading: Boolean = false,

    val deleteSuccess: Boolean = false,
    val downloadSuccess: Boolean = false,

    val errorMessage: String? = null
)

@HiltViewModel
class MediaDetailViewModel @Inject constructor(
    private val repository: SharedMediaRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val mediaIdFlow = MutableStateFlow<String?>(null)
    private val actionState = MutableStateFlow(
        MediaDetailUiState(
            isLoading = true
        )
    )

    val uiState: StateFlow<MediaDetailUiState> =
        combine(
            repository.getSharedAlbumStream(),
            mediaIdFlow,
            actionState
        ) { medias, mediaId, action ->
            if (mediaId == null) {
                return@combine action.copy(
                    mediaId = null,
                    media = null,
                    isLoading = true,
                    errorMessage = null
                )
            }

            val media = medias.firstOrNull { it.id == mediaId }

            when {
                media != null -> action.copy(
                    mediaId = mediaId,
                    media = media,
                    allMedias = medias,
                    isLoading = false,
                    errorMessage = null
                )

                medias.isEmpty() -> action.copy(
                    mediaId = mediaId,
                    media = null,
                    allMedias = emptyList(),
                    isLoading = true
                )

                else -> action.copy(
                    mediaId = mediaId,
                    media = null,
                    allMedias = medias,
                    isLoading = false,
                    errorMessage = "사진을 불러오지 못했어요"
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MediaDetailUiState(isLoading = true)
        )

    fun setMediaId(mediaId: String) {
        mediaIdFlow.value = mediaId
        actionState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                deleteSuccess = false,
                downloadSuccess = false
            )
        }
    }

    /** ✅ 삭제 */
    fun deleteCurrent() {
        val id = uiState.value.mediaId ?: return

        viewModelScope.launch {
            actionState.update { it.copy(isDeleting = true, errorMessage = null) }

            repository.deleteMedia(id)
                .onSuccess {
                    actionState.update { it.copy(isDeleting = false, deleteSuccess = true) }
                }
                .onFailure { e ->
                    actionState.update {
                        it.copy(
                            isDeleting = false,
                            errorMessage = e.message ?: "삭제에 실패했어요"
                        )
                    }
                }
        }
    }

    fun onDeleteSuccessConsumed() {
        actionState.update { it.copy(deleteSuccess = false) }
    }


    fun downloadCurrent() {
        val media = uiState.value.media ?: run {
            actionState.update { it.copy(errorMessage = "다운로드할 사진이 없어요") }
            return
        }

        val url = media.remoteUrl ?: run {
            if (!media.localUri.isNullOrEmpty()) {
                actionState.update { it.copy(errorMessage = "이미 기기에 저장되어 있는 사진입니다.") }
            } else {
                actionState.update { it.copy(errorMessage = "다운로드 URL이 없어요") }
            }
            return
        }

        viewModelScope.launch {
            actionState.update { it.copy(isDownloading = true, errorMessage = null) }

            try {
                val request = DownloadManager.Request(Uri.parse(url))
                    .setTitle("아이랑나랑 사진")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_PICTURES,
                        "a602_${media.id}.jpg"
                    )

                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)

                actionState.update { it.copy(isDownloading = false, downloadSuccess = true) }
            } catch (e: Exception) {
                actionState.update {
                    it.copy(
                        isDownloading = false,
                        errorMessage = e.message ?: "다운로드에 실패했어요"
                    )
                }
            }
        }
    }

    fun onDownloadSuccessConsumed() {
        actionState.update { it.copy(downloadSuccess = false) }
    }

    fun clearError() {
        actionState.update { it.copy(errorMessage = null) }
    }
}
