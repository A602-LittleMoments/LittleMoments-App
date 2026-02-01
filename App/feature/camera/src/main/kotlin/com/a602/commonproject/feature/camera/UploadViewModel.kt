package com.a602.commonproject.feature.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.TempMediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val tempMediaRepository: TempMediaRepository
) : ViewModel() {

    fun saveMedia(
        backUri: String,
        subLocalUri: String,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val tempId = UUID.randomUUID().toString()
                val takenAt = System.currentTimeMillis()
                val backFile = File(backUri)
                val subFile = if (subLocalUri.isNotBlank()) File(subLocalUri) else null

                tempMediaRepository.saveTempMedia(
                    tempId = tempId,
                    file = backFile,
                    subFile = subFile,
                    takenAt = takenAt,
                    orientation = 0, // 기본값
                    cameraFacing = "BACK_AND_FRONT"
                ).onSuccess {
                    onSuccess()
                }.onFailure {
                    onError(it)
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
