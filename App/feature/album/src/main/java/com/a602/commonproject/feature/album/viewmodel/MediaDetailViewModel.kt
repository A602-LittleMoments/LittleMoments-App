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
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.model.data.SharedMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
import kotlinx.coroutines.flow.flatMapLatest

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
    private val collectionRepository: CollectionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val mediaIdFlow = MutableStateFlow<String?>(null)
    private val dateFlow = MutableStateFlow<String?>(null)
    private val keywordIdFlow = MutableStateFlow<String?>(null)
    private val babyIdFlow = MutableStateFlow<String?>(null)
    private val yearFlow = MutableStateFlow<Int?>(null)
    private val keywordMediasFlow = MutableStateFlow<List<SharedMedia>>(emptyList())
    private val actionState = MutableStateFlow(
        MediaDetailUiState(
            isLoading = true
        )
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MediaDetailUiState> = kotlinx.coroutines.flow.combine(babyIdFlow, yearFlow) { b, y -> b to y }
        .flatMapLatest { (babyId, year) ->
            combine(
                repository.getSharedAlbumStream(babyId, year),
                mediaIdFlow,
                dateFlow,
                keywordIdFlow,
                keywordMediasFlow,
                actionState
            ) { flows ->
                val medias = flows[0] as List<SharedMedia>
                val mediaId = flows[1] as? String
                val dateStr = flows[2] as? String
                val keywordIdStr = flows[3] as? String
                val kMedias = flows[4] as List<SharedMedia>
                val action = flows[5] as MediaDetailUiState

                if (mediaId == null) {
                    return@combine action.copy(
                        mediaId = null,
                        media = null,
                        isLoading = true,
                        errorMessage = null
                    )
                }

                // 1. 먼저 어떤 리스트를 보여줄지 결정합니다 (필터링 적용)
                val filteredMedias = when {
                    dateStr != null -> {
                        val targetDate = LocalDate.parse(dateStr)
                        medias.filter {
                            Instant.ofEpochMilli(it.dateTaken)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate() == targetDate
                        }
                    }
                    keywordIdStr != null -> {
                        kMedias
                    }
                    else -> {
                        medias
                    }
                }

                // 2. 결정된 리스트 안에서 현재 보고 있는 사진을 찾습니다.
                // (키워드/날짜 모드일 경우 medias에는 없고 filteredMedias에만 있을 수 있음)
                val media = filteredMedias.firstOrNull { it.id == mediaId }

                when {
                    media != null -> action.copy(
                        mediaId = mediaId,
                        media = media,
                        // ✨ [Fix] 삭제 성공 상태라면 리스트에서 사라져도 에러가 아님 (화면 닫히기 전)
                        allMedias = if (action.deleteSuccess) emptyList() else filteredMedias,
                        isLoading = false,
                        errorMessage = null
                    )

                    // ✨ [Fix] 삭제 중이거나 삭제에 성공했을 때, 데이터가 사라져도 에러로 처리하지 않음
                    action.isDeleting || action.deleteSuccess -> action.copy(
                        mediaId = mediaId,
                        media = null,
                        allMedias = filteredMedias,
                        isLoading = action.isDeleting, // 삭제 중이면 로딩 표시
                        errorMessage = null // 에러 메시지 띄우지 않음
                    )

                    filteredMedias.isEmpty() -> action.copy(
                        mediaId = mediaId,
                        media = null,
                        allMedias = emptyList(),
                        isLoading = true // 로딩 중일 수도 있음
                    )

                    else -> action.copy(
                        mediaId = mediaId,
                        media = null,
                        allMedias = filteredMedias,
                        isLoading = false,
                        errorMessage = "사진을 불러오지 못했어요"
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MediaDetailUiState(isLoading = true)
        )

    fun setMediaId(mediaId: String, date: String? = null, keywordId: String? = null, babyId: String? = null, year: Int? = null) {
        // 모든 값이 동일하면 무시
        if (mediaIdFlow.value == mediaId && 
            dateFlow.value == date && 
            keywordIdFlow.value == keywordId &&
            babyIdFlow.value == babyId &&
            yearFlow.value == year) return

        // 상태 업데이트 전 기존 키워드 확인
        val previousKeywordId = keywordIdFlow.value

        mediaIdFlow.value = mediaId
        dateFlow.value = date
        keywordIdFlow.value = keywordId
        babyIdFlow.value = babyId
        yearFlow.value = year

        // 키워드가 실제로 '변경'되었거나, 처음 들어왔을 때만 로딩
        if (keywordId != null && keywordId != previousKeywordId) {
            viewModelScope.launch {
                val result = collectionRepository.getCollectionDetail(keywordId)
                keywordMediasFlow.value = result.getOrElse { emptyList() }
            }
        }

        actionState.update {
            it.copy(
                // 키워드 변경 시에는 로딩 보여주기, 단순 스와이프(ID 변경) 시에는 로딩 안 함
                isLoading = keywordId != previousKeywordId,
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

    fun refreshData() {
        val keywordId = keywordIdFlow.value
        if (keywordId != null) {
            viewModelScope.launch {
                // 로딩 시작
                actionState.update { it.copy(isLoading = true) }
                // 데이터 다시 불러오기
                val result = collectionRepository.getCollectionDetail(keywordId)
                keywordMediasFlow.value = result.getOrElse { emptyList() }
                // 로딩 끝
                actionState.update { it.copy(isLoading = false) }
            }
        }
    }
}
