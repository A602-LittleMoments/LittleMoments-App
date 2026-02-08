package com.a602.commonproject.feature.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.TempMediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val tempMediaRepository: TempMediaRepository
) : ViewModel() {

    private val _saveResultEvent = MutableSharedFlow<String>() // Message to show in Snackbar
    val saveResultEvent: SharedFlow<String> = _saveResultEvent.asSharedFlow()

    fun savePhoto(
        backPath: String,
        frontPath: String = ""
    ) {
        viewModelScope.launch {
            val backFile = File(backPath)
            val subFile = if (frontPath.isNotBlank()) File(frontPath) else null
            val id = UUID.randomUUID().toString()
            val takenAt = System.currentTimeMillis()

            // TODO: Orientation and Facing are hardcoded for now or need to be passed from Camera
            val orientation = 0 
            val cameraFacing = "DUAL" 

            tempMediaRepository.saveTempMedia(
                tempId = id,
                file = backFile,
                subFile = subFile,
                takenAt = takenAt,
                orientation = orientation,
                cameraFacing = cameraFacing
            ).onSuccess {
                _saveResultEvent.emit("한달 앨범에 저장되었어요")
            }.onFailure {
                _saveResultEvent.emit("저장에 실패했어요")
            }
        }
    }
}
