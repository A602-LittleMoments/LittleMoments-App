package com.a602.commonproject.feature.login.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.LMEditInputField

/**
 * 그룹 생성 화면 (Main Content)
 * - 이미 회원가입이 완료된 상태에서 보여짐
 * - 오직 "그룹" 관련 정보만 입력받음
 */
@Composable
fun CreateGroupContent(
    onCreateGroup: (String, String) -> Unit,
    onOpenJoinDialog: () -> Unit,
) {
    var groupName by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly // Center vertically
    ) {

        Text(
            text = "그룹 정보를 입력해주세요",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 28.sp),
            color = Color.White,
            modifier = Modifier
        )

        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.9f),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Group Name
                Text(
                    text = "그룹 이름",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = com.a602.commonproject.designsystem.theme.NavyBlue,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 4.dp)
                )
                LMEditInputField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = "",
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(16.dp))

                // Relation
                Text(
                    text = "나의 역할 (예: 아빠)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = com.a602.commonproject.designsystem.theme.NavyBlue,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 4.dp)
                )
                LMEditInputField(
                    value = relation,
                    onValueChange = { relation = it },
                    label = "",
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(32.dp))

                FilledButton(
                    text = "그룹 생성",
                    onClick = {
                        onCreateGroup(groupName, relation)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    size = ButtonSize.Full,
                    enabled = groupName.isNotBlank() && relation.isNotBlank(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onOpenJoinDialog) {
                    Text("초대 코드가 있으신가요? 그룹 참여하기", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateGroupContentPreview() {
    com.a602.commonproject.designsystem.theme.LMTheme {
        CreateGroupContent(
            onCreateGroup = { _, _ -> },
            onOpenJoinDialog = {}
        )
    }
}


