package com.a602.commonproject.feature.mypage.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.feature.mypage.navigation.KidEditKey
import com.a602.commonproject.model.data.Baby
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * 아이 수정 화면에 필요한 모든 UI 상태를 담는 데이터 클래스입니다.
 */
data class KidEditUiState(
    val name: String = "",
    val birthDate: String = "",
    val gender: Baby.Gender = Baby.Gender.UNKNOWN,
    val imageUri: String? = null,
    val isLoading: Boolean = true, // 처음에는 데이터를 불러오므로 true
    val isSaveSuccess: Boolean = false,
    val isDeleteSuccess: Boolean = false, // 삭제 성공 상태 추가
    val errorMessage: String? = null
)

@HiltViewModel
class KidEditViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(KidEditUiState())
    val uiState: StateFlow<KidEditUiState> = _uiState.asStateFlow()

    private var babyId: String? = null
    private var initialImageUri: String? = null // 초기 이미지 URI를 저장할 변수

    fun initialize(key: KidEditKey) {
        if (key.babyId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "잘못된 접근입니다. 아이 ID가 없습니다.") }
        } else {
            this.babyId = key.babyId
            loadBabyInfo(key.babyId)
        }
    }

    private fun loadBabyInfo(babyId: String) {
        viewModelScope.launch {
            val baby = babyRepository.getBabyStream()
                .first()
                .find { it.babyId == babyId }

            if (baby != null) {
                initialImageUri = baby.imageUrl // 초기 URI 저장
                _uiState.value = KidEditUiState(
                    name = baby.babyName,
                    birthDate = baby.birthDate,
                    gender = baby.gender,
                    imageUri = baby.imageUrl,
                    isLoading = false
                )
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "아이 정보를 불러오지 못했습니다.") }
            }
        }
    }

    // --- UI 이벤트 처리 함수들 ---

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onBirthDateChanged(birthDate: String) {
        _uiState.update { it.copy(birthDate = birthDate) }
    }

    fun onGenderSelected(gender: Baby.Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun onImageSelected(uri: String?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    // --- 핵심 로직: 아이 정보 수정 저장 ---

    fun updateBaby() {
        val currentBabyId = babyId ?: run {
            _uiState.update { it.copy(errorMessage = "아이 ID를 찾을 수 없어 저장할 수 없습니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val currentState = _uiState.value

            val imageFile: File? = if (currentState.imageUri != initialImageUri && currentState.imageUri != null) {
                if (currentState.imageUri.startsWith("content://")) {
                    try {
                        val uri = Uri.parse(currentState.imageUri)
                        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            FileOutputStream(tempFile).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        tempFile
                    } catch (e: Exception) {
                        Log.e("KidEditViewModel", "Failed to create temp file from URI", e)
                        null
                    }
                } else null
            } else {
                // 이미지가 변경되지 않았으면 파일을 보내지 않습니다 (null).
                null
            }

            val result = babyRepository.updateBaby(
                babyId = currentBabyId,
                name = currentState.name,
                birthDate = currentState.birthDate,
                gender = currentState.gender,
                imageFile = imageFile
            )

            Log.d("update", "response : $result");

            if (result.isSuccess) {
                // 수정 성공 후, 서버와 동기화하여 최신 데이터를 반영합니다.
                babyRepository.syncWithServer(groupId = "") // groupId는 Repository에서 처리합니다.
                _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "아이 정보 수정에 실패했습니다.") }
            }
        }
    }

    // --- 핵심 로직: 아이 정보 삭제 ---
    fun deleteBaby() {
        val currentBabyId = babyId ?: run {
            _uiState.update { it.copy(errorMessage = "아이 ID를 찾을 수 없어 삭제할 수 없습니다.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // BabyRepository에 그룹 ID가 필요하므로, 이 부분은 Repository에서 처리하도록 위임합니다.
            val result = babyRepository.deleteBaby(groupId = "", babyId = currentBabyId)

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isDeleteSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "아이 정보 삭제에 실패했습니다.") }
            }
        }
    }

    fun onSaveSuccessConsumed() {
        _uiState.update { it.copy(isSaveSuccess = false) }
    }

    fun onDeleteSuccessConsumed() {
        _uiState.update { it.copy(isDeleteSuccess = false) }
    }
}
