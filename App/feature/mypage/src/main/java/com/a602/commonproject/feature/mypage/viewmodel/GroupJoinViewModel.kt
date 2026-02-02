package com.a602.commonproject.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupJoinUiState(
    val code: String = "",
    val relation: String = "",
    val isLoading: Boolean = false,
    val isJoinSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class GroupJoinViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupJoinUiState())
    val uiState: StateFlow<GroupJoinUiState> = _uiState.asStateFlow()

    fun onCodeChanged(newCode: String) {
        _uiState.update { it.copy(code = newCode) }
    }

    fun onRelationChanged(newRelation: String) {
        _uiState.update { it.copy(relation = newRelation) }
    }

    fun joinGroup() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.code.isBlank() || currentState.relation.isBlank()) {
                _uiState.update { it.copy(errorMessage = "초대코드와 관계를 모두 입력해주세요.") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = groupRepository.joinGroup(currentState.code, currentState.relation)

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isJoinSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "그룹 참여에 실패했습니다. 코드를 확인해주세요.") }
            }
        }
    }

    fun onJoinSuccessConsumed() {
        _uiState.update { it.copy(isJoinSuccess = false) }
    }
}
