package com.a602.commonproject.feature.memory

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MemoryScreen(
    items: List<TempKeywordDto>,
    onPlanetClick: (String) -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val layout = remember(items, maxWidth, maxHeight) {
            buildPlanetsUiLaneLayout(
                items = items,
                viewportWidth = maxWidth,
                viewportHeight = maxHeight,
                bottomSafeArea = 96.dp,
            )
        }

        PlanetsScrollContent(
            planets = layout.planets,
            canvasHeight = layout.canvasHeight,
            onPlanetClick = onPlanetClick,
        )
    }
}

@Composable
private fun PlanetsScrollContent(
    planets: List<KeywordPlanetUi>,
    canvasHeight: Dp,
    onPlanetClick: (String) -> Unit,
) {
    val scroll = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(canvasHeight)
        ) {
            Image(
                painter = painterResource(id = com.a602.commonproject.designsystem.R.drawable.many_planet),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            BoxWithConstraints(Modifier.fillMaxSize()) {
                val maxW = maxWidth
                planets.forEach { p ->
                    val x = (maxW * p.xRatio).coerceIn(0.dp, maxW - p.size)

                    Image(
                        painter = painterResource(id = p.planetResId),
                        contentDescription = null,
                        modifier = Modifier
                            .offset(x = x, y = p.y)
                            .size(p.size)
                            .clickable { onPlanetClick(p.keywordId) },
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Memory - Planets")
@Composable
private fun Preview_Memory_Planets() {
    val items = listOf(
        TempKeywordDto("c1", "물건", "k1", "인형", 12),
        TempKeywordDto("c2", "음식", "k2", "밥", 20),
        TempKeywordDto("c3", "인물", "k3", "엄마", 30),
        TempKeywordDto("c4", "기념", "k4", "생일", 5),
        TempKeywordDto("c5", "여행", "k5", "바다", 50),
    )

    MemoryScreen(
        items = items,
        onPlanetClick = {}
    )
}
