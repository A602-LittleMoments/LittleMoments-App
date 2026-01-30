package com.a602.commonproject.feature.mypage.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
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
    val errorMessage: String? = null
)

@HiltViewModel
class KidEditViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val savedStateHandle: SavedStateHandle, // NavKey의 인자를 받기 위해 필요
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val babyId: String = savedStateHandle.get<KidEditKey>("key")!!.babyId

    private val _uiState = MutableStateFlow(KidEditUiState())
    val uiState: StateFlow<KidEditUiState> = _uiState.asStateFlow()

    init {
        // 뷰모델 생성 시, babyId에 해당하는 아이의 정보로 초기 상태를 설정합니다.
        viewModelScope.launch {
            val baby = babyRepository.getBabyStream()
                .first() // 현재 DB에 있는 최신 목록을 한 번만 가져옵니다.
                .find { it.babyId == babyId }

            if (baby != null) {
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val currentState = _uiState.value

            val imageFile = currentState.imageUri?.let { uriString ->
                // 서버에 이미 있는 URL이 아니라, 사용자가 새로 선택한 파일 경로일 때만 File 객체로 변환
                if (!uriString.startsWith("http")) {
                    try {
                        Uri.parse(uriString).path?.let { File(it) }
                    } catch (e: Exception) { null }
                } else null
            }

            val result = babyRepository.updateBaby(
                babyId = babyId,
                name = currentState.name,
                birthDate = currentState.birthDate,
                gender = currentState.gender,
                imageFile = imageFile
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "아이 정보 수정에 실패했습니다.") }
            }
        }
    }

    fun onSaveSuccessConsumed() {
        _uiState.update { it.copy(isSaveSuccess = false) }
    }
}
