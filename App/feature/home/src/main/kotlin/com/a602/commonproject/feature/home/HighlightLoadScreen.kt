package com.a602.commonproject.feature.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.feature.home.viewmodel.HighlightLoadingViewModel
import com.a602.commonproject.designsystem.theme.main

@Composable
fun HighlightLoadingRoute(
    startMillis: Long,
    endMillis: Long,
    onSuccess: (String) -> Unit, // COMPLETED 상태의 slideshowId 전달
    onFailure: (Throwable) -> Unit,
    viewModel: HighlightLoadingViewModel = hiltViewModel()
) {
    LaunchedEffect(startMillis, endMillis) {
        viewModel.createAndWaitSlideshow(
            startMillis = startMillis,
            endMillis = endMillis,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    HighlightLoadingScreen()
}

@Composable
fun HighlightLoadingScreen(
    modifier: Modifier = Modifier
) {
    LoadingContent(modifier = modifier)
}

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier,
    title: String = "추억 조각들을 연결하는 중",
    subTitle: String = "곧 우리 가족만의\n특별한 하이라이트가\n우주에서 도착합니다!"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rocket_mission")

    // 1. Unified Launch Progress (0.0 to 1.0)
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    // 2. Derive state from progress
    val isLaunching = progress > 0.3f
    val launchFactor = if (isLaunching) (progress - 0.3f) / 0.7f else 0f
    
    // Angled Trajectory (Shooting towards Top-Right)
    // Applying a 45-degree visually consistent flight path
    val launchX = launchFactor * 800f
    val launchY = -(launchFactor * 1000f)

    // Alpha (Fade out later in the launch)
    val launchAlpha = when {
        progress < 0.6f -> 1f
        else -> 1f - ((progress - 0.6f) / 0.4f)
    }

    // 3. Shake intensity
    val shakeIntensity = if (isLaunching) 0.5f else 1.8f
    val shakeX by infiniteTransition.animateFloat(
        initialValue = -shakeIntensity,
        targetValue = shakeIntensity,
        animationSpec = infiniteRepeatable(tween(40), RepeatMode.Reverse),
        label = "shakeX"
    )
    val shakeY by infiniteTransition.animateFloat(
        initialValue = -shakeIntensity,
        targetValue = shakeIntensity,
        animationSpec = infiniteRepeatable(tween(45), RepeatMode.Reverse),
        label = "shakeY"
    )

    // 4. Engine Fire Pulse
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(200), RepeatMode.Reverse),
        label = "fire"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000814))
    ) {
        // --- Layer 1: Global Background ---
        Image(
            painter = painterResource(id = R.drawable.gallery_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        // --- Layer 2: Main Content ---
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                // Engine Glow (Positioned relative to the angled rocket)
                Box(
                    modifier = Modifier
                        .offset(x = (launchX - 30).dp, y = (launchY + 80).dp)
                        .size(160.dp)
                        .scale(fireScale)
                        .alpha(launchAlpha)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(main.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )

                // The Rocket (rocket4)
                Image(
                    painter = painterResource(id = R.drawable.rocket4),
                    contentDescription = "Rocket",
                    modifier = Modifier
                        .size(240.dp)
                        .offset(x = (shakeX + launchX).dp, y = (shakeY + launchY).dp)
                        .alpha(launchAlpha),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(56.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = subTitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(48.dp))
            LoadingDots()
        }
    }
}

@Composable
private fun LoadingDots() {
    val infinite = rememberInfiniteTransition(label = "dots")
    val s1 by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "s1"
    )
    val s2 by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(500, delayMillis = 150), RepeatMode.Reverse),
        label = "s2"
    )
    val s3 by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(500, delayMillis = 300), RepeatMode.Reverse),
        label = "s3"
    )

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Dot(s1)
        Dot(s2)
        Dot(s3)
    }
}

@Composable
private fun Dot(scale: Float) {
    Surface(
        modifier = Modifier
            .size(12.dp)
            .scale(scale),
        shape = CircleShape,
        color = main
    ) {}
}

@Preview(showBackground = true)
@Composable
fun LoadingContentPreview() {
    LMTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoadingContent()
        }
    }
}
