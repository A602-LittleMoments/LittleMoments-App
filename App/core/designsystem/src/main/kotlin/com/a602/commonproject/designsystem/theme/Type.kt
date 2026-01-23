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


// 1. 첫 로딩 페이지
val LoadingTextStyle = TextStyle(
    fontSize = 20.sp,
    lineHeight = 35.sp,
    fontFamily = SuiteFontFamily,
    fontWeight = FontWeight.Bold,
    color = color3,
    textAlign = TextAlign.Center,
    letterSpacing = 2.sp
)

// 2. 통합 타이포그래피 세트
val AppTypography = Typography(
    // 가장 크고 굵은 스타일 (로딩/강조)
    displayMedium = LoadingTextStyle,

    // [입력창 제목] "이메일", "현재 비밀번호 *"
    titleSmall = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = color3  // 짙은 갈색
    ),

    // [버튼용] "로그인", "변경하기"
    labelLarge = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        textAlign = TextAlign.Center
    ),

    // [에러 문구] "비밀번호가 일치하지 않습니다."
    labelSmall = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = errorRed // 에러 빨간색
    ),

    // [안내 문구] "영문/숫자 조합..." (조금 더 연한 색)
    bodySmall = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = color4 // 연한 갈색
    ),

    //

)
