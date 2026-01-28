package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.lightbackground
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import com.a602.commonproject.designsystem.component.LMTopAppBar
// 💡 데이터 모델 인식을 위한 임포트
import com.a602.commonproject.model.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    user: User,
    onSaveClick: (User) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    // 💡 1. 초기값을 SampleData(user)에서 가져와서 '수정창'으로 만듭니다.
    var nameValue by remember { mutableStateOf(user.nickname) }
    var nicknameValue by remember { mutableStateOf(user.nickname) }
    var emailValue by remember { mutableStateOf(user.email) }

    // 💡 비밀번호도 이제 빈 칸이 아니라 기존 데이터를 불러옵니다.
    // (User 모델에 password가 없다면 임시로 "1234" 등을 넣거나 모델에 추가해야 합니다.)
    var passwordValue by remember { mutableStateOf("") }

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
            Spacer(modifier = Modifier.height(60.dp))

            EditInputField(label = "이름", value = nameValue, onValueChange = { nameValue = it }, icon = Icons.Outlined.Person)
            Spacer(modifier = Modifier.height(16.dp))

            EditInputField(label = "닉네임", value = nicknameValue, onValueChange = { nicknameValue = it }, icon = Icons.Outlined.Person)
            Spacer(modifier = Modifier.height(16.dp))

            EditInputField(label = "이메일", value = emailValue, onValueChange = { emailValue = it }, icon = Icons.Outlined.Email)
            Spacer(modifier = Modifier.height(16.dp))

            // 💡 2. 비밀번호 수정창 (보안 적용)
            OutlinedTextField(
                value = passwordValue,
                onValueChange = { passwordValue = it },
                label = { Text("비밀번호 수정", color = color4.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(), // ⭐ 점(••••)으로 가려줌
                trailingIcon = {
                    Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = color4.copy(alpha = 0.4f))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = lightbackground,
                    unfocusedContainerColor = lightbackground,
                    focusedBorderColor = main,
                    unfocusedBorderColor = color4
                )
            )

            Spacer(modifier = Modifier.height(60.dp))

            Button(
                onClick = {
                    // 💡 3. 모든 수정된 값을 담아서 저장 버튼 클릭 시 보냅니다.
                    val updatedUser = user.copy(
                        nickname = nicknameValue,
                        email = emailValue
                        // password 필드가 모델에 있다면 여기에 추가: password = passwordValue
                    )
                    onSaveClick(updatedUser)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = main)
            ) {
                Text(text = "저장하기", color = lightbackground, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EditInputField(label: String, value: String, onValueChange: (String) -> Unit, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = color4.copy(alpha = 0.6f)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        trailingIcon = {
            Icon(imageVector = icon, contentDescription = null, tint = color4.copy(alpha = 0.4f))
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = lightbackground,
            unfocusedContainerColor = lightbackground,
            focusedBorderColor = main,
            unfocusedBorderColor = color4
        )
    )
}

/*@Preview(showBackground = true, name = "내 정보 확인 미리보기")
@Composable
fun ProfileDetailPreview() {
    // 💡 4. 미리보기에서도 모델 규격(id 등)을 지켜서 데이터를 넣어줍니다.
    ProfileEditScreen(
        user = User(
            id = "user_123",
            username = "홍길동",
            nickname = "가나다",
            email = "abc@naver.com",
            password = "qwer123",
            profileImageUrl = null
        )
    )
}*/
