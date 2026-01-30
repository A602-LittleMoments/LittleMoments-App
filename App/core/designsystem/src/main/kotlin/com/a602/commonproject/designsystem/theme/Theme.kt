package com.a602.commonproject.designsystem.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

//private val LightColorScheme = lightColorScheme(
//    // --- Primary (주요 강조색) ---
//    primary = ,                // 앱의 대표 색상
//    onPrimary = ,              // primary 위에 올라가는 콘텐츠 색
//    primaryContainer = ,       // primary의 연한 배경색
//    onPrimaryContainer = ,     // primaryContainer 위에 올라가는 콘텐츠 색
//
//    // --- Secondary (보조 강조색) ---
//    secondary = ,              // 보조 강조 색상
//    onSecondary = ,            // secondary 위에 올라가는 콘텐츠 색
//    secondaryContainer = ,     // secondary의 연한 배경색
//    onSecondaryContainer = ,   // secondaryContainer 위에 올라가는 콘텐츠 색
//
//    // --- Tertiary (제3의 강조색) ---
//    tertiary = ,               // 세 번째 강조 색상 (보통 보조 텍스트나 반전 포인트로 활용)
//    onTertiary = ,             // tertiary 위에 올라가는 콘텐츠 색
//    tertiaryContainer = ,      // tertiary의 연한 배경색
//    onTertiaryContainer = ,    // tertiaryContainer 위에 올라가는 콘텐츠 색
//
//    // --- Error (오류색) ---
//    error = ,                  // 경고 및 오류 색상
//    onError = ,                // error 위에 올라가는 콘텐츠 색
//    errorContainer = ,         // 오류 메시지 박스 등의 배경색
//    onErrorContainer = ,       // errorContainer 위에 올라가는 콘텐츠 색
//
//    // --- Background & Surface (바탕 및 표면) ---
//    background = ,             // 화면 전체의 가장 밑바닥 배경색
//    onBackground = ,           // background 위에 올라가는 기본 글자색
//    surface = ,                // 카드, 메뉴, 다이얼로그 등 컴포넌트의 표면색
//    onSurface = ,              // surface 위에 올라가는 기본 글자색
//    onSurfaceVariant = ,       // surface 위에서 보조 텍스트나 아이콘에 쓰는 연한 색
//
//    // --- Outline (경계선) ---
//    outline = ,                // 버튼 테두리나 구분선 등의 외곽선 색
//    outlineVariant = ,         // 더 연한 경계선이 필요할 때 사용하는 색
//
//    // --- Inverse (반전) ---
//    inverseSurface = ,         // 배경과 반전된 색상 (스낵바 등에 활용)
//    inverseOnSurface = ,       // inverseSurface 위에 올라가는 글자색
//    inversePrimary = ,         // 반전된 상태에서의 주요 강조색
//
//    // --- 기타 ---
//    scrim = ,                  // 다이얼로그 뒤쪽을 어둡게 가리는 반투명 색
//    surfaceTint = ,            // 표면에 색조를 입힐 때 사용하는 강조색
//    surfaceVariant =           // surface의 변형된 배경색
//)

// 💡 Color.kt와 Type.kt에 있는 변수들을 그대로 가져와서 조립만 합니다.
/*
private val LightColorScheme = lightColorScheme(
    primary = main,                // Color.kt의 main 참조
    onPrimary = lightbackground,
    primaryContainer = lightblue,  // Color.kt의 lightblue 참조
    onPrimaryContainer = background,   // Color.kt의 color6 참조
    secondary = color3,       // Color.kt의 background 참조
    onSecondary = color4,
    tertiary = color5,
    onSurface = color5,
    error = errorRed,
    outline = gray1,
)
*/


private val LightColorScheme = lightColorScheme(
    primary = main,                   // 주요 버튼 및 핵심 브랜드 색상 (파란색)
    onPrimary = lightbackground,      // 파란 버튼 위의 텍스트/아이콘 색상
    primaryContainer = lightblue,     // 연한 파란색 배경 (선택된 항목 등)
    onPrimaryContainer = color6,      // 연한 파란색 컨테이너 위의 짙은 파란색 텍스트

    secondary = color1,               // 포인트 색상 (노란색 별이나 강조 요소)
    onSecondary = color5,             // 노란색 배경 위의 짙은 텍스트
    secondaryContainer = color2,      // 보조 노란색 (연한 노랑)

    tertiary = color3,                // 강조 텍스트나 보조 UI (갈색 계열)
    onTertiary = lightbackground,

    background = background,          // 앱의 전체 기본 배경 (아이보리)
    onBackground = color5,            // 기본 배경 위의 메인 텍스트 (짙은 네이비)

    surface = lightbackground,        // 카드, 다이얼로그 등 들어올려진 UI 요소
    onSurface = color5,               // 표면 위의 텍스트

    error = errorRed,                 // 에러 상태
    onError = lightbackground,

    outline = gray1,                  // 경계선이나 구분선
    outlineVariant = gray2            // 좀 더 짙은 구분선
)

@Composable
fun LMTheme(
    content: @Composable () -> Unit
) {
    // 1. 색상 테마를 항상 Light로 고정
    val colorScheme = LightColorScheme

    // 2. 상태바(Status Bar) 색상 및 아이콘 설정
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // ✅ 수정됨: android.graphics.Color 대신 Compose Color 사용
            // (배경색을 테마의 배경색과 일치시키려면 colorScheme.background.toArgb() 사용)
            window.isNavigationBarContrastEnforced = true

            // 상태바 아이콘(시간, 배터리)을 검은색으로 강제 (Light Mode 스타일)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
