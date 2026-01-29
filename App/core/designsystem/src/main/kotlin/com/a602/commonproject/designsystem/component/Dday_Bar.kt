package com.a602.commonproject.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.theme.NiaTheme
import com.a602.commonproject.designsystem.theme.color1
import com.a602.commonproject.designsystem.theme.gray2
import kotlin.math.pow

/**
 * D+텍스트 + 100일 단위 반복 ProgressBar
 *
 * - dayCount=100  -> 100% (가득)
 * - dayCount=101  -> 1% (다시 처음부터)
 * 아래처럼 사용
 * DdayBarWithLabel(dayCount = 60)
 */
@Composable
fun DdayBarWithLabel(
    dayCount: Int, // 예: 60 -> "D+60"
    modifier: Modifier = Modifier,
    barHeight: Dp = 22.dp,
    barWidthFraction: Float = 0.72f,   // 화면폭 기준
    backgroundColor: Color = gray2,
    fillColor: Color = color1,
    spacing: Dp = 14.dp,
) {
    val progress = dayCountToProgress100Cycle(dayCount)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // D+ 텍스트
        Text(
            text = "D+$dayCount",
            style = MaterialTheme.typography.headlineLarge, // 너희 Type.kt 기준 큰 글씨
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(spacing))

        // 진행바
        DdayProgressBar(
            progress = progress,
            modifier = Modifier.fillMaxWidth(barWidthFraction),
            height = barHeight,
            backgroundColor = backgroundColor,
            fillColor = fillColor,
            minFillDp = 14.dp,          // (아래 설명) 최소 표시 폭
            edgeExponent = 0.75f       // (아래 설명) 초반을 더 “티나게”
        )
    }
}

/** ProgressBar */
@Composable
fun DdayProgressBar(
    progress: Float, // 0.0f ~ 1.0f
    modifier: Modifier = Modifier,
    height: Dp = 18.dp,
    backgroundColor: Color = gray2,
    fillColor: Color = color1,
    // 0이 아니면 최소 이만큼은 채워서 "티"나게
    minFillDp: Dp = 14.dp,
    // 양 끝 강조(0% 근처는 더 커 보이고, 100% 근처는 99가 덜 차 보여서 100이 더 튀어보임)
    // 0.65~0.85 추천. (0.75가 무난)
    edgeExponent: Float = 0.75f,
) {
    val clamped = progress.coerceIn(0f, 1f)

    // 양 끝 강조 보정 함수
    fun edgeBoost(p: Float, e: Float): Float {
        if (p <= 0f) return 0f
        if (p >= 1f) return 1f
        val t = p.coerceIn(0f, 1f)
        return if (t < 0.5f) {
            0.5f * (2f * t).pow(e)                // 초반을 더 크게
        } else {
            1f - 0.5f * (2f * (1f - t)).pow(e)    // 끝부분은 99를 덜 차게 -> 100이 더 튀게
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(percent = 50))
            .background(backgroundColor)
    ) {
        val widthDp = maxWidth
        val minFraction =
            if (minFillDp > 0.dp && widthDp > 0.dp) (minFillDp / widthDp).coerceIn(0f, 1f)
            else 0f

        val boosted = edgeBoost(clamped, edgeExponent)

        // progress가 0이 아니면 최소 폭 보장
        val visualProgress =
            if (clamped == 0f) 0f else maxOf(boosted, minFraction).coerceIn(0f, 1f)

        val animatedProgress by animateFloatAsState(
            targetValue = visualProgress,
            animationSpec = tween(durationMillis = 450),
            label = "dday-progress"
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(fillColor)
        )
    }
}


/**
 * dayCount -> 0..1 변환 (100일 단위 반복)
 * - 100일은 100%로 보여주고, 101일부터 다시 0%로 시작
 */
fun dayCountToProgress100Cycle(dayCount: Int): Float {
    if (dayCount <= 0) return 0f
    val mod = dayCount % 100
    return if (mod == 0) 1f else (mod / 100f)
}

/* -------------------- Preview -------------------- */

@Preview(showBackground = true, widthDp = 411, heightDp = 520)
@Composable
fun DdayBarWithLabelPreview_Showcase() {
    Surface(color = Color(0xFFF8F2E3)) { // 캡쳐 배경 톤 비슷하게
        NiaTheme {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DdayBarWithLabel(dayCount = 60)
                DdayBarWithLabel(dayCount = 99)
                DdayBarWithLabel(dayCount = 100)
                DdayBarWithLabel(dayCount = 101)
            }
        }
    }
}

