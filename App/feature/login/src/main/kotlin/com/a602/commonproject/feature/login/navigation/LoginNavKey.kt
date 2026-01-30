package com.a602.commonproject.feature.login.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// =================================================================
// 🔑 Navigation Keys
// =================================================================
@Serializable
object SplashNavKey : NavKey // Splash

@Serializable
object LoginNavKey : NavKey // 로그인 화면

@Serializable
object SignUpNavKey : NavKey // 회원가입 화면
