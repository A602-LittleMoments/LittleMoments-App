package com.a602.commonproject.feature.album.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.TempMediaRepository
import com.a602.commonproject.model.data.TempMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

data class TempAlbumUiState(
    val medias: List<TempMedia> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
private val errorFlow = MutableStateFlow<String?>(null)

@HiltViewModel
class TempAlbumViewModel @Inject constructor(
    private val repository: TempMediaRepository
) : ViewModel() {

    val uiState: StateFlow<TempAlbumUiState> =
        repository.getTempMediaStream()
            .map { medias ->
                TempAlbumUiState(
                    medias = medias,
                    isLoading = false,
                    error = errorFlow.value

                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TempAlbumUiState(isLoading = true)
            )
    fun clearError() {
        errorFlow.value = null
    }

    //선택 삭제
    fun deleteSelected(ids: List<String>, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteTempMedia(ids)
                .onSuccess { onComplete() }
                .onFailure {  e ->
                    errorFlow.value = e.message ?: "사진 삭제에 실패했어요" }
        }
    }

    //전체 삭제
    fun clearAll(onComplete: () -> Unit) {
        viewModelScope.launch {
            val allIds = uiState.value.medias.map { it.id }
            repository.deleteTempMedia(allIds)
                .onSuccess { onComplete() }
                .onFailure {  e ->
                    errorFlow.value = e.message ?: "한달 앨범을 비우지 못했어요" }
        }
    }
}
