package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.model.data.User

@Composable
fun ProfileEditScreen(
    user: User,
    onSaveClick: (User) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    // 화면 맨 위에 추가
    var isVerified by remember { mutableStateOf(false) } // 처음에는 미확인 상태
    // ✅ [수정] 한글 입력 문제 해결을 위해 String 대신 TextFieldValue 사용
    // var nameValue by remember { mutableStateOf(user.nickname) }
    var nameValue by remember { mutableStateOf(TextFieldValue(user.nickname)) }
    // var nicknameValue by remember { mutableStateOf(user.nickname) }
    var nicknameValue by remember { mutableStateOf(TextFieldValue(user.nickname)) }
    // var emailValue by remember { mutableStateOf(user.email) }
    var emailValue by remember { mutableStateOf(TextFieldValue(user.email)) }

    // var currentPassword by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf(TextFieldValue("")) }
    // var newPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf(TextFieldValue("")) }
    // var confirmPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        containerColor = background,
        topBar = {
            LMTopAppBar(title = "내 정보 수정", onNavigationClick = onBackClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 🟢 기본 정보 입력 (이름, 닉네임, 이메일)
            EditInputField(label = "이름", value = nameValue, onValueChange = { nameValue = it }, icon = LMicons.Person)
            Spacer(modifier = Modifier.height(16.dp))
            EditInputField(label = "닉네임", value = nicknameValue, onValueChange = { nicknameValue = it }, icon = LMicons.Person)
            Spacer(modifier = Modifier.height(16.dp))
            EditInputField(label = "이메일", value = emailValue, onValueChange = { emailValue = it }, icon = LMicons.Email)

            Spacer(modifier = Modifier.height(32.dp))
            Divider(color = color4.copy(alpha = 0.2f)) // 구분선 추가
            Spacer(modifier = Modifier.height(32.dp))

            // ✅ 3. 비밀번호 변경 단계별 화면 제어
            if (!isVerified) {
                // 🛑 1단계: 현재 비밀번호를 먼저 확인받아야 함
                EditInputField(
                    label = "비밀번호 변경을 위해 현재 비번 입력",
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    icon = Icons.Outlined.Lock,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // 💡 임시 로직: 입력값이 비어있지 않으면 확인된 것으로 간주 (내일 API 연결)
                        if (currentPassword.text.isNotEmpty()) { isVerified = true }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = color4),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("현재 비밀번호 확인", color = lightbackground)
                }
            } else {
                // 🟢 2단계: 확인이 완료되면 새 비밀번호 입력창 등장
                EditInputField(label = "새 비밀번호", value = newPassword, onValueChange = { newPassword = it }, icon = Icons.Outlined.Lock, isPassword = true)
                Spacer(modifier = Modifier.height(16.dp))
                EditInputField(label = "새 비밀번호 확인", value = confirmPassword, onValueChange = { confirmPassword = it }, icon = Icons.Outlined.Lock, isPassword = true)

                // 확인 완료 메시지
                Text("✅ 현재 비밀번호가 확인되었습니다.", style = AppTypography.labelSmall, color = main, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // 최종 저장 버튼
            Button(
                onClick = {
                    val updatedUser = user.copy(
//                        name = nameValue.text,
                        nickname = nicknameValue.text,
                        email = emailValue.text
                    )
                    onSaveClick(updatedUser)

                    // 새 비밀번호가 있고 확인까지 끝났다면 서버에 변경 요청 (UserRepository 호출 예정)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = main)
            ) {
                Text(text = "최종 저장하기", color = lightbackground, style = AppTypography.labelLarge)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun EditInputField(
    label: String,
    // ✅ [수정] 타입을 TextFieldValue로 변경
    // value: String,
    value: TextFieldValue,
    // onValueChange: (String) -> Unit,
    onValueChange: (TextFieldValue) -> Unit,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = lightbackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, color4)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                textStyle = AppTypography.titleMedium.copy(color = color4),
                label = { Text(label, style = AppTypography.labelMedium, color = color4.copy(alpha = 0.6f)) },
                visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = color4,
                    unfocusedTextColor = color4,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color4.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "내 정보 수정 미리보기")
@Composable
fun ProfileEditScreenPreview() {
    LtTheme {
        ProfileEditScreen(
            user = User(id = "1", email = "lilly@example.com", nickname = "Lilly"),
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
