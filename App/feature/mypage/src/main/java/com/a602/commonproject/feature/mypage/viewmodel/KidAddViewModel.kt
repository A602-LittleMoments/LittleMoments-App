package com.a602.commonproject.feature.mypage.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.model.data.Baby
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * 아이 추가 화면에 필요한 모든 UI 상태를 담는 데이터 클래스입니다.
 */
data class KidAddUiState(
    val name: String = "",
    val birthDate: String = "", // "YYYY-MM-DD"
    val gender: Baby.Gender = Baby.Gender.UNKNOWN,
    val imageUri: String? = null,
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class KidAddViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    @ApplicationContext private val context: Context // 이미지 파일 경로 처리를 위해 Context 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow(KidAddUiState())
    val uiState: StateFlow<KidAddUiState> = _uiState.asStateFlow()

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

    // --- 핵심 로직: 아이 추가 저장 ---

    fun addBaby() {
        viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState.name.isBlank() || currentState.birthDate.isBlank()) {
                _uiState.update { it.copy(errorMessage = "아이 이름과 생년월일을 모두 입력해주세요.") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val imageFile: File? = currentState.imageUri?.let { uriString ->
                if (uriString.startsWith("content://")) {
                    try {
                        val uri = Uri.parse(uriString)
                        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            FileOutputStream(tempFile).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        tempFile
                    } catch (e: Exception) {
                        Log.e("KidAddViewModel", "Failed to create temp file from URI", e)
                        null
                    }
                } else {
                    null
                }
            }

            val result = babyRepository.addBaby(
                name = currentState.name,
                birthDate = currentState.birthDate,
                gender = currentState.gender,
                imageFile = imageFile
            )

            Log.d("add", "response : $result")
            if (result.isSuccess) {
                // 추가 성공 후, 서버와 동기화하여 최신 데이터를 반영합니다.
                babyRepository.syncWithServer(groupId = "") // groupId는 Repository에서 처리합니다.
                _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "아이 추가에 실패했습니다.") }
            }
        }
    }

    fun onSaveSuccessConsumed() {
        _uiState.update { it.copy(isSaveSuccess = false) }
    }
}
