package com.a602.commonproject.feature.memory

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp


/**
 * 화면에 떠있는 "행성" 하나의 정보
 * xRatio: 0f ~ 1f (화면 가로 비율 위치)
 * y: 캔버스 내 세로 위치
 */
data class KeywordPlanetUi(
    val keywordId: String,
    @DrawableRes val planetResId: Int,
    val xRatio: Float, // 0.0f(왼쪽) ~ 1.0f(오른쪽)
    val y: Dp,
    val size: Dp,
    val label: String, // 행성 밑에 표시할 키워드
)

data class PlanetLayoutResult(
    val planets: List<KeywordPlanetUi>,
    val canvasHeight: Dp,
)
