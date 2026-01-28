package com.a602.commonproject.feature.memory

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random
import com.a602.commonproject.designsystem.R as DsR

private const val MAX_PLANETS = 20

// 카테고리별 아이콘 3개씩 (예시 — 너희 drawable로 교체)
private val categoryToPlanetPool: Map<String, List<Int>> = mapOf(
    "물건" to listOf(DsR.drawable.bear, DsR.drawable.ball, DsR.drawable.camera),
    "음식" to listOf(DsR.drawable.food1, DsR.drawable.food2, DsR.drawable.cupcake),
    "인물" to listOf(DsR.drawable.kids, DsR.drawable.bear, DsR.drawable.planet),
    "기념" to listOf(DsR.drawable.cupcake, DsR.drawable.rubberduck, DsR.drawable.planet),
    "여행" to listOf(DsR.drawable.camera, DsR.drawable.game, DsR.drawable.music),
)

// 매칭 실패 시 기본
private val defaultPlanetRes = DsR.drawable.music

private fun stableIndex(id: String, mod: Int): Int {
    if (mod <= 0) return 0
    val h = id.hashCode()
    val p = if (h == Int.MIN_VALUE) 0 else abs(h)
    return p % mod
}

// keywordId 기반: 항상 같은 아이콘 선택
private fun pickStablePlanetRes(categoryValue: String, keywordId: String): Int {
    val pool = categoryToPlanetPool[categoryValue].orEmpty()
    if (pool.isEmpty()) return defaultPlanetRes
    return pool[stableIndex(keywordId, pool.size)]
}

// collectionSize 기반 크기
private fun sizeFromCollectionSize(size: Int): Dp = when {
    size >= 50 -> 140.dp
    size >= 30 -> 128.dp
    size >= 15 -> 116.dp
    else -> 104.dp
}

/**
 * 위→아래 배치
 * x는 왼/중/오 중 랜덤(하지만 keywordId 기반이라 항상 동일하게 보임)
 * 바로 이전 lane과 같으면 금지
 */
fun buildPlanetsUiLaneLayout(
    items: List<TempKeywordDto>,
    viewportWidth: Dp,
    viewportHeight: Dp,
    bottomSafeArea: Dp = 96.dp,
    topSafeArea: Dp = 24.dp,
    sideSafeArea: Dp = 16.dp,
): PlanetLayoutResult {

    val limited = items.take(MAX_PLANETS)
    val singlePage = limited.size <= 10

    val baseHeight = viewportHeight
    val scrollExtra = 70.dp
    val targetHeight = if (singlePage) baseHeight else baseHeight + scrollExtra * (limited.size - 10)

    // lane 비율(너무 가장자리 붙지 않게)
    fun laneRatios(): List<Float> {
        if (viewportWidth <= 1.dp) return listOf(0.2f, 0.5f, 0.8f)
        val sideR = (sideSafeArea / viewportWidth).coerceIn(0f, 0.2f)
        val left = 0.18f + sideR
        val center = 0.5f
        val right = 0.82f - sideR
        return listOf(left, center, right)
    }

    val lanes = laneRatios()

    val planets = mutableListOf<KeywordPlanetUi>()

    var currentY = topSafeArea
    var prevLane = -1

    limited.forEach { item ->
        val size = sizeFromCollectionSize(item.collectionSize)
        val planetRes = pickStablePlanetRes(item.categoryValue, item.keywordId)

        // 고정 랜덤(자리 유지됨)
        val r = Random(item.keywordId.hashCode())
        var lane = r.nextInt(3)
        if (lane == prevLane) lane = (lane + 1 + r.nextInt(2)) % 3
        prevLane = lane

        val yMax = (targetHeight - bottomSafeArea - size).coerceAtLeast(currentY)
        val y = currentY.coerceIn(topSafeArea, yMax)

        // 1개면 중앙 고정
        val xRatio = if (limited.size == 1) 0.5f else lanes[lane]

        planets.add(
            KeywordPlanetUi(
                keywordId = item.keywordId,
                planetResId = planetRes,
                xRatio = xRatio,
                y = y,
                size = size,
            ),
        )

        // 다음 y 간격(크기에 따라 조정)
        val baseStep = 130.dp
        val step = max(baseStep.value, (size.value * 0.9f)).dp
        currentY += step
    }

    val maxBottom = planets.maxOfOrNull { it.y + it.size } ?: baseHeight
    val canvasHeight = if (singlePage) {
        baseHeight
    } else {
        max(baseHeight.value, (maxBottom + 140.dp + bottomSafeArea).value).dp
    }

    return PlanetLayoutResult(planets = planets, canvasHeight = canvasHeight)
}
