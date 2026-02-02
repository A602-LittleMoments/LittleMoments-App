package com.a602.commonproject.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.GroupRepository
import com.a602.commonproject.model.data.Group
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
    val group: Group? = null,
    val members: List<GroupMember> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // Dialog states
    val showRoleSelectDialog: Boolean = false,
    val showInviteCodeDialog: Boolean = false,
    val memberToEdit: GroupMember? = null,
    val showEditNameDialog: Boolean = false, // 그룹 이름 수정 팝업 상태
    val showLeaveGroupConfirmDialog: Boolean = false, // 그룹 나가기 확인 팝업 상태
    val isLeaveSuccess: Boolean = false, // 그룹 나가기 성공 상태
    // Data for dialogs
    val inviteCode: String? = null,
    val inviteCodeExpiry: Int = 180,
    val isMemberCode: Boolean = true
)

@HiltViewModel
class GroupChangeViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupChangeUiState())
    val uiState: StateFlow<GroupChangeUiState> = _uiState.asStateFlow()

    init {
        loadGroupAndMembers()
    }

    /**
     * 그룹 정보와 멤버 목록을 함께 불러옵니다.
     */
    private fun loadGroupAndMembers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val groupResult = groupRepository.getMyGroup()
            val membersResult = groupRepository.getGroupMembers()

            if (groupResult.isSuccess && membersResult.isSuccess) {
                val sortedMembers = membersResult.getOrThrow().sortedBy { it.role.ordinal }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        group = groupResult.getOrThrow(),
                        members = sortedMembers
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "그룹 정보를 불러오지 못했습니다.") }
            }
        }
    }

    /**
     * 화면을 아래로 당겨 새로고침할 때 호출됩니다.
     */
    fun refresh() {
        loadGroupAndMembers()
    }

    // --- 그룹 이름 수정 관련 ---
    fun onEditGroupNameClicked() {
        _uiState.update { it.copy(showEditNameDialog = true) }
    }

    fun onEditGroupNameDismissed() {
        _uiState.update { it.copy(showEditNameDialog = false) }
    }

    fun updateGroupName(newName: String) {
        viewModelScope.launch {
            val result = groupRepository.updateGroupName(newName)
            if(result.isSuccess) {
                loadGroupAndMembers() // 성공 시 그룹 정보 새로고침
            } else {
                _uiState.update{ it.copy(errorMessage = "그룹 이름 변경에 실패했습니다.") }
            }
            _uiState.update { it.copy(showEditNameDialog = false) } // 성공 여부와 관계없이 다이얼로그 닫기
        }
    }

    // --- 그룹 나가기 관련 ---
    fun onLeaveGroupClicked() {
        _uiState.update { it.copy(showLeaveGroupConfirmDialog = true) }
    }

    fun onLeaveGroupDismissed() {
        _uiState.update { it.copy(showLeaveGroupConfirmDialog = false) }
    }

    fun leaveGroup() {
        viewModelScope.launch {
            val result = groupRepository.leaveGroup()
            if(result.isSuccess) {
                _uiState.update { it.copy(isLeaveSuccess = true) }
            } else {
                _uiState.update { it.copy(errorMessage = "그룹 나가기에 실패했습니다.") }
            }
            _uiState.update { it.copy(showLeaveGroupConfirmDialog = false) }
        }
    }

    fun onLeaveSuccessConsumed() {
        _uiState.update { it.copy(isLeaveSuccess = false) }
    }

    // --- 초대 플로우 관련 액션 ---

    // 1. '멤버 추가' 버튼 클릭 -> 역할 선택 팝업 표시
    fun onAddMemberClicked() {
        _uiState.update { it.copy(showRoleSelectDialog = true) }
    }

    // 2. 역할 선택 팝업에서 '확인' 클릭 -> 초대 코드 요청 및 표시
    fun onRoleSelectedForInvite(isMember: Boolean) {
        _uiState.update { it.copy(showRoleSelectDialog = false, isMemberCode = isMember) }
        fetchInviteCode(isMember = isMember)
    }

    // 초대 코드 새로고침
    fun refreshInviteCode(){
        fetchInviteCode(isMember = _uiState.value.isMemberCode, isRefresh = true)
    }

    private fun fetchInviteCode(isMember: Boolean, isRefresh: Boolean = false) {
        viewModelScope.launch {
            val result = if(isRefresh) groupRepository.refreshInvites() else groupRepository.getInvites()

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
            // TODO: 역할 변경 API 구현 후 주석 해제
            /* val result = groupRepository.updateMemberRole(userId, newRole)
            if (result.isSuccess) {
                loadGroupAndMembers()
            } else {
                _uiState.update { it.copy(errorMessage = "역할 변경에 실패했습니다.") }
            }*/
            _uiState.update { it.copy(memberToEdit = null) }
        }
    }
}
