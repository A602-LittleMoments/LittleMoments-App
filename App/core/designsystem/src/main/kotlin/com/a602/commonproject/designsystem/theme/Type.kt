package com.a602.commonproject.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.R // 패키지명에 맞는 R 클래스 확인

// 1. 폰트 폴더에 넣은 파일들(suite 글꼴)을 하나의 패밀리로 묶기
val SuiteFontFamily = FontFamily(
    Font(R.font.suite_regular, FontWeight.Normal), // 400
    Font(R.font.suite_medium, FontWeight.Medium),   // 500
    Font(R.font.suite_bold, FontWeight.Bold)       // 700
)


// 통합 타이포그래피 세트
val AppTypography = Typography(
    // 헤드라인 글씨(최상단 글씨) -> 해당 페이지 위치
    headlineLarge = TextStyle(
        fontSize = 20.sp,
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = color3,
    ),

    // 조그마한 헤드라인 글씨
    headlineMedium = TextStyle(
        fontSize = 15.sp,
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
    ),

    headlineSmall = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
    ),

    // 첫 로딩 페이지 글씨, ai 하이라이트 생성 로딩 화면
    bodyLarge = TextStyle(
        fontSize = 24.sp,
        lineHeight = 35.sp,
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        letterSpacing = 2.sp,
        color = color3,
    ),

    // 가장 많이 쓰는 텍스트 스타일(크기만 다르게 적용하면 됨)
    bodyMedium = TextStyle(
        fontSize = 18.sp,
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = color3,
    ),

    // [메인 버튼용] "로그인", "변경하기", "구글로 로그인", "카카오톡으로 로그인" 등
    labelLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = lightbackground,
        letterSpacing = 2.4.sp,
    ),

    // [입력창 글씨] "이름", "이메일", "현재 비밀번호"
    // "아직 아이랑 나랑 회원이 아니신가요? 회원가입"
    labelMedium = TextStyle(
        fontSize = 14.sp,
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Medium,
        color = color3,
    ),

    // 자동 로그인, 비밀번호 찾기 문구(색깔만 바꾸면 됨)
    // [에러 문구] "비밀번호가 일치하지 않습니다.", "영문/숫자 조합..." (빨간색 + lineHeight = 17.06.sp 지우기)
    labelSmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 17.06.sp,
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Normal,
    )

)
