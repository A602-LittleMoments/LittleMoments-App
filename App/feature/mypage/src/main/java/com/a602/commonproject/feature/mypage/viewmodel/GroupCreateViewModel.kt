package com.a602.commonproject.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.GroupRepository
import com.a602.commonproject.model.data.GroupRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupCreateUiState(
    val groupName: String = "",
    val relation: String = "",
    val isLoading: Boolean = false,
    val isCreateSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class GroupCreateViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupCreateUiState())
    val uiState: StateFlow<GroupCreateUiState> = _uiState.asStateFlow()

    fun onGroupNameChanged(newName: String) {
        _uiState.update { it.copy(groupName = newName) }
    }

    fun onRelationChanged(newRelation: String) {
        _uiState.update { it.copy(relation = newRelation) }
    }

    fun createGroup() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.groupName.isBlank() || currentState.relation.isBlank()) {
                _uiState.update { it.copy(errorMessage = "그룹 이름과 호칭을 모두 입력해주세요.") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // API 명세에 맞춰 role을 OWNER로 명시하여 전달
            val result = groupRepository.createGroup(
                groupName = currentState.groupName,
                relation = currentState.relation,
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isCreateSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "그룹 생성에 실패했습니다.") }
            }
        }
    }

    fun onCreateSuccessConsumed() {
        _uiState.update { it.copy(isCreateSuccess = false) }
    }
}
