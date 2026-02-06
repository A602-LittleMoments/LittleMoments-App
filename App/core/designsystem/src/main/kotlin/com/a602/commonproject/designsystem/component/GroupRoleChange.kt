package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.NavyBlue
import com.a602.commonproject.designsystem.theme.OffWhite
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.R

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
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = OffWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "역할 선택",
                        style = AppTypography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavyBlue,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                    IconButton(
                        onClick = onCloseClick,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = NavyBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 💡 사진사 (멤버) 선택지
                val isMemberDisabled = currentRole == "MEMBER"
                SelectionCard(
                    title = "사진사",
                    description = "사진을 찍고 공유하는 역할",
                    iconResId = R.drawable.camera,
                    isSelected = isMemberSelected,
                    onClick = { if (!isMemberDisabled) isMemberSelected = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 💡 관찰자 (뷰어) 선택지
                val isViewerDisabled = currentRole == "VIEWER"
                SelectionCard(
                    title = "관찰자",
                    description = "공유된 사진을 보는 역할",
                    iconResId = R.drawable.telescope,
                    isSelected = !isMemberSelected,
                    onClick = { if (!isViewerDisabled) isMemberSelected = false }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onConfirm(isMemberSelected) },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "코드 생성",
                        style = AppTypography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SelectionCard(
    title: String,
    description: String,
    iconResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // 이미지가 흰 배경이 있을 수 있으므로 항상 흰색 유지
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) NavyBlue else Color.Gray.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘 이미지
            // 드로우블 이미지 (크기 조정)
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = AppTypography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    style = AppTypography.bodyMedium.copy(
                        color = NavyBlue.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupRoleChangeDialogPreview() {
    LMTheme {
        GroupRoleChangeDialog(
            currentRole = "MEMBER",
            onDismiss = { },
            onCloseClick = { },
            onConfirm = { }
        )
    }
}
