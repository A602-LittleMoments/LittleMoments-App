package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.GroupCodeDialog
import com.a602.commonproject.designsystem.component.GroupRoleChangeDialog
import com.a602.commonproject.designsystem.component.GroupRoleSelectDialog
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.errorRed
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.mypage.viewmodel.GroupChangeViewModel
import com.a602.commonproject.model.data.Group
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.GroupRole
import com.a602.commonproject.navigation.Navigator

@Composable
fun GroupChangeContainer(
    navigator: Navigator,
    viewModel: GroupChangeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isAnyDialogOpen = uiState.showRoleSelectDialog || uiState.showInviteCodeDialog || uiState.memberToEdit != null || uiState.showEditNameDialog || uiState.showLeaveGroupConfirmDialog

    // 그룹 나가기 성공 시, 마이페이지로 복귀
    LaunchedEffect(uiState.isLeaveSuccess) {
        if(uiState.isLeaveSuccess) {
            navigator.goBack()
            viewModel.onLeaveSuccessConsumed()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = if (isAnyDialogOpen) 0.5f else 1f)
        ) {
            GroupManagementScreen(
                group = uiState.group,
                members = uiState.members,
                onBackClick = { navigator.goBack() },
                onRoleEditClick = { memberId ->
                    uiState.members.find { it.userId == memberId }?.let(viewModel::onRoleEditClicked)
                },
                onAddMemberClick = viewModel::onAddMemberClicked,
                onEditGroupNameClick = viewModel::onEditGroupNameClicked,
                onLeaveGroupClick = viewModel::onLeaveGroupClicked
            )
        }

        // 배경 딤(Dim) 처리 레이어
        if (isAnyDialogOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = color3.copy(alpha = 0.4f))
            )
        }

        // 역할 선택 팝업
        if (uiState.showRoleSelectDialog) {
            GroupRoleSelectDialog(
                onDismiss = viewModel::onRoleSelectDialogDismissed,
                onCloseClick = viewModel::onRoleSelectDialogDismissed,
                onConfirm = viewModel::onRoleSelectedForInvite
            )
        }

        // 초대 코드 팝업
        if (uiState.showInviteCodeDialog) {
            uiState.inviteCode?.let {
                GroupCodeDialog(
                    initialCode = it,
                    initialSeconds = uiState.inviteCodeExpiry,
                    onRefreshClick = viewModel::refreshInviteCode,
                    onDismissRequest = viewModel::onInviteDialogDismissed
                )
            }
        }

        // 멤버 역할 변경 팝업
        uiState.memberToEdit?.let { member ->
            GroupRoleChangeDialog(
                currentRole = member.role.name,
                onDismiss = viewModel::onRoleChangeDialogDismissed,
                onCloseClick = viewModel::onRoleChangeDialogDismissed,
                onConfirm = { isMember ->
                    val newRole = if (isMember) GroupRole.MEMBER else GroupRole.VIEWER
                    viewModel.updateMemberRole(newRole)
                }
            )
        }

        // 그룹 이름 수정 팝업
        if (uiState.showEditNameDialog) {
            GroupNameEditDialog(
                currentName = uiState.group?.name ?: "",
                onDismiss = viewModel::onEditGroupNameDismissed,
                onConfirm = { newName ->
                    viewModel.updateGroupName(newName)
                }
            )
        }

        // 그룹 나가기 확인 팝업
        if (uiState.showLeaveGroupConfirmDialog) {
            AlertDialog(
                onDismissRequest = viewModel::onLeaveGroupDismissed,
                title = { Text("그룹 나가기") },
                text = { Text("정말로 그룹을 나가시겠습니까?\n이 작업은 되돌릴 수 없습니다.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.leaveGroup() }) {
                        Text("나가기")
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::onLeaveGroupDismissed) {
                        Text("취소")
                    }
                }
            )
        }
    }
}

@Composable
fun GroupManagementScreen(
    group: Group?,
    members: List<GroupMember>,
    onBackClick: () -> Unit,
    onRoleEditClick: (String) -> Unit,
    onAddMemberClick: () -> Unit,
    onEditGroupNameClick: () -> Unit,
    onLeaveGroupClick: () -> Unit
) {
    Scaffold(
        containerColor = background,
        topBar = { LMTopAppBar(title = "그룹 구성원 관리", onNavigationClick = onBackClick) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            item {
                if (group != null) {
                    GroupSummaryCard(
                        groupName = group.name,
                        description = "함께 추억을 공유해요",
                        onEditClick = onEditGroupNameClick
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(text = "그룹원", style = MaterialTheme.typography.labelLarge, color = color3)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = lightblue, shape = RoundedCornerShape(10.dp)) {
                        Text(
                            text = "${members.size}명",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = main,
                        )
                    }
                }
            }
            items(members) { member ->
                ManageableMemberItem(
                    name = member.nickname,
                    groupName = group?.name ?: "내 그룹",
                    role = member.role.name,
                    color = getColorForRole(member.role),
                    isOwner = (member.role == GroupRole.OWNER),
                    onEditClick = { onRoleEditClick(member.userId) }
                )
            }
            item {
                Button(
                    onClick = onAddMemberClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = main)
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "새 구성원 추가하기", style = MaterialTheme.typography.labelLarge)
                }
            }
            item {
                PermissionGuideSection()
            }
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                    TextButton(onClick = onLeaveGroupClick) {
                        Text("그룹 나가기", color = errorRed, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GroupManagementScreenPreview() {
    LMTheme {
        val sampleGroup = Group(id = "1", name = "우리 가족 그룹", role = GroupRole.OWNER, relation = "엄마")
        val sampleMembers = listOf(
            GroupMember(userId = "1", nickname = "엄마", relation = "엄마", role = GroupRole.OWNER),
            GroupMember(userId = "2", nickname = "아빠", relation = "아빠", role = GroupRole.MEMBER),
            GroupMember(userId = "3", nickname = "언니", relation = "언니", role = GroupRole.VIEWER)
        )
        GroupManagementScreen(
            group = sampleGroup,
            members = sampleMembers,
            onBackClick = {},
            onRoleEditClick = {},
            onAddMemberClick = {},
            onEditGroupNameClick = {},
            onLeaveGroupClick = {}
        )
    }
}
