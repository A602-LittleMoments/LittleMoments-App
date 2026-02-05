package com.a602.commonproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.a602.commonproject.designsystem.theme.LMTheme
import dagger.hilt.android.AndroidEntryPoint

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.a602.commonproject.ui.LMApp

import android.content.pm.ActivityInfo

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // 딥링크 URI를 Compose에 전달하기 위한 state
    private val deepLinkUri = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { false }
        // 화면 세로 모드 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // 딥링크 URI 추출 (onCreate에서 앱이 처음 열릴 때)
        handleDeepLink(intent)

        enableEdgeToEdge()
        setContent {
            LMTheme {
                LMApp(
                    deepLinkUri = deepLinkUri.value,
                    onDeepLinkHandled = { deepLinkUri.value = null }
                )
            }
        }
    }

    // 앱이 이미 실행 중일 때 새로운 Intent로 딥링크가 들어오는 경우 처리
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            deepLinkUri.value = intent.data
        }
    }
}


