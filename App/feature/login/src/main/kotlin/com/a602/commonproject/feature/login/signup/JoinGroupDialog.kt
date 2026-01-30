package com.a602.commonproject.feature.login.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.LMEditInputField
import com.a602.commonproject.designsystem.theme.main

/**
 * 그룹 참여 다이얼로그
 * - 오직 "코드 참여" 기능만 담당함
 */
@Composable
fun JoinGroupDialog(
    onDismissRequest: () -> Unit,
    onJoinGroup: (String, String) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("그룹 참여하기", style = MaterialTheme.typography.titleMedium, color = main)
                Spacer(modifier = Modifier.height(16.dp))

                JoinGroupForm(onJoinGroup)
            }
        }
    }
}

/**
 * 그룹 참여 폼입니다.
 * 초대 코드와 본인의 관계를 입력받아 기존 그룹에 참여합니다.
 */
@Composable
fun JoinGroupForm(
    onJoin: (String, String) -> Unit,
) {
    var inviteCode by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }

    Column {
        LMEditInputField(
            value = inviteCode,
            onValueChange = { inviteCode = it },
            label = "초대 코드 (6자리)",
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        LMEditInputField(
            value = relation,
            onValueChange = { relation = it },
            label = "나의 역할 (예: 엄마)",
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))

        FilledButton(
            text = "참여하기",
            onClick = { onJoin(inviteCode, relation) },
            modifier = Modifier.fillMaxWidth(),
            size = ButtonSize.Medium,
            enabled = inviteCode.isNotBlank() && relation.isNotBlank(),
        )
    }
}
