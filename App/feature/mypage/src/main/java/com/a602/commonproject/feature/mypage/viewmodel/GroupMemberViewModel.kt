package com.a602.commonproject.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.GroupRepository
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.GroupRole
import com.a602.commonproject.model.data.InviteCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 그룹 멤버 관리 화면에 필요한 모든 UI 상태를 담는 데이터 클래스입니다.
 */
data class GroupMemberUiState(
    val members: List<GroupMember> = emptyList(),
    val inviteCode: InviteCode? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showInviteDialog: Boolean = false, // 초대 코드 팝업을 보여줄지 여부
    val memberToEdit: GroupMember? = null   // 역할을 수정할 멤버 정보
)

@HiltViewModel
class GroupMemberViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupMemberUiState())
    val uiState: StateFlow<GroupMemberUiState> = _uiState.asStateFlow()

    init {
        loadMembers()
    }

    /**
     * 그룹 멤버 목록을 새로고침하는 함수입니다.
     */
    fun loadMembers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = groupRepository.getGroupMembers()
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, members = result.getOrThrow()) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "그룹 멤버를 불러오는데 실패했습니다.") }
            }
        }
    }

    /**
     * '새 구성원 추가' 버튼을 눌렀을 때 호출됩니다.
     * 초대 코드를 불러와 팝업을 띄우도록 상태를 변경합니다.
     */
    fun onAddMemberClicked() {
        viewModelScope.launch {
            val result = groupRepository.getInvites()
            if (result.isSuccess) {
                _uiState.update { it.copy(showInviteDialog = true, inviteCode = result.getOrThrow()) }
            } else {
                _uiState.update { it.copy(errorMessage = "초대 코드를 불러오는데 실패했습니다.") }
            }
        }
    }

    /**
     * 초대 코드 팝업이 닫혔을 때 호출됩니다.
     */
    fun onInviteDialogDismissed() {
        _uiState.update { it.copy(showInviteDialog = false, inviteCode = null) }
    }

    /**
     * 멤버의 '역할 수정' 버튼을 눌렀을 때, 수정할 멤버를 상태에 저장합니다.
     * (실제 UI에서는 이 상태를 보고 역할 선택 팝업을 띄웁니다)
     */
    fun onRoleEditClicked(member: GroupMember) {
        _uiState.update { it.copy(memberToEdit = member) }
    }

    /**
     * 역할 수정 팝업이 닫혔을 때 호출됩니다.
     */
    fun onRoleEditDialogDismissed() {
        _uiState.update { it.copy(memberToEdit = null) }
    }

    /**
     * 멤버의 역할을 실제로 변경하는 함수입니다.
     */
    fun updateMemberRole(userId: String, newRole: GroupRole) {
        viewModelScope.launch {
            // TODO: GroupRepository에 역할 변경(updateMemberRole) 함수를 추가해야 합니다.
            // 아래는 함수가 추가되었을 때의 예상 코드입니다.
            /*
            val result = groupRepository.updateMemberRole(userId, newRole)
            if (result.isSuccess) {
                loadMembers() // 성공 시 멤버 목록 새로고침
            } else {
                _uiState.update { it.copy(errorMessage = "역할 변경에 실패했습니다.") }
            }
            */

            // 임시로 역할 수정 팝업만 닫습니다.
            onRoleEditDialogDismissed()
        }
    }
}
