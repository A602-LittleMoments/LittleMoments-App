package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.component.GroupRoleSelectDialog
import androidx.compose.ui.tooling.preview.Preview
// 💡 데이터 모델 인식을 위해 반드시 필요한 임포트
import com.a602.commonproject.model.data.*

//@Composable
//fun GroupChangeScreen(
//    // 💡 메인에서 온 데이터를 받아서
//    members: List<GroupMember>,
//    onBackClick: () -> Unit = {}
//) {
//    var showRoleDialog by remember { mutableStateOf(false) }
//    var selectedMemberName by remember { mutableStateOf("") }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        GroupManagementScreen(
//            // 💡 상세 화면으로 다시 배달합니다!
//            members = members,
//            onBackClick = onBackClick,
//            onRoleEditClick = { name ->
//                selectedMemberName = name
//                showRoleDialog = true
//            }
//        )
//
//        if (showRoleDialog) {
//            GroupRoleSelectDialog(
//                onDismiss = { showRoleDialog = false },
//                onCloseClick = { showRoleDialog = false },
//                onConfirm = { showRoleDialog = false }
//            )
//        }
//    }
//}
//
//// 💡 3. 프리뷰에서도 샘플 데이터를 넣어줘야 빨간 줄이 생기지 않습니다.
//@Preview(showBackground = true, name = "1. 초기 화면 (팝업 없음)", widthDp = 360, heightDp = 800)
//@Composable
//fun GroupChangeScreenPreview() {
//    NiaTheme {
//        GroupChangeScreen(
//            // SampleData에 정의된 그룹 멤버 리스트를 넣어줍니다.
//            members = SampleData.group.members,
//            onBackClick = {}
//        )
//    }
//}
//
//@Preview(showBackground = true, name = "2. 권한 변경 팝업 노출 상태", widthDp = 360, heightDp = 800)
//@Composable
//fun GroupRoleDialogPreview() {
//    NiaTheme {
//        Box(modifier = Modifier.fillMaxSize()) {
//            // 배경에도 샘플 데이터를 주입해줍니다.
//            GroupManagementScreen(
//                members = SampleData.group.members
//            )
//
//            GroupRoleSelectDialog(
//                onDismiss = {},
//                onCloseClick = {},
//                onConfirm = {}
//            )
//        }
//    }
//}
