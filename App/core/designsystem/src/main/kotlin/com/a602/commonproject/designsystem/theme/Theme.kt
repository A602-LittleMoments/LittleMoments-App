package com.a602.commonproject.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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

@Composable
fun NiaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography, // 💡 Type.kt의 AppTypography 참조
        content = content
    )
}
