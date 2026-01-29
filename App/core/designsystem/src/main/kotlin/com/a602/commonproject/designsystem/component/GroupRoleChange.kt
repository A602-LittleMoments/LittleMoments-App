package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.component.* // RoleItem 등을 가져오기 위함
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun GroupRoleChangeDialog(
    currentRole: String, // "MEMBER" 또는 "VIEWER"
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit,
    onCloseClick: () -> Unit
) {
    // 💡 현재 권한이 아닌 쪽을 기본 선택값으로 세팅
    var isMemberSelected by remember { mutableStateOf(currentRole != "MEMBER") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 28.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // 원형 아이콘 (zIndex를 주어 맨 위로 올림)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .offset(y = (-28).dp)
                    .zIndex(2f)
                    .background(LMGroupDialogDefaults.iconBackgroundColor(), CircleShape)
                    .border(BorderStroke(3.dp, LMGroupDialogDefaults.borderColor()), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.groupdialog),
                    contentDescription = null,
                    tint = lightbackground
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LMGroupDialogDefaults.containerColor(), RoundedCornerShape(28.dp))
                    .border(BorderStroke(3.dp, LMGroupDialogDefaults.borderColor()), RoundedCornerShape(28.dp))
                    .padding(20.dp),
            ) {
                // 상단 제목 레이아웃 (양 끝 배치)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(48.dp)) // 왼쪽 여백
                    Text(
                        text = "권한 변경",
                        style = MaterialTheme.typography.titleLarge,
                        color = LMGroupDialogDefaults.titleColor(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = onCloseClick) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "닫기")
                    }
                }

                Spacer(Modifier.height(20.dp))

                // 💡 멤버 선택지 (현재 멤버면 흑백/흐림 처리)
                val isMemberDisabled = currentRole == "MEMBER"
                Box(modifier = Modifier.alpha(if (isMemberDisabled) 0.4f else 1f)) {
                    RoleItem(
                        icon = Icons.Outlined.Lock,
                        title = "멤버",
                        desc = if (isMemberDisabled) "현재 권한입니다" else "편집 및 업로드 가능",
                        selected = isMemberSelected,
                        backgroundColor = LMGroupDialogDefaults.memberBackgroundColor()
                    ) {
                        if (!isMemberDisabled) isMemberSelected = true // 💡 클릭 방지
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 💡 뷰어 선택지 (현재 뷰어면 흑백/흐림 처리)
                val isViewerDisabled = currentRole == "VIEWER"
                Box(modifier = Modifier.alpha(if (isViewerDisabled) 0.4f else 1f)) {
                    RoleItem(
                        icon = LMicons.Visibility,
                        title = "뷰어",
                        desc = if (isViewerDisabled) "현재 권한입니다" else "보기만 가능",
                        selected = !isMemberSelected,
                        backgroundColor = LMGroupDialogDefaults.viewerBackgroundColor()
                    ) {
                        if (!isViewerDisabled) isMemberSelected = false // 💡 클릭 방지
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { onConfirm(isMemberSelected) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LMGroupDialogDefaults.confirmButtonColor()
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("변경하기", style = AppTypography.labelLarge)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun GroupRoleChangeDialogPreview() {
    LtTheme {
        // 실제 앱처럼 보이기 위해 배경색이 있는 Box로 감쌉니다.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = color2.copy(alpha = 0.1f))
        ) {
            GroupRoleChangeDialog(
                // ✅ 테스트: 현재 권한이 "MEMBER"인 상황을 가정합니다.
                // 결과: '멤버' 칸은 흐릿(alpha 0.4)해지고 클릭이 안 되어야 합니다.
                currentRole = "MEMBER",
                onDismiss = { /* 팝업 바깥 클릭 시 */ },
                onCloseClick = { /* X 버튼 클릭 시 */ },
                onConfirm = { isMemberSelected ->
                    // 변경하기 버튼 클릭 시 로직
                    println("새로 선택된 권한이 멤버인가요? : $isMemberSelected")
                }
            )
        }
    }
}
