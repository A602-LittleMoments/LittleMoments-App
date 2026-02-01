package com.a602.commonproject.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.R // 패키지명에 맞는 R 클래스 확인

// 다른 임시 폰트
//val NewFontFamily = FontFamily(
//    Font(R.font.lineseed_thin, FontWeight.Normal),
//    Font(R.font.lineseed_regular, FontWeight.Medium),
//    Font(R.font.lineseed_bold, FontWeight.Bold)
//)

val SuiteFontFamily = FontFamily(
    Font(R.font.suite_regular, FontWeight.Normal), // 400
    Font(R.font.suite_medium, FontWeight.Medium),   // 500
    Font(R.font.suite_bold, FontWeight.Bold)       // 700
)
/* 폰트 사용 규칙
* 1. 기본 색상은 color3, labelLarge만 lightbackground, 상황에 따라 색 변경이 필요하다면 색상 지정 필요
* 2. 폰트 크기 display > headline > title > body > label 순으로 지정
* 3. 각 폰트에서 Large = Bold, Medium = medium, Normal = regular로 굵기 지정
* 4. 텍스트 정렬은 본인이 사용할 때 지정
*/

// 통합 타이포그래피 세트
val AppTypography = Typography(
    // 화면 로딩 중인데 크게 보여줘야할 때 사용
    displayLarge = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.25).sp,
        color = color3
    ),
    // TopAppbar 사용
    // Dday bar, Popup, Tab, GalleryScreen 사용중
    headlineLarge = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
        color = color3,
    ),
    // Polaroid 사용중
    headlineMedium = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
        color = color3,
    ),
    headlineSmall = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
        color = color3,
    ),
    // MemoryMakeScreen, MemoryEmptyScreen, Mypage, HighLightLoadScreen, 등 사용중
    titleLarge = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = color3
    ),
    // 캘린더, 회원가입 사용중
    // [입력창 실제 글씨 스타일]
    titleMedium = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        color = color3,
    ),

    titleSmall = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        color = color3,
    ),
    // 첫 로딩 페이지 글씨, ai 하이라이트 생성 로딩 화면
    bodyLarge = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
        color = color3,
    ),

    // 가장 많이 쓰는 텍스트 스타일(크기만 다르게 적용하면 됨)
    bodyMedium = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
        color = color3,
    ),
    bodySmall = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
        color = color3,
    ),

    // [메인 버튼용] "로그인", "변경하기", "구글로 로그인", "카카오톡으로 로그인" 등
    labelLarge = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
        color = lightbackground,
    ),

    // [입력창 글씨] "이름", "이메일", "현재 비밀번호"
    // "아직 아이랑 나랑 회원이 아니신가요? 회원가입"
    labelMedium = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
        color = color3,
    ),

    // 자동 로그인, 비밀번호 찾기 문구(색깔만 바꾸면 됨)
    // [에러 문구] "비밀번호가 일치하지 않습니다.", "영문/숫자 조합..." (빨간색 + lineHeight = 17.06.sp 지우기)
    labelSmall = TextStyle(
        fontFamily = SuiteFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.sp,
        color = color3,
    ),
)


//// 1. 폰트 폴더에 넣은 파일들(suite 글꼴)을 하나의 패밀리로 묶기
//val SuiteFontFamily = FontFamily(
//    Font(R.font.suite_regular, FontWeight.Normal), // 400
//    Font(R.font.suite_medium, FontWeight.Medium),   // 500
//    Font(R.font.suite_bold, FontWeight.Bold)       // 700
//)
//
//
//// 통합 타이포그래피 세트
//val AppTypography = Typography(
//    // 헤드라인 글씨(최상단 글씨) -> 해당 페이지 위치
//    headlineLarge = TextStyle(
//        fontSize = 20.sp,
//        fontFamily = SuiteFontFamily,
//        fontWeight = FontWeight.Bold,
//        textAlign = TextAlign.Center,
//        color = color3,
//    ),
//
//    // 조그마한 헤드라인 글씨
//    headlineMedium = TextStyle(
//        fontSize = 15.sp,
//        fontFamily = SuiteFontFamily,
//        fontWeight = FontWeight.Normal,
//        textAlign = TextAlign.Center,
//    ),
//
//    headlineSmall = TextStyle(
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        fontFamily = SuiteFontFamily,
//        fontWeight = FontWeight.Bold,
//    ),
//
//    // 첫 로딩 페이지 글씨, ai 하이라이트 생성 로딩 화면
//    bodyLarge = TextStyle(
//        fontSize = 24.sp,
//        lineHeight = 35.sp,
//        fontFamily = SuiteFontFamily,
//        fontWeight = FontWeight.Bold,
//        textAlign = TextAlign.Center,
//        letterSpacing = 2.sp,
//        color = color3,
//    ),
//
//    // 가장 많이 쓰는 텍스트 스타일(크기만 다르게 적용하면 됨)
//    bodyMedium = TextStyle(
//        fontSize = 18.sp,
//        fontFamily = SuiteFontFamily,
//        fontWeight = FontWeight.Bold,
//        textAlign = TextAlign.Center,
//        color = color3,
//    ),
//
//    // [메인 버튼용] "로그인", "변경하기", "구글로 로그인", "카카오톡으로 로그인" 등
//    labelLarge = TextStyle(
//        fontSize = 16.sp,
//        lineHeight = 20.sp,
//        fontFamily = SuiteFontFamily,
//        fontWeight = FontWeight.Bold,
//        textAlign = TextAlign.Center,
//        color = lightbackground,
//        letterSpacing = 2.4.sp,
//    ),
//
//    // [입력창 글씨] "이름", "이메일", "현재 비밀번호"
//    // "아직 아이랑 나랑 회원이 아니신가요? 회원가입"
//    labelMedium = TextStyle(
//        fontSize = 14.sp,
//        fontFamily = SuiteFontFamily,
//        fontWeight = FontWeight.Medium,
//        color = color3,
//    ),
//
//    // 자동 로그인, 비밀번호 찾기 문구(색깔만 바꾸면 됨)
//    // [에러 문구] "비밀번호가 일치하지 않습니다.", "영문/숫자 조합..." (빨간색 + lineHeight = 17.06.sp 지우기)
//    labelSmall = TextStyle(
//        fontSize = 12.sp,
//        lineHeight = 17.06.sp,
//        fontFamily = SuiteFontFamily,
//        fontWeight = FontWeight.Normal,
//    ),
//
//    // [입력창 실제 글씨 스타일]
//    titleMedium = TextStyle(
//        fontSize = 16.sp,
//        fontFamily = SuiteFontFamily,
//        fontWeight = FontWeight.Medium,
//        color = color3,
//    ),
//
//    titleSmall = TextStyle(
//        fontSize = 14.sp,
//        fontFamily = SuiteFontFamily,
//        fontWeight = FontWeight.Medium,
//        color = color3,
//    )
//)
