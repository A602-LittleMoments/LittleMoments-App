package com.a602.commonproject.feature.album.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.TempMediaRepository
import com.a602.commonproject.model.data.TempMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MultiPhotoUploadViewModel @Inject constructor(
    private val repository: TempMediaRepository
) : ViewModel() {

    // UI에서 Nav arguments를 받아 `setTargetIds`를 호출하여 ID 리스트를 전달한다고 가정합니다.

    private val _targetIds = MutableStateFlow<List<String>>(emptyList())

    val selectedMedias: StateFlow<List<TempMedia>> = combine(
        repository.getTempMediaStream(),
        _targetIds
    ) { allMedia, ids ->
        allMedia.filter { it.id in ids }
    }.stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), emptyList())

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState = _uploadState.asStateFlow()

    fun setTargetIds(ids: List<String>) {
        _targetIds.value = ids
    }

    fun removeTargetId(id: String) {
        val current = _targetIds.value.toMutableList()
        current.remove(id)
        _targetIds.value = current
    }

    fun upload(captions: Map<String, String>, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Uploading
            val ids = _targetIds.value

            repository.moveToShared(ids, captions)
                .onSuccess {
                    _uploadState.value = UploadState.Success
                    onComplete()
                }
                .onFailure {
                    _uploadState.value = UploadState.Error(it.message ?: "업로드 실패")
                }
        }
    }
}

sealed interface UploadState {
    data object Idle : UploadState
    data object Uploading : UploadState
    data object Success : UploadState
    data class Error(val message: String) : UploadState
}
