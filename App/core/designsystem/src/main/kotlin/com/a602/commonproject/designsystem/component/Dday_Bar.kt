package com.a602.commonproject.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.theme.color1
import com.a602.commonproject.designsystem.theme.gray2


@Composable
fun DdayProgressBar(
    progress: Float,                 // 0.0f ~ 1.0f
    modifier: Modifier = Modifier,
    height: Dp = 18.dp,
    backgroundColor: Color = gray2,
    fillColor: Color = color1,
) {
    val clamped = progress.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = clamped,
        label = "dday-progress"
    )

    val shape = RoundedCornerShape(percent = 50)

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(fillColor)
        )
    }
}

// 추후 디데이 계산해서 쓸 때
// 전체 기간 : totalDays
// 남은 기간 : dDay
//val progress = ((totalDays - dDay).toFloat() / totalDays).coerceIn(0f, 1f)
//DdayProgressBar(progress = progress)
