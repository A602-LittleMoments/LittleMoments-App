package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color2
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.errorRed
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.main

@Composable
fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        containerColor = background,
        icon = {
            Icon(Icons.Default.Error,
                "error icon",
                tint = LMDialogDefaults.iconTint()
            )
        },
        title = {
            Text(
                text = "삭제하시겠습니까?",
                color = LMDialogDefaults.titleColor(),
                style = MaterialTheme.typography.headlineLarge
            )
        },
        text = {
            Text(
                text = "삭제된 항목은 복구할 수 없습니다.",
                modifier = Modifier.fillMaxWidth(),
                color = LMDialogDefaults.textColor(),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center

            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    "삭제",
                    color = LMDialogDefaults.confirmTextColor(),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "취소",
                    color = LMDialogDefaults.dismissTextColor(),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

object LMDialogDefaults {

    @Composable
    fun iconTint(): Color = errorRed

    @Composable
    fun titleColor(): Color = color3

    @Composable
    fun textColor(): Color = color3

    @Composable
    fun confirmTextColor(): Color = MaterialTheme.colorScheme.error

    @Composable
    fun dismissTextColor(): Color = color4
}
@Preview(showBackground = true)
@Composable
fun ConfirmDeleteDialogPreview() {
    LMTheme {
        ConfirmDeleteDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}


//그룹원 추가
@Composable
fun RoleItem(
    icon: ImageVector,
    title: String,
    desc: String,
    selected: Boolean,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (selected) 8.dp else 2.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected)
                    LMGroupDialogDefaults.selectedBorderColor()
                else
                    Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LMGroupDialogDefaults.iconTint()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column{
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = LMGroupDialogDefaults.titleColor()
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.labelMedium,
                color = LMGroupDialogDefaults.descColor()
            )
        }
    }
}


@Composable
fun GroupRoleSelectDialog(
    closeIcon: ImageVector= Icons.Default.Close,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit,
    onCloseClick: () -> Unit
) {
    var isMemberSelected by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 28.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // 원형 아이콘
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .offset(y = (-28).dp)
                    .zIndex(1f)
                    .background(
                        LMGroupDialogDefaults.iconBackgroundColor(),
                        CircleShape
                    )
                    .border(
                        BorderStroke(3.dp, LMGroupDialogDefaults.borderColor()),
                        CircleShape
                    ),
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
                    .background(
                        LMGroupDialogDefaults.containerColor(),
                        RoundedCornerShape(28.dp)
                    )
                    .border(
                        BorderStroke(3.dp, LMGroupDialogDefaults.borderColor()),
                        RoundedCornerShape(28.dp)
                    )
                    .padding(20.dp),
            ) {

                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "그룹원 추가 코드",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = onCloseClick,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = closeIcon,
                            contentDescription = "닫기",
                            tint = color3
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))

                RoleItem(
                    icon = Icons.Outlined.Lock,
                    title = "멤버",
                    desc = "편집 및 업로드 가능",
                    selected = isMemberSelected,
                    backgroundColor = LMGroupDialogDefaults.memberBackgroundColor()
                ) {
                    isMemberSelected = true
                }

                Spacer(Modifier.height(12.dp))

                RoleItem(
                    icon = LMicons.Visibility,
                    title = "뷰어",
                    desc = "보기만 가능",
                    selected = !isMemberSelected,
                    backgroundColor = LMGroupDialogDefaults.viewerBackgroundColor()

                ) {
                    isMemberSelected = false
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { onConfirm(isMemberSelected) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LMGroupDialogDefaults.confirmButtonColor()
                    )
                ) {
                    Text("생성하기")
                }
            }
        }
    }
}


object LMGroupDialogDefaults{

    @Composable
    fun containerColor(): Color = background

    @Composable
    fun borderColor(): Color = color2
    @Composable
    fun iconBackgroundColor(): Color = color2


    @Composable
    fun memberBackgroundColor(): Color = Color(0xFFE9D4FF)

    @Composable
    fun viewerBackgroundColor(): Color = Color(0xFFFCCEE8)

    @Composable
    fun selectedBorderColor(): Color = Color(0xFF89A5FF)


    @Composable
    fun iconTint(): Color = Color(0xFFFF46E2)


    @Composable
    fun titleColor(): Color = color3

    @Composable
    fun descColor(): Color = color4


    @Composable
    fun confirmButtonColor(): Color = main
}

@Preview(showBackground = true)
@Composable
fun GroupRoleSelectDialogPreview() {
    LMTheme {
        GroupRoleSelectDialog(
            onDismiss = {},
            onConfirm = {},
            onCloseClick={}
        )
    }
}

