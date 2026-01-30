//package com.a602.commonproject.feature.gallery.navigation
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.a602.commonproject.model.data.SharedMedia
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//data class PhotoDetailUiState(
//    val isLoading: Boolean = true,
//    val mediaId: String? = null,
//    val media: SharedMedia? = null,
//    val errorMessage: String? = null,
//)
//
//@HiltViewModel
//class PhotoDetailViewModel @Inject constructor(
//    // repo 주입 ?
//    // private val repository: GalleryRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(PhotoDetailUiState(isLoading = true))
//    val uiState: StateFlow<PhotoDetailUiState> = _uiState.asStateFlow()
//
//
//    fun load(mediaId: String, medias: List<SharedMedia>) {
//        _uiState.value = PhotoDetailUiState(isLoading = true, mediaId = mediaId)
//
//        val media = medias.firstOrNull { it.id == mediaId }
//        _uiState.value = if (media != null) {
//            PhotoDetailUiState(
//                isLoading = false,
//                mediaId = mediaId,
//                media = media,
//                errorMessage = null
//            )
//        } else {
//            PhotoDetailUiState(
//                isLoading = false,
//                mediaId = mediaId,
//                media = null,
//                errorMessage = "Media not found: $mediaId"
//            )
//        }
//    }
//
//    fun onDelete(mediaId: String) {
//        viewModelScope.launch {
//            // 서버 삭제 API 호출
//            // repository.delete(mediaId)
//        }
//    }
//
//    fun onDownload(mediaId: String) {
//        viewModelScope.launch {
//            // 다운로드 처리 (파일 저장 / 권한 등)
//        }
//    }
//
//    fun onEdit(mediaId: String) {
//
//    }
//
//}
