package com.a602.commonproject.feature.album.viewmodel

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class MediaDetailUiState(
    val mediaId: String? = null,
    val media: SharedMedia? = null,
    val allMedias: List<SharedMedia> = emptyList(),

    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val isDownloading: Boolean = false,
    val isSavingBitmap: Boolean = false,

    val deleteSuccess: Boolean = false,
    val downloadSuccess: Boolean = false,
    val saveBitmapSuccess: Boolean = false,

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
                downloadSuccess = false,
                saveBitmapSuccess = false
            )
        }
    }

    /** ✅ 삭제 */
    fun deleteCurrent() {
        val id = uiState.value.mediaId ?: return
        deleteMedia(id)
    }

    fun deleteMedia(id: String) {
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
        downloadMedia(media)
    }

    fun downloadMedia(media: SharedMedia) {
        val remoteUrl = media.remoteUrl
        val localUriPath = media.localUri

        viewModelScope.launch {
            actionState.update { it.copy(isDownloading = true, errorMessage = null) }

            try {
                if (!remoteUrl.isNullOrBlank()) {
                    // 1. Remote Download via DownloadManager
                    val request = android.app.DownloadManager.Request(Uri.parse(remoteUrl))
                        .setTitle("아이랑나랑 사진")
                        .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_PICTURES,
                            "a602_${media.id}.jpg"
                        )

                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
                    dm.enqueue(request)
                    
                    actionState.update { it.copy(isDownloading = false, downloadSuccess = true) }
                } else if (!localUriPath.isNullOrBlank()) {
                    // 2. Local File -> Save to Gallery
                    val success = withContext(Dispatchers.IO) {
                        saveLocalFileToGallery(localUriPath, media.id)
                    }
                    if (success) {
                        actionState.update { it.copy(isDownloading = false, downloadSuccess = true) }
                    } else {
                        actionState.update { it.copy(isDownloading = false, errorMessage = "갤러리 저장에 실패했어요") }
                    }
                } else {
                    actionState.update { it.copy(isDownloading = false, errorMessage = "저장할 수 있는 사진 정보가 없어요") }
                }

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

    private suspend fun saveLocalFileToGallery(localPath: String, mediaId: String): Boolean {
        return try {
            val sourceFile = File(localPath)
            if (!sourceFile.exists()) return false

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "a602_${mediaId}_${System.currentTimeMillis()}.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return false

            resolver.openOutputStream(uri)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun onDownloadSuccessConsumed() {
        actionState.update { it.copy(downloadSuccess = false, saveBitmapSuccess = false) }
    }

    fun saveBitmapToGallery(bitmap: Bitmap) {
        viewModelScope.launch {
            actionState.update { it.copy(isSavingBitmap = true, errorMessage = null) }
            
            val success = withContext(Dispatchers.IO) {
                try {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, "a602_capture_${System.currentTimeMillis()}.jpg")
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
 
                    val resolver = context.contentResolver
                    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return@withContext false
 
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    }
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
 
            if (success) {
                actionState.update { it.copy(isSavingBitmap = false, saveBitmapSuccess = true) }
            } else {
                actionState.update { it.copy(isSavingBitmap = false, errorMessage = "이미지 저장에 실패했어요") }
            }
        }
    }
 
    fun clearError() {
        actionState.update { it.copy(errorMessage = null) }
    }
}
