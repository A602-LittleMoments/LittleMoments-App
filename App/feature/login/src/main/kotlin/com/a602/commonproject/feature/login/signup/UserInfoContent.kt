package com.a602.commonproject.feature.login.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var email by remember { mutableStateOf(uiState.email) }
    var password by remember { mutableStateOf(uiState.password) }
    var nickname by remember { mutableStateOf(uiState.nickname) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.littlemoments_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(250.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        LMEditInputField(
            value = email,
            onValueChange = { email = it },
            label = "이메일",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(Modifier.height(8.dp))

        LMEditInputField(
            value = password,
            onValueChange = { password = it },
            label = "비밀번호",
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        Spacer(Modifier.height(8.dp))

        LMEditInputField(
            value = nickname,
            onValueChange = { nickname = it },
            label = "닉네임",
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.weight(1f))

        FilledButton(
            text = "가입하기",
            onClick = { onSignUp(email, password, nickname) },
            modifier = Modifier.fillMaxWidth(),
            size = ButtonSize.Full,
            enabled = email.isNotBlank() && password.isNotBlank() && nickname.isNotBlank(),
        )
        Spacer(Modifier.height(24.dp))
    }
}
