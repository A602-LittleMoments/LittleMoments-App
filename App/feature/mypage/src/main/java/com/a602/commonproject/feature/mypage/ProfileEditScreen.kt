package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.LMEditInputField
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.errorRed
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

    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            navigator.goBack()
            viewModel.onSaveSuccessConsumed()
        }
    }

    // 비밀번호 변경 성공 시 (재로그인 필요)
    LaunchedEffect(uiState.isPasswordChanged) {
        if (uiState.isPasswordChanged) {
            // TODO: 스낵바 메시지 표시 (현재는 UI에 스낵바가 없음)
            viewModel.onPasswordChangedConsumed()
            navigator.replaceRoot(LoginNavKey)
        }
    }

    // 로그아웃 성공 시 로그인 화면으로 이동
    LaunchedEffect(uiState.isLogoutSuccess) {
        if (uiState.isLogoutSuccess) {
            viewModel.onLogoutSuccessConsumed()
            navigator.replaceRoot(LoginNavKey)
        }
    }

    // 회원 탈퇴 성공 시 로그인 화면으로 이동
    LaunchedEffect(uiState.isDeleteSuccess) {
        if (uiState.isDeleteSuccess) {
            viewModel.onDeleteSuccessConsumed()
            navigator.replaceRoot(LoginNavKey)
        }
    }

    if (uiState.showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onLogoutDismissed,
            title = { Text("로그아웃") },
            text = { Text("정말로 로그아웃 하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = viewModel::logout) {
                    Text("로그아웃")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onLogoutDismissed) {
                    Text("취소")
                }
            }
        )
    }

    // 회원 탈퇴 확인 팝업
    if (uiState.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteAccountDismissed,
            title = { Text("회원 탈퇴") },
            text = { Text("정말로 계정을 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = viewModel::deleteAccount) {
                    Text("탈퇴", color = errorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDeleteAccountDismissed) {
                    Text("취소")
                }
            }
        )
    }

    ProfileEditScreen(
        uiState = uiState,
        onNicknameChanged = viewModel::onNicknameChanged,
        onCurrentPasswordChanged = viewModel::onCurrentPasswordChanged,
        onNewPasswordChanged = viewModel::onNewPasswordChanged,
        onConfirmNewPasswordChanged = viewModel::onConfirmNewPasswordChanged,
        onToggleCurrentPasswordVisibility = viewModel::onToggleCurrentPasswordVisibility,
        onToggleNewPasswordVisibility = viewModel::onToggleNewPasswordVisibility,
        onToggleConfirmPasswordVisibility = viewModel::onToggleConfirmPasswordVisibility,
        onSaveClick = viewModel::saveProfile,
        onLogoutClick = viewModel::onLogoutClicked,
        onDeleteAccountClick = viewModel::onDeleteAccountClicked,
        onBackClick = { navigator.goBack() }
    )
}

@Composable
fun ProfileEditScreen(
    uiState: ProfileEditUiState,
    onNicknameChanged: (String) -> Unit,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmNewPasswordChanged: (String) -> Unit,
    onToggleCurrentPasswordVisibility: () -> Unit,
    onToggleNewPasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onSaveClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = background,
        topBar = { LMTopAppBar(title = "내 정보 수정", onNavigationClick = onBackClick) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                FilledButton(
                    text = "저장하기",
                    onClick = onSaveClick,
                    size = ButtonSize.Full,
                    enabled = !uiState.isLoading
                )
            }
        }
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
                    trailingIcon = { Icon(imageVector = LMicons.Person, contentDescription = "닉네임", tint = color4) },
                    supportingText = uiState.nicknameError,
                    isError = uiState.nicknameError != null
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

                LMEditInputField(
                    label = "현재 비밀번호",
                    value = uiState.currentPassword,
                    onValueChange = onCurrentPasswordChanged,
                    isPassword = !uiState.isCurrentPasswordVisible,
                    supportingText = uiState.currentPasswordError,
                    isError = uiState.currentPasswordError != null,
                    trailingIcon = {
                        IconButton(onClick = onToggleCurrentPasswordVisibility) {
                            Icon(imageVector = if (uiState.isCurrentPasswordVisible) LMicons.VisibilityOff else LMicons.Visibility, contentDescription = "비밀번호 보기/숨기기", tint = color4)
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
                            Icon(imageVector = if (uiState.isNewPasswordVisible) LMicons.VisibilityOff else LMicons.Visibility, contentDescription = "비밀번호 보기/숨기기", tint = color4)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                LMEditInputField(
                    label = "새 비밀번호 확인",
                    value = uiState.confirmNewPassword,
                    onValueChange = onConfirmNewPasswordChanged,
                    isPassword = !uiState.isConfirmPasswordVisible,
                    supportingText = uiState.newPasswordError,
                    isError = uiState.newPasswordError != null,
                    trailingIcon = {
                        IconButton(onClick = onToggleConfirmPasswordVisibility) {
                            Icon(imageVector = if (uiState.isConfirmPasswordVisible) LMicons.VisibilityOff else LMicons.Visibility, contentDescription = "비밀번호 보기/숨기기", tint = color4)
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // 로그아웃 버튼
                TextButton(onClick = onLogoutClick) {
                    Text(text = "로그아웃", color = color4)
                }
                TextButton(onClick = onDeleteAccountClick) {
                    Text(text = "회원 탈퇴", color = errorRed)
                }
                Spacer(modifier = Modifier.height(16.dp))
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
            onNicknameChanged = {},
            onCurrentPasswordChanged = {},
            onNewPasswordChanged = {},
            onConfirmNewPasswordChanged = {},
            onToggleCurrentPasswordVisibility = {},
            onToggleNewPasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onSaveClick = {},
            onLogoutClick = {},
            onDeleteAccountClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "오류 상태")
@Composable
fun ProfileEditScreenErrorPreview() {
    LMTheme {
        ProfileEditScreen(
            uiState = ProfileEditUiState(
                email = "lilly@example.com",
                nickname = "Lilly",
                currentPasswordError = "현재 비밀번호를 확인해주세요.",
                newPasswordError = "새 비밀번호가 일치하지 않습니다."
            ),
            onNicknameChanged = {},
            onCurrentPasswordChanged = {},
            onNewPasswordChanged = {},
            onConfirmNewPasswordChanged = {},
            onToggleCurrentPasswordVisibility = {},
            onToggleNewPasswordVisibility = {},
            onToggleConfirmPasswordVisibility = {},
            onSaveClick = {},
            onLogoutClick = {},
            onDeleteAccountClick = {},
            onBackClick = {}
        )
    }
}
