package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.a602.commonproject.designsystem.component.LMEditInputField
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.login.navigation.LoginNavKey
import com.a602.commonproject.feature.mypage.viewmodel.ProfileEditUiState
import com.a602.commonproject.feature.mypage.viewmodel.ProfileEditViewModel
import com.a602.commonproject.navigation.Navigator


@Composable
fun ProfileEditContainer(
    navigator: Navigator,
    viewModel: ProfileEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            navigator.goBack()
            viewModel.onSaveSuccessConsumed()
        }
    }

    // 비밀번호 변경 성공 시 (재로그인 필요)
    LaunchedEffect(uiState.isPasswordChanged) {
        if (uiState.isPasswordChanged) {
            snackbarHostState.showSnackbar("비밀번호가 변경되었습니다. 다시 로그인해주세요.")
            viewModel.onPasswordChangedConsumed()
            navigator.replaceRoot(LoginNavKey) // 모든 스택을 지우고 로그인 화면으로 이동
        }
    }

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
        onToggleCurrentPasswordVisibility = viewModel::onToggleCurrentPasswordVisibility,
        onToggleNewPasswordVisibility = viewModel::onToggleNewPasswordVisibility,
        onToggleConfirmPasswordVisibility = viewModel::onToggleConfirmPasswordVisibility,
        onSaveClick = viewModel::saveProfile,
        onBackClick = { navigator.goBack() }
    )
}

@Composable
fun ProfileEditScreen(
    uiState: ProfileEditUiState,
    snackbarHostState: SnackbarHostState,
    onNicknameChanged: (String) -> Unit,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmNewPasswordChanged: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = background,
        topBar = { LMTopAppBar(title = "내 정보 수정", onNavigationClick = onBackClick) },
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

                // 기본 정보
                LMEditInputField(
                    label = "닉네임",
                    value = uiState.nickname,
                    onValueChange = onNicknameChanged,
                    trailingIcon = { Icon(imageVector = LMicons.Person, contentDescription = "닉네임") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LMEditInputField(
                    label = "이메일",
                    value = uiState.email,
                    onValueChange = {},
                    trailingIcon = { Icon(imageVector = LMicons.Email, contentDescription = "이메일") },
                    enabled = false
                )

                Spacer(modifier = Modifier.height(32.dp))
                Divider(color = color4.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(32.dp))

                // 비밀번호 변경 섹션
                LMEditInputField(
                    label = "현재 비밀번호",
                    value = uiState.currentPassword,
                    onValueChange = onCurrentPasswordChanged,
                    isPassword = !uiState.isCurrentPasswordVisible,
                    trailingIcon = {
                        IconButton(onClick = onToggleCurrentPasswordVisibility) {
                            Icon(imageVector = if (uiState.isCurrentPasswordVisible) LMicons.VisibilityOff else LMicons.Visibility, contentDescription = "비밀번호 보기/숨기기")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LMEditInputField(
                    label = "새 비밀번호",
                    value = uiState.newPassword,
                    onValueChange = onNewPasswordChanged,
                    isPassword = !uiState.isNewPasswordVisible,
                    trailingIcon = {
                        IconButton(onClick = onToggleNewPasswordVisibility) {
                            Icon(imageVector = if (uiState.isNewPasswordVisible) LMicons.VisibilityOff else LMicons.Visibility, contentDescription = "비밀번호 보기/숨기기")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LMEditInputField(
                    label = "새 비밀번호 확인",
                    value = uiState.confirmNewPassword,
                    onValueChange = onConfirmNewPasswordChanged,
                    isPassword = !uiState.isConfirmPasswordVisible,
                    trailingIcon = {
                        IconButton(onClick = onToggleConfirmPasswordVisibility) {
                            Icon(imageVector = if (uiState.isConfirmPasswordVisible) LMicons.VisibilityOff else LMicons.Visibility, contentDescription = "비밀번호 보기/숨기기")
                        }
                    }
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

// Preview는 UI의 다양한 상태를 테스트하기 위해 여러 개를 만들 수 있습니다.
@Preview(showBackground = true, name = "기본 상태")
@Composable
fun ProfileEditScreenDefaultPreview() {
    LMTheme {
        ProfileEditScreen(
            uiState = ProfileEditUiState(email = "lilly@example.com", nickname = "Lilly"),
            snackbarHostState = remember { SnackbarHostState() },
            onNicknameChanged = {},
            onCurrentPasswordChanged = {},
            onNewPasswordChanged = {},
            onConfirmNewPasswordChanged = {},
            onToggleCurrentPasswordVisibility = {},
            onToggleNewPasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
