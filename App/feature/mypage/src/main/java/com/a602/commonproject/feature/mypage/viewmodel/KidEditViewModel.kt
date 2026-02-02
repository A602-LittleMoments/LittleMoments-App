package com.a602.commonproject.feature.mypage.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.data.repository.GroupRepository
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
    val isLoading: Boolean = true,
    val isSaveSuccess: Boolean = false,
    val isDeleteSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class KidEditViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val groupRepository: GroupRepository, // 그룹 ID를 가져오기 위해 의존성 추가
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(KidEditUiState())
    val uiState: StateFlow<KidEditUiState> = _uiState.asStateFlow()

    private var babyId: String? = null
    private var initialImageUri: String? = null

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
            val baby = babyRepository.getBabyStream().first().find { it.babyId == babyId }

            if (baby != null) {
                initialImageUri = baby.imageUrl
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
        val currentBabyId = babyId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val currentState = _uiState.value

            val imageFile: File? = getFileFromUri(currentState.imageUri)

            val result = babyRepository.updateBaby(
                babyId = currentBabyId,
                name = currentState.name,
                birthDate = currentState.birthDate,
                gender = currentState.gender,
                imageFile = imageFile
            )

            Log.d("update", "response : $result");

            if (result.isSuccess) {
                // 수정 성공 후, 동기화를 위해 그룹 정보를 가져옵니다.
                val groupResult = groupRepository.getMyGroup()
                if (groupResult.isFailure) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "그룹 정보를 찾을 수 없어 동기화에 실패했습니다.") }
                    return@launch
                }
                val groupId = groupResult.getOrThrow().id

                // 최신 데이터로 동기화합니다.
                babyRepository.syncWithServer(groupId = groupId)
                _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "아이 정보 수정에 실패했습니다.") }
            }
        }
    }

    // --- 핵심 로직: 아이 정보 삭제 ---
    fun deleteBaby() {
        val currentBabyId = babyId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. 그룹 ID를 먼저 가져옵니다.
            val groupResult = groupRepository.getMyGroup()
            if (groupResult.isFailure) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "그룹 정보를 찾을 수 없어 삭제할 수 없습니다.") }
                return@launch
            }
            val groupId = groupResult.getOrThrow().id

            // 2. 정확한 그룹 ID로 삭제를 요청합니다.
            val deleteResult = babyRepository.deleteBaby(groupId = groupId, babyId = currentBabyId)

            if (deleteResult.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isDeleteSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "아이 정보 삭제에 실패했습니다.") }
            }
        }
    }

    private fun getFileFromUri(uriString: String?): File? {
        return if (uriString != initialImageUri && uriString != null && uriString.startsWith("content://")) {
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
                Log.e("KidEditViewModel", "URI에서 임시 파일 생성 실패", e)
                null
            }
        } else {
            null
        }
    }

    fun onSaveSuccessConsumed() {
        _uiState.update { it.copy(isSaveSuccess = false) }
    }

    fun onDeleteSuccessConsumed() {
        _uiState.update { it.copy(isDeleteSuccess = false) }
    }
}
