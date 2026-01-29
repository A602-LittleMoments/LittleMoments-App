package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.background // 💡 background 오류 해결
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer // 💡 graphicsLayer 오류 해결
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.component.*
import com.a602.commonproject.model.data.*

@Composable
fun GroupChangeScreen(
    members: List<GroupMember>,
    onBackClick: () -> Unit = {}
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showCodeDialog by remember { mutableStateOf(false) }
    var showChangeDialog by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<GroupMember?>(null) }

    // 현재 어떤 팝업이라도 떠 있는지 체크
    val isAnyDialogOpen = showAddDialog || showCodeDialog || showChangeDialog

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 메인 화면 레이어
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // 팝업이 뜨면 화면을 살짝 투명하게 하여 뒤로 물러난 효과를 줌
                    alpha = if (isAnyDialogOpen) 0.5f else 1f
                }
        ) {
            GroupManagementScreen(
                members = members,
                onBackClick = onBackClick,
                onRoleEditClick = { nickname ->
                    selectedMember = members.find { it.nickname == nickname }
                    showChangeDialog = true
                },
                onAddMemberClick = {
                    showAddDialog = true
                }
            )
        }

        // 2. 배경 딤(Dim) 처리 레이어: 팝업이 뜰 때만 나타남
        if (isAnyDialogOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = color3.copy(alpha = 0.4f)) // 어두운 색으로 덮음
            )
        }

        // 3. 각 상황에 맞는 팝업 노출 로직
        if (showAddDialog) {
            GroupRoleSelectDialog(
                onDismiss = { showAddDialog = false },
                onCloseClick = { showAddDialog = false },
                onConfirm = { isMember ->
                    showAddDialog = false
                    showCodeDialog = true
                }
            )
        }

        if (showCodeDialog) {
            GroupCodeDialog(
                onDismissRequest = { showCodeDialog = false },
                onRefreshClick = { println("코드가 새로고침되었습니다.") }
            )
        }

        if (showChangeDialog && selectedMember != null) {
            GroupRoleChangeDialog(
                currentRole = selectedMember!!.role.name,
                onDismiss = { showChangeDialog = false },
                onCloseClick = { showChangeDialog = false },
                onConfirm = { isMember -> showChangeDialog = false }
            )
        }
    }
}

// --- 프리뷰 영역 ---

@Preview(showBackground = true, name = "메인 화면")
@Composable
fun GroupChangeScreenPreview() {
    LMTheme {
        val sampleMembers = listOf(
            GroupMember(userId = "1", nickname = "엄마", relation = "엄마", role = GroupRole.OWNER),
            GroupMember(userId = "2", nickname = "아빠", relation = "아빠", role = GroupRole.MEMBER),
            GroupMember(userId = "3", nickname = "언니", relation = "언니", role = GroupRole.VIEWER)
        )
        GroupChangeScreen(members = sampleMembers)
    }
}
