package com.a602.commonproject.feature.login.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.LMEditInputField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 로그인 화면의 진입점(Route)입니다.
 * ViewModel을 주입받아 UI 상태([LoginUiState])를 관찰하고, 네비게이션 이벤트 및 사이드 이펙트를 처리합니다.
 *
 * @param onLoginSuccess 로그인 성공 시 호출되는 콜백 (홈 화면 등으로 이동)
 * @param onNavigateToSignUp 회원가입 버튼 클릭 시 호출되는 콜백
 */
@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is LoginUiState.Success -> {
                viewModel.resetState()
                onLoginSuccess()
            }
            is LoginUiState.NeedGroupSetup -> onNavigateToSignUp()
            is LoginUiState.Error -> {
                snackBarHostState.showSnackbar(state.message)
                viewModel.clearError()
            }
            else -> {}
        }
    }

    LoginScreen(
        isLoading = uiState is LoginUiState.Loading,
        snackBarHostState = snackBarHostState,
        onLoginClick = viewModel::login,
        onSignUpClick = onNavigateToSignUp
    )
}

/**
 * 실제 로그인 UI를 구성하는 컴포저블입니다.
 *
 * @param isLoading 로딩 진행 중 여부
 * @param snackBarHostState 스낵바 상태 관리 객체
 * @param onLoginClick 로그인 버튼 클릭 시 실행할 함수 (이메일, 비밀번호 전달)
 * @param onSignUpClick 회원가입 텍스트 클릭 시 실행할 함수
 */
@Composable
fun LoginScreen(
    isLoading: Boolean,
    snackBarHostState: SnackbarHostState,
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // [Fix] Explicitly force Status Bar to Transparent + White Icons for LoginScreen
    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        androidx.compose.runtime.SideEffect {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        containerColor = Color.Transparent,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background Image
            Image(
                painter = painterResource(id = R.drawable.gallery_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            // 2. Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_shadow),
                    contentDescription = "Logo",
                    modifier = Modifier.size(280.dp) // Adjusted size
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Email Input
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "이메일",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    LMEditInputField(
                        value = email,
                        onValueChange = { email = it },
                        label = "",
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "비밀번호",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    LMEditInputField(
                        value = password,
                        onValueChange = { password = it },
                        label = "",
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Forgot Password (Auto Login removed as requested)
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { /* TODO: Forgot Password Logic */ }) {
                        Text(
                            text = "비밀번호 찾기",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Login Button
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    }
                } else {
                    FilledButton(
                        text = "로그인",
                        onClick = { onLoginClick(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        size = ButtonSize.Full, // Assuming Large/Full fits design
                        enabled = !isLoading,
                        // Custom colors if needed, but FilledButton might default to Primary (Navy)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sign Up Link
                TextButton(onClick = onSignUpClick) {
                    Text(
                        text = "아직 아이랑 나랑 회원이 아니신가요? 회원가입",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
