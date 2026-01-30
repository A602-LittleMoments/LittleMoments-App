package com.a602.commonproject.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.GroupRepository
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.GroupRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 그룹 관리 화면의 모든 UI 상태와 팝업 상태를 관리하는 데이터 클래스입니다.
 */
data class GroupChangeUiState(
    val members: List<GroupMember> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // Dialog states
    val showRoleSelectDialog: Boolean = false,
    val showInviteCodeDialog: Boolean = false,
    val memberToEdit: GroupMember? = null, // 역할 변경 대상 멤버
    // Data for dialogs
    val inviteCode: String? = null,
    val inviteCodeExpiry: Int = 180
)

@HiltViewModel
class GroupChangeViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupChangeUiState())
    val uiState: StateFlow<GroupChangeUiState> = _uiState.asStateFlow()

    init {
        loadMembers()
    }

    /**
     * 멤버 목록을 불러옵니다.
     */
    fun loadMembers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = groupRepository.getGroupMembers()
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, members = result.getOrThrow()) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "멤버 목록을 불러오지 못했습니다.") }
            }
        }
    }

    // --- 초대 플로우 관련 액션 ---

    // 1. '멤버 추가' 버튼 클릭 -> 역할 선택 팝업 표시
    fun onAddMemberClicked() {
        _uiState.update { it.copy(showRoleSelectDialog = true) }
    }

    // 2. 역할 선택 팝업에서 '확인' 클릭 -> 초대 코드 요청 및 표시
    fun onRoleSelectedForInvite(isMember: Boolean) {
        _uiState.update { it.copy(showRoleSelectDialog = false) } // 역할 선택 팝업 닫기
        viewModelScope.launch {
            val result = groupRepository.getInvites()
            if (result.isSuccess) {
                val inviteData = result.getOrThrow()
                // 선택된 역할에 따라 다른 코드를 사용합니다.
                val code = if (isMember) inviteData.codeMember else inviteData.codeViewer
                val expiry = inviteData.expiredAt.toIntOrNull() ?: 180
                _uiState.update {
                    it.copy(
                        showInviteCodeDialog = true,
                        inviteCode = code,
                        inviteCodeExpiry = expiry
                    )
                }
            } else {
                _uiState.update { it.copy(errorMessage = "초대 코드를 불러오지 못했습니다.") }
            }
        }
    }

    // 3. 초대 코드 팝업 닫기
    fun onInviteDialogDismissed() {
        _uiState.update { it.copy(showInviteCodeDialog = false, inviteCode = null) }
    }

    // 4. 역할 선택 팝업 닫기
    fun onRoleSelectDialogDismissed() {
        _uiState.update { it.copy(showRoleSelectDialog = false) }
    }


    // --- 역할 변경 플로우 관련 액션 ---

    fun onRoleEditClicked(member: GroupMember) {
        // 방장(OWNER)의 역할은 변경할 수 없습니다.
        if (member.role != GroupRole.OWNER) {
            _uiState.update { it.copy(memberToEdit = member) }
        }
    }

    fun onRoleChangeDialogDismissed() {
        _uiState.update { it.copy(memberToEdit = null) }
    }

    fun updateMemberRole(newRole: GroupRole) {
        val userId = _uiState.value.memberToEdit?.userId ?: return
        viewModelScope.launch {
            // TODO: GroupRepository에 역할 변경(updateMemberRole) 함수가 추가되면, 아래 주석을 풀고 실제 로직을 구현합니다.
            /*
            val result = groupRepository.updateMemberRole(userId, newRole)
            if (result.isSuccess) {
                loadMembers() // 성공 시 멤버 목록 새로고침
            } else {
                _uiState.update { it.copy(errorMessage = "역할 변경에 실패했습니다.") }
            }
            */
            // 지금은 임시로 팝업만 닫습니다.
            _uiState.update { it.copy(memberToEdit = null) }
        }
    }
}
