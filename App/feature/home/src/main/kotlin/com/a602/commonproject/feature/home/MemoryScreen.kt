package com.a602.commonproject.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.model.data.Collection
import com.a602.commonproject.designsystem.R
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun MemoryScreen(
    items: List<Collection>,
    onPlanetClick: (String, String, Int) -> Unit, // id, label, planetResId
    onCameraClick: () -> Unit,
    onMakeSlideshowClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onHelpClick: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val maxWidth = maxWidth
        val maxHeight = maxHeight

        // 1. 배경
        Image(
            painter = painterResource(id = R.drawable.memory_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. 행성 스크롤 영역
        // 하단 캐릭터/버튼 영역(흰색 배경)과 겹치지 않게 위쪽(남색 배경)에만 배치
        val bottomReservedSpace = 340.dp // [Fix] Increased to avoid overlap with higher buttons

        // 5. Layout Calculation on Background Thread
        // [Fix] Use rememberSaveable to keep the layout consistent across navigation/recomposition
        val layoutSeed = androidx.compose.runtime.saveable.rememberSaveable { kotlin.random.Random.nextLong() }
        val layout by produceState<PlanetLayoutResult?>(initialValue = null, items, maxWidth, maxHeight, layoutSeed) {
            value = withContext(Dispatchers.Default) {
                buildPlanetsUiLaneLayout(
                    items = items,
                    viewportWidth = maxWidth,
                    viewportHeight = maxHeight - bottomReservedSpace,
                    bottomSafeArea = 60.dp,
                    topSafeArea = 100.dp,
                    seed = layoutSeed

                )
            }
        }

        if (layout != null) {
             PlanetsScrollContent(
                planets = layout!!.planets,
                canvasHeight = layout!!.canvasHeight,
                onPlanetClick = onPlanetClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomReservedSpace)
            )
        }

        // 3. 상단 아이콘 (알림, 도움말)
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()

                .padding(16.dp), // [Fix] Changed from vertical=8.dp to 16.dp to match BabyScreen Edit icon height
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 도움말 아이콘
            // [Fix] Applied Glass Frame Style
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                    .clickable(onClick = onHelpClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = "Help",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 알림 아이콘
            // [Fix] Applied Glass Frame Style
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.1f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                    .clickable(onClick = onNotificationClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 4. 하단 캐릭터 (Boy & Girl)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 170.dp), // [Fix] Raised further as requested (160dp -> 200dp) -> Modified to 150dp to account for text labels
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Side padding reduced to allow centering
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally), // Centered with spacing
                verticalAlignment = Alignment.Bottom
            ) {
                // 왼쪽: 소년 (카메라)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onCameraClick() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.boy_camera),
                        contentDescription = "Camera",
                        modifier = Modifier.size(160.dp), // 136dp -> 160dp
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "사진 찍기",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }

                // 오른쪽: 소녀 (슬라이드쇼)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onMakeSlideshowClick() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.girl_slideshow),
                        contentDescription = "Slideshow",
                        modifier = Modifier.size(160.dp), // 136dp -> 160dp
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "하이라이트 생성",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanetsScrollContent(
    planets: List<KeywordPlanetUi>,
    canvasHeight: Dp,
    onPlanetClick: (String, String, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(canvasHeight)
        ) {
            // 배경은 메인에서 처리하므로 여기선 제거하거나 투명 처리
            // 아이템 배치
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val maxW = maxWidth
                planets.forEach { p ->
                    MovingPlanetItem(
                        p = p,
                        maxW = maxW,
                        onPlanetClick = onPlanetClick
                    )
                }
            }
        }
    }
}

@Composable
private fun MovingPlanetItem(
    p: KeywordPlanetUi,
    maxW: Dp,
    onPlanetClick: (String, String, Int) -> Unit
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    // 1. Entrance animation state
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(p.keywordId) {
        // Random delayed appearance for "pretty" entrance
        val delayTime = (p.keywordId.hashCode() % 600).absoluteValue.toLong()
        delay(delayTime)
        isVisible = true
    }

    val entranceAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "entranceAlpha"
    )
    val entranceScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "entranceScale"
    )
    val entranceOffset by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 20.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "entranceOffset"
    )

    // 2. Continuous Floating (Bobbing & Rotating) animation
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2500 + (p.keywordId.hashCode() % 1200).absoluteValue,
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )
    val floatRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3500 + (p.keywordId.hashCode() % 1500).absoluteValue,
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatRotation"
    )

    // 3. Click (Press) animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val clickScale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "clickScale"
    )

    val x = ((maxW * p.xRatio) - (p.size / 2)).coerceIn(0.dp, maxW - p.size)

    Column(
        modifier = Modifier
            .offset(x = x, y = p.y + floatOffset.dp + entranceOffset)
            .width(p.size)
            .graphicsLayer {
                alpha = entranceAlpha
                scaleX = entranceScale * clickScale
                scaleY = entranceScale * clickScale
                rotationZ = floatRotation
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom click animation handled via scale
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onPlanetClick(p.keywordId, p.label, p.planetResId)
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(p.size)) {
            Image(
                painter = painterResource(id = p.planetResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(6.dp))

        // 2줄 허용 및 글자 수에 따른 크기 조절
        val isLongText = p.label.length > 4

        Text(
            text = p.label,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = if (isLongText) 18.sp else 24.sp,
            style = (if (isLongText) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge).copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black,
                    offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
        )
    }
}

@Preview(showBackground = true, name = "Memory - Planets", device = "spec:width=411dp,height=891dp,dpi=440")
@Composable
private fun Preview_Memory_Planets() {
    val items = listOf(
        Collection("c1", "물건", "k1", "인형", 12),
        Collection("c2", "음식", "k2", "밥", 20),
        Collection("c3", "인물", "k3", "엄마", 30),
        Collection("c4", "기념", "k4", "생일", 5),
        Collection("c5", "여행", "k5", "바다", 50),
    )
    LMTheme {
        MemoryScreen(
            items = items,
            onPlanetClick = { _,_,_ ->

            },
            onCameraClick = {},
            onMakeSlideshowClick = {},
            onNotificationClick = {},
            onHelpClick = {}
        )
    }
}
