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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.a602.commonproject.designsystem.component.GroupCodeDialog
import com.a602.commonproject.designsystem.component.GroupRoleChangeDialog
import com.a602.commonproject.designsystem.component.GroupRoleSelectDialog
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color1
import com.a602.commonproject.designsystem.theme.color2
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.gray1
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.designsystem.theme.purple1
import com.a602.commonproject.feature.mypage.viewmodel.GroupChangeViewModel
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.GroupRole
import com.a602.commonproject.navigation.Navigator

@Composable
fun GroupChangeContainer(
    navigator: Navigator,
    viewModel: GroupChangeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isAnyDialogOpen = uiState.showRoleSelectDialog || uiState.showInviteCodeDialog || uiState.memberToEdit != null

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 메인 화면 레이어 (팝업 시 반투명 처리)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = if (isAnyDialogOpen) 0.5f else 1f)
        ) {
            GroupManagementScreen(
                members = uiState.members,
                onBackClick = { navigator.goBack() },
                onRoleEditClick = { memberId ->
                    uiState.members.find { it.userId == memberId }?.let(viewModel::onRoleEditClicked)
                },
                onAddMemberClick = viewModel::onAddMemberClicked
            )
        }

        // 2. 배경 딤(Dim) 처리 레이어
        if (isAnyDialogOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = color3.copy(alpha = 0.4f))
            )
        }

        // 3. 각 상황에 맞는 팝업 노출 로직
        if (uiState.showRoleSelectDialog) {
            GroupRoleSelectDialog(
                onDismiss = viewModel::onRoleSelectDialogDismissed,
                onCloseClick = viewModel::onRoleSelectDialogDismissed,
                onConfirm = viewModel::onRoleSelectedForInvite
            )
        }

        if (uiState.showInviteCodeDialog) {
            uiState.inviteCode?.let {
                GroupCodeDialog(
                    initialCode = it,
                    initialSeconds = uiState.inviteCodeExpiry,
                    onRefreshClick = { viewModel.onRoleSelectedForInvite(true) }, // 멤버 코드로 새로고침
                    onDismissRequest = viewModel::onInviteDialogDismissed
                )
            }
        }

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
    }
}

// --- 프리뷰 영역 ---

@Preview(showBackground = true, name = "그룹 관리 화면 프리뷰")
@Composable
fun GroupManagementScreenPreview() {
    LMTheme {
        val sampleMembers = listOf(
            GroupMember(userId = "1", nickname = "엄마", relation = "엄마", role = GroupRole.OWNER),
            GroupMember(userId = "2", nickname = "아빠", relation = "아빠", role = GroupRole.MEMBER),
            GroupMember(userId = "3", nickname = "언니", relation = "언니", role = GroupRole.VIEWER)
        )
        GroupManagementScreen(
            members = sampleMembers,
            onBackClick = {},
            onRoleEditClick = {},
            onAddMemberClick = {}
        )
    }
}

@Composable
fun GroupManagementScreen(
    members: List<GroupMember>,
    onBackClick: () -> Unit,
    onRoleEditClick: (String) -> Unit,
    onAddMemberClick: () -> Unit
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
                GroupSummaryCard(groupName = "우리 가족 그룹", description = "함께 추억을 공유해요")
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "그룹원", style = AppTypography.labelMedium, color = color3)
                    Spacer(modifier = Modifier.width(5.dp))
                    Surface(color = lightblue, shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = "${members.size}명",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = main,
                        )
                    }
                }
            }
            items(members) { member ->
                ManageableMemberItem(
                    name = member.nickname,
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
                    Text(text = "새 구성원 추가하기", style = AppTypography.labelLarge)
                }
            }
        }
    }
}

private fun getColorForRole(role: GroupRole): Color {
    return when (role) {
        GroupRole.OWNER -> main
        GroupRole.MEMBER -> color1
        GroupRole.VIEWER -> purple1
        else -> gray1
    }
}
