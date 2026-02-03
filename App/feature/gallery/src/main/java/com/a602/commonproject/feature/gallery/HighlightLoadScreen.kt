
package com.a602.commonproject.feature.gallery

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.gallery.viewmodel.HighlightLoadingViewModel

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
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mission_control")

    // 1. Star Warp Effect (Simulated motion)
    val starProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stars"
    )

    // 2. Rocket Rumble
    val rumbleX by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(40, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rumbleX"
    )
    val rumbleY by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(45, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rumbleY"
    )

    // 3. Floating Lift
    val liftDelta by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lift"
    )

    // 4. Glow Intensity
    val coreGlowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF000814))) {
        // Starry Background
        Image(
            painter = painterResource(id = R.drawable.gallery_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = 0.6f
        )

        // Simulated Star Warp Particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val count = 35 // Increased for richer look
            for (i in 0 until count) {
                val x = (i * 137.5f % 1.0f) * size.width
                val startY = (i * 53.1f % 1.0f) * size.height
                val speed = (i % 3 + 1) * 0.4f
                val y = (startY + starProgress * size.height * speed) % size.height
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f * (y / size.height)),
                    radius = (i % 2 + 1).dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-20).dp), // Subtler offset for better centering
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Multi-layered engine glow - Adjusted to match 300dp rocket better
                Box(
                    modifier = Modifier
                        .size(380.dp)
                        .scale(coreGlowScale)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    main.copy(alpha = 0.5f),
                                    Color(0xFF6200EE).copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Secondary core glow
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .scale(coreGlowScale * 1.1f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                      // --- Rocket with Bloom Effect ---
                Box(contentAlignment = Alignment.Center) {
                    // 1. Outer Blur Layer (The "Heat/Glow" Effect)
                    Image(
                        painter = painterResource(id = R.drawable.rocket3),
                        contentDescription = null,
                        modifier = Modifier
                            .size(310.dp) // Slightly larger
                            .offset(x = rumbleX.dp, y = (rumbleY + liftDelta).dp)
                            .blur(radius = 16.dp)
                            .graphicsLayer(alpha = 0.5f),
                        contentScale = ContentScale.Fit
                    )

                    // 2. The Main Rocket (Sharp with very slight soft edges)
                    Image(
                        painter = painterResource(id = R.drawable.rocket3),
                        contentDescription = "Rocket",
                        modifier = Modifier
                            .size(300.dp)
                            .offset(x = rumbleX.dp, y = (rumbleY + liftDelta).dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .blur(radius = 0.5.dp), // Extremely subtle soft focus
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(Modifier.height(32.dp)) // Tighter spacing for better ratio

            Text(
                text = "추억 조각들을 연결하는 중",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp,
                ),
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "곧 우리 가족만의\n특별한 하이라이트가\n우주에서 도착합니다!",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 22.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))
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
        shape = androidx.compose.foundation.shape.CircleShape,
        color = com.a602.commonproject.designsystem.theme.main
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
