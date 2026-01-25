package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.errorRed

//@Composable
//fun AlertDialogExample(
//    onDismissRequest: () -> Unit,
//    onConfirmation: () -> Unit,
//    dialogTitle: String,
//    dialogText: String,
//    icon: ImageVector,
//) {
//    AlertDialog(
//        icon = {
//            Icon(Icons.Default.Error, "error icon")
//        },
//        title = {
//            Text(text = dialogTitle)
//        },
//        text = {
//            Text(text = dialogText)
//        },
//        onDismissRequest = {
//            onDismissRequest()
//        },
//        confirmButton = {
//            TextButton(
//                onClick = {
//                    onConfirmation()
//                }
//            ) {
//                Text("Confirm")
//            }
//        },
//        dismissButton = {
//            TextButton(
//                onClick = {
//                    onDismissRequest()
//                }
//            ) {
//                Text("Dismiss")
//            }
//        }
//    )
//}

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
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = "삭제된 항목은 복구할 수 없습니다.",
                modifier = Modifier.fillMaxWidth(),
                color = LMDialogDefaults.textColor(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center

            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    "삭제",
                    color = LMDialogDefaults.confirmTextColor()
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "취소",
                    color = LMDialogDefaults.dismissTextColor()
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
    MaterialTheme {
        ConfirmDeleteDialog(
            onDismiss = {},
            onConfirm = {}
        )
    }
}
