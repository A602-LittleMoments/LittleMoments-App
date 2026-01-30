package com.a602.commonproject.feature.login.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.login.login.LoginRoute
import com.a602.commonproject.feature.login.signup.SignUpRoute
import com.a602.commonproject.feature.login.splash.SplashRoute
import com.a602.commonproject.navigation.Navigator

// =================================================================
// 🗺️ Entry Provider
// =================================================================
fun EntryProviderScope<NavKey>.loginEntries(
    navigator: Navigator,
    onLoginSuccess: () -> Unit // 로그인/회원가입 완료 후 홈으로 이동하는 콜백
) {
    // 1. Splash Screen
    entry<SplashNavKey> {
        SplashRoute(
            onNavigateToLogin = { navigator.navigate(LoginNavKey) },
            onNavigateToHome = onLoginSuccess
        )
    }

    // 2. Login Screen
    entry<LoginNavKey> {
        LoginRoute(
            onNavigateToSignUp = { navigator.navigate(SignUpNavKey) },
            onLoginSuccess = onLoginSuccess
        )
    }

    // 3. SignUp Screen
    entry<SignUpNavKey> {
        SignUpRoute(
            onSignUpSuccess = onLoginSuccess,
            onBackClick = { navigator.goBack() }
        )
    }
}
