package com.a602.commonproject.feature.login.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.R
// TODO: 로고 리소스 필요 (없으면 임시 텍스트나 아이콘 대체)

@Composable
fun SplashRoute(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        when (uiState) {
            SplashUiState.Loading -> Unit // 대기
            SplashUiState.NavigateToHome -> onNavigateToHome()
            SplashUiState.NavigateToLogin -> onNavigateToLogin()
        }
    }

    SplashScreen()
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9E6)), // 아이보리 배경 (참고 이미지 색상)
        contentAlignment = Alignment.Center
    ) {
         Image(
             painter = painterResource(id = R.drawable.littlemoments_logo),
             contentDescription = "Logo",
             modifier = Modifier.size(300.dp)
         )
    }
}
