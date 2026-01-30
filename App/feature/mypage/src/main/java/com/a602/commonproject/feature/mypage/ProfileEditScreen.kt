package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.a602.commonproject.designsystem.component.LMEditInputField
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.mypage.viewmodel.ProfileEditViewModel
import com.a602.commonproject.navigation.Navigator


@Composable
fun ProfileEditContainer(
    navigator: Navigator,
    viewModel: ProfileEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 저장 성공 시 처리
    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            navigator.goBack() // 이전 화면으로 이동
            viewModel.onSaveSuccessConsumed() // 성공 상태 리셋
        }
    }

    // 에러 메시지 표시
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    ProfileEditScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNicknameChanged = viewModel::onNicknameChanged,
        onCurrentPasswordChanged = viewModel::onCurrentPasswordChanged,
        onNewPasswordChanged = viewModel::onNewPasswordChanged,
        onConfirmNewPasswordChanged = viewModel::onConfirmNewPasswordChanged,
        onSaveClick = viewModel::saveProfile,
        onBackClick = { navigator.goBack() }
    )
}

@Composable
fun ProfileEditScreen(
    uiState: com.a602.commonproject.feature.mypage.viewmodel.ProfileEditUiState,
    snackbarHostState: SnackbarHostState,
    onNicknameChanged: (String) -> Unit,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmNewPasswordChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = background,
        topBar = {
            LMTopAppBar(title = "내 정보 수정", onNavigationClick = onBackClick)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // 기본 정보 입력
                LMEditInputField(label = "닉네임", value = uiState.nickname, onValueChange = onNicknameChanged, icon = LMicons.Person)
                Spacer(modifier = Modifier.height(16.dp))
                LMEditInputField(label = "이메일", value = uiState.email, onValueChange = {}, icon = LMicons.Email, enabled = false)

                Spacer(modifier = Modifier.height(32.dp))
                Divider(color = color4.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(32.dp))

                // 비밀번호 변경
                LMEditInputField(
                    label = "현재 비밀번호",
                    value = uiState.currentPassword,
                    onValueChange = onCurrentPasswordChanged,
                    icon = Icons.Outlined.Lock,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                LMEditInputField(label = "새 비밀번호", value = uiState.newPassword, onValueChange = onNewPasswordChanged, icon = Icons.Outlined.Lock, isPassword = true)
                Spacer(modifier = Modifier.height(16.dp))
                LMEditInputField(
                    label = "새 비밀번호 확인",
                    value = uiState.confirmNewPassword,
                    onValueChange = onConfirmNewPasswordChanged,
                    icon = Icons.Outlined.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.weight(1f))

                // 최종 저장 버튼
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = main)
                ) {
                    Text(text = "저장하기", color = lightbackground, style = AppTypography.labelLarge)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true, name = "내 정보 수정 미리보기")
@Composable
fun ProfileEditScreenPreview() {
    LMTheme {
        ProfileEditScreen(
            uiState = com.a602.commonproject.feature.mypage.viewmodel.ProfileEditUiState(email = "lilly@example.com", nickname = "Lilly"),
            snackbarHostState = remember { SnackbarHostState() },
            onNicknameChanged = {},
            onCurrentPasswordChanged = {},
            onNewPasswordChanged = {},
            onConfirmNewPasswordChanged = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
