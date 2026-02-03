package com.a602.commonproject.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.data.repository.GroupRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.model.data.AuthState
import com.a602.commonproject.model.data.Baby
import kotlinx.coroutines.flow.Flow
import com.a602.commonproject.model.data.Group
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 마이페이지
// 1. 화면에 필요한 모든 데이터를 담을 data class을 정의
//    (로딩 상태, 오류 메시지 등 UI 상태 전체를 포함합니다.)
data class MyPageUiState(
    val user: User? = null,
    val babies: List<Baby> = emptyList(),
    val group: Group? = null, // 그룹 정보
    val groupMembers: List<GroupMember> = emptyList(),
    val hasGroup: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // 다이얼로그 상태
    val showGroupEditDialog: Boolean = false,
    val editingGroupName: String = "",
    // 초대 다이얼로그 상태
    val showRoleSelectDialog: Boolean = false,
    val showInviteCodeDialog: Boolean = false,
    val currentInviteCode: String = ""
)

private data class MyPageDataState(
    val user: User? = null,
    val babies: List<Baby> = emptyList(),
    val group: Group? = null,
    val groupMembers: List<GroupMember> = emptyList(),
    val hasGroup: Boolean = false,
    val isLoading: Boolean = true
)

private data class MyPageLocalState(
    val showGroupEditDialog: Boolean = false,
    val editingGroupName: String = "",
    val showRoleSelectDialog: Boolean = false,
    val showInviteCodeDialog: Boolean = false,
    val currentInviteCode: String = "",
    val selectedRoleIsMember: Boolean = true // true: Member, false: Viewer
)

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository, // 유저
    babyRepository: BabyRepository, // 아기정보
    private val groupRepository: GroupRepository // 그룹 데이터
) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    private val _localState = MutableStateFlow(MyPageLocalState())
    private val _refreshTrigger = MutableStateFlow(0)

    // 데이터 스트림: 유저, 아기, 그룹 정보 (네트워크 호출 포함)
    private val _dataState: Flow<MyPageDataState> = combine(
        userRepository.authState,
        babyRepository.getBabyStream(),
        _refreshTrigger
    ) { authState, babyList, _ ->
        Pair(authState, babyList)
    }.flatMapLatest { (authState, babyList) ->
        flow {
            val user = if (authState is AuthState.LoggedIn) authState.user else null
            emit(MyPageDataState(user = user, babies = babyList, isLoading = true))

            // 그룹 정보와 멤버 목록을 모두 가져옵니다.
            val groupResult = groupRepository.getMyGroup()
            val group = groupResult.getOrNull()

            val members = if (group != null) {
                groupRepository.getGroupMembers().getOrNull() ?: emptyList()
            } else {
                emptyList()
            }
            // 역할(OWNER, MEMBER, VIEWER) 순으로 정렬
            val sortedMembers = members.sortedBy { it.role.ordinal }

            emit(
                MyPageDataState(
                    user = user,
                    babies = babyList,
                    group = group,
                    groupMembers = sortedMembers,
                    hasGroup = group != null,
                    isLoading = false
                )
            )
        }
    }

    val uiState: StateFlow<MyPageUiState> = combine(
        _dataState,
        _localState
    ) { data, local ->
        MyPageUiState(
            user = data.user,
            babies = data.babies,
            group = data.group,
            groupMembers = data.groupMembers,
            hasGroup = data.hasGroup,
            isLoading = data.isLoading,
            showGroupEditDialog = local.showGroupEditDialog,
            editingGroupName = local.editingGroupName,
            showRoleSelectDialog = local.showRoleSelectDialog,
            showInviteCodeDialog = local.showInviteCodeDialog,
            currentInviteCode = local.currentInviteCode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MyPageUiState(isLoading = true)
    )

    fun openGroupEditDialog() {
        val currentGroup = uiState.value.group
        _localState.update {
            it.copy(
                showGroupEditDialog = true,
                editingGroupName = currentGroup?.name ?: ""
            )
        }
    }

    fun closeGroupEditDialog() {
        _localState.update { it.copy(showGroupEditDialog = false) }
    }

    fun onGroupNameChange(newName: String) {
        _localState.update { it.copy(editingGroupName = newName) }
    }

    fun saveGroupName() {
        val newName = _localState.value.editingGroupName
        if (newName.isBlank()) return // validation

        viewModelScope.launch {
            groupRepository.updateGroupName(newName)
            // 성공하면 다이얼로그 닫기 + 데이터 새로고침
            closeGroupEditDialog()
            _refreshTrigger.value += 1
        }
    }

    // --- 초대 관련 로직 ---

    fun onAddNewMemberClick() {
        _localState.update { it.copy(showRoleSelectDialog = true) }
    }

    fun onRoleSelected(isMember: Boolean) {
        _localState.update { 
            it.copy(
                showRoleSelectDialog = false, 
                selectedRoleIsMember = isMember
            ) 
        }
        fetchInviteCode(isMember)
    }

    private fun fetchInviteCode(isMember: Boolean) {
        viewModelScope.launch {
            // Loading state handling if needed
            val result = groupRepository.getInvites()
            result.onSuccess { inviteCode ->
                val code = if (isMember) inviteCode.codeMember else inviteCode.codeViewer
                _localState.update {
                    it.copy(
                        showInviteCodeDialog = true,
                        currentInviteCode = code
                    )
                }
            }.onFailure {
                // handle error?
            }
        }
    }
    
    fun refreshInviteCode() { // 이름 변경: onRefreshInviteCode -> refreshInviteCode 로 통일
        val isMember = _localState.value.selectedRoleIsMember
        viewModelScope.launch {
            val result = groupRepository.refreshInvites()
            result.onSuccess { inviteCode ->
                val code = if (isMember) inviteCode.codeMember else inviteCode.codeViewer
                _localState.update { it.copy(currentInviteCode = code) }
            }
        }
    }

    fun closeInviteDialogs() {
        _localState.update {
            it.copy(
                showRoleSelectDialog = false,
                showInviteCodeDialog = false
            )
        }
    }
}
