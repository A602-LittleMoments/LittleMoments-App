package com.a602.commonproject.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.model.data.Collection
import com.a602.commonproject.designsystem.R

@Composable
fun MemoryScreen(
    items: List<Collection>,
    onPlanetClick: (String) -> Unit,
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
        val bottomReservedSpace = 260.dp
        
        val layout = remember(items, maxWidth, maxHeight) {
            buildPlanetsUiLaneLayout(
                items = items,
                viewportWidth = maxWidth,
                viewportHeight = maxHeight - bottomReservedSpace, // 뷰포트 높이 자체를 줄임
                bottomSafeArea = 60.dp, // 스크롤 끝부분 여백 (이제 캐릭터 높이만큼 줄 필요 없음)
                topSafeArea = 80.dp
            )
        }

        PlanetsScrollContent(
            planets = layout.planets,
            canvasHeight = layout.canvasHeight,
            onPlanetClick = onPlanetClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomReservedSpace) // 하단 영역 침범 금지
        )

        // 3. 상단 아이콘 (알림, 도움말)
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 도움말 아이콘
            IconButton(onClick = onHelpClick) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Help",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            // 알림 아이콘
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // 4. 하단 캐릭터 (Boy & Girl)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 80.dp), // 언덕 위에 앉아 있는 느낌 (Padding Up)
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
                Image(
                    painter = painterResource(id = R.drawable.boy_camera),
                    contentDescription = "Camera",
                    modifier = Modifier
                        .size(160.dp) // 136dp -> 160dp
                        .clickable { onCameraClick() },
                    contentScale = ContentScale.Fit
                )

                // 오른쪽: 소녀 (슬라이드쇼)
                Image(
                    painter = painterResource(id = R.drawable.girl_slideshow),
                    contentDescription = "Slideshow",
                    modifier = Modifier
                        .size(160.dp) // 136dp -> 160dp
                        .clickable { onMakeSlideshowClick() },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun PlanetsScrollContent(
    planets: List<KeywordPlanetUi>,
    canvasHeight: Dp,
    onPlanetClick: (String) -> Unit,
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
                    val x = ((maxW * p.xRatio) - (p.size / 2)).coerceIn(0.dp, maxW - p.size)

                    Column (
                        modifier = Modifier
                            .offset(x = x, y = p.y)
                            .width(p.size)
                            .clickable { onPlanetClick(p.keywordId) },
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

                        Text(
                            text = p.label,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyLarge.copy(
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
            }
        }
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
            onPlanetClick = {},
            onCameraClick = {},
            onMakeSlideshowClick = {},
            onNotificationClick = {},
            onHelpClick = {}
        )
    }
}
