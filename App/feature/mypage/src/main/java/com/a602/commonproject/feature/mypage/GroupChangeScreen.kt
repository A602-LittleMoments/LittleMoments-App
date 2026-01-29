package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.component.GroupRoleSelectDialog
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.model.data.*

@Composable
fun GroupChangeScreen(
    // 메인에서 온 데이터를 받아서
    members: List<GroupMember>,
    onBackClick: () -> Unit = {}
) {
    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedMemberName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        GroupManagementScreen(
            // 💡 상세 화면으로 다시 배달합니다!
            members = members,
            onBackClick = onBackClick,
            onRoleEditClick = { name ->
                selectedMemberName = name
                showRoleDialog = true
            }
        )

        if (showRoleDialog) {
            GroupRoleSelectDialog(
                onDismiss = { showRoleDialog = false },
                onCloseClick = { showRoleDialog = false },
                onConfirm = { showRoleDialog = false }
            )
        }
    }
}

// 💡 프리뷰에서도 샘플 데이터를 넣어줘야 빨간 줄이 생기지 않습니다.
@Preview(showBackground = true, name = "1. 초기 화면 (팝업 없음)", widthDp = 360, heightDp = 800)
@Composable
fun GroupChangeScreenPreview() {
    NiaTheme {
        val sampleMembers = listOf(
            GroupMember(userId = "1", nickname = "엄마", relation = "엄마", role = GroupRole.OWNER),
            GroupMember(userId = "2", nickname = "아빠", relation = "아빠", role = GroupRole.MEMBER),
            GroupMember(userId = "3", nickname = "언니", relation = "언니", role = GroupRole.VIEWER)
        )
        GroupChangeScreen(
            members = sampleMembers,
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "2. 권한 변경 팝업 노출 상태", widthDp = 360, heightDp = 800)
@Composable
fun GroupRoleDialogPreview() {
    NiaTheme {
        val sampleMembers = listOf(
            GroupMember(userId = "1", nickname = "엄마", relation = "엄마", role = GroupRole.OWNER),
            GroupMember(userId = "2", nickname = "아빠", relation = "아빠", role = GroupRole.MEMBER),
            GroupMember(userId = "3", nickname = "언니", relation = "언니", role = GroupRole.VIEWER)
        )
        Box(modifier = Modifier.fillMaxSize()) {
            // 배경에도 샘플 데이터를 주입해줍니다.
            GroupManagementScreen(
                members = sampleMembers
            )

            GroupRoleSelectDialog(
                onDismiss = {},
                onCloseClick = {},
                onConfirm = {}
            )
        }
    }
}
