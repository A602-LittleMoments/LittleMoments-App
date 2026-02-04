package com.a602.commonproject.feature.login.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.LMEditInputField

/**
 * 사용자 기본 정보(이메일, 비밀번호, 닉네임)를 입력받는 컨텐츠 영역입니다.
 */
@Composable
fun UserInfoContent(
    uiState: SignUpUiState,
    onSignUp: (String, String, String) -> Unit,
) {
    var name by remember { mutableStateOf("") } // UI only
    var nickname by remember { mutableStateOf(uiState.nickname) }
    var email by remember { mutableStateOf(uiState.email) }
    var password by remember { mutableStateOf(uiState.password) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Validation (Simple Regex for demonstration matches design text)
    // Password: 8~20 chars, 2 of (English, Number, Special)
    // Regex is complex, simplified check:
    val passwordRegex = "^(?=.*[a-zA-Z])(?=.*[0-9])|(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9])|(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,20}$".toRegex()
    val isPasswordValid = password.isEmpty() || password.matches(passwordRegex)

    // Check for specific error messages (Naive implementation based on uiState.error)
    val emailError = uiState.error?.contains("이메일") == true || uiState.error?.contains("email") == true || uiState.error?.contains("사용중") == true
    val passwordError = !isPasswordValid

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_shadow),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp), // Slightly smaller to fit card
        )

        Spacer(modifier = Modifier.height(24.dp))

        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Name
                LMEditInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "이름",
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.Person,
                            contentDescription = null,
                            tint = com.a602.commonproject.designsystem.theme.NavyBlue
                        )
                    }
                )
                Spacer(Modifier.height(12.dp))

                // 2. Nickname
                LMEditInputField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = "닉네임",
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.Person, // Person Outline not available in default? Use Person
                            contentDescription = null,
                            tint = com.a602.commonproject.designsystem.theme.NavyBlue
                        )
                    }
                )
                Spacer(Modifier.height(12.dp))

                // 3. Email
                LMEditInputField(
                    value = email,
                    onValueChange = { email = it },
                    label = "이메일",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    trailingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.Email,
                            contentDescription = null,
                            tint = com.a602.commonproject.designsystem.theme.NavyBlue
                        )
                    },
                    isError = emailError,
                    supportingText = if (emailError) uiState.error else null
                )
                Spacer(Modifier.height(12.dp))

                // 4. Password
                LMEditInputField(
                    value = password,
                    onValueChange = { password = it },
                    label = "비밀번호",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isPassword = !isPasswordVisible,
                    trailingIcon = {
                        androidx.compose.material3.IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            androidx.compose.material3.Icon(
                                imageVector = if (isPasswordVisible) androidx.compose.material.icons.Icons.Outlined.Visibility else androidx.compose.material.icons.Icons.Outlined.VisibilityOff,
                                contentDescription = null,
                                tint = com.a602.commonproject.designsystem.theme.NavyBlue
                            )
                        }
                    },
                    isError = passwordError,
                    supportingText = if (passwordError) "영문/숫자/특수문자 중 2가지 이상 조합 (8 ~ 20자)" else null
                )
                Spacer(Modifier.height(24.dp))

                FilledButton(
                    text = "가입하기",
                    onClick = { onSignUp(email, password, nickname) },
                    modifier = Modifier.fillMaxWidth(),
                    size = ButtonSize.Full,
                    enabled = email.isNotBlank() && password.isNotBlank() && nickname.isNotBlank() && name.isNotBlank(), // Validations
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
