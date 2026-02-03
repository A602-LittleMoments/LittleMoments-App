package com.a602.commonproject.feature.home

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random
import com.a602.commonproject.designsystem.R as DsR
import com.a602.commonproject.model.data.Collection
import kotlin.math.ceil
import kotlin.math.sqrt

private const val MAX_PLANETS = 20

// 카테고리별 아이콘 3개씩 (예시 — 너희 drawable로 교체)
// 카테고리별 아이콘 (너희 drawable로 교체)
// 카테고리끼리 겹치지 않게 고유하게 배정
private val categoryToPlanetPool: Map<String, List<Int>> = mapOf(
    "물건" to listOf(DsR.drawable.ball, DsR.drawable.game, DsR.drawable.rubberduck),
    "음식" to listOf(DsR.drawable.food1, DsR.drawable.food2),
    "인물" to listOf(DsR.drawable.kids, DsR.drawable.bear),
    "기념" to listOf(DsR.drawable.cupcake, DsR.drawable.moon, DsR.drawable.star),
    "여행" to listOf(DsR.drawable.camera, DsR.drawable.rocket, DsR.drawable.planet),
)

// 매칭 실패 시 기본
private val defaultPlanetRes = DsR.drawable.planet

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
    size >= 200 -> 120.dp
    size >= 100 -> 100.dp
    size >= 50 -> 80.dp
    else -> 80.dp
}

/**
 * 위→아래 배치
 * x는 왼/중/오 중 랜덤(하지만 keywordId 기반이라 항상 동일하게 보임)
 * 바로 이전 lane과 같으면 금지
 */
fun buildPlanetsUiLaneLayout(
    items: List<Collection>,
    viewportWidth: Dp,
    viewportHeight: Dp,
    bottomSafeArea: Dp = 96.dp,
    topSafeArea: Dp = 80.dp,
    sideSafeArea: Dp = 16.dp,
): PlanetLayoutResult {

    val limited = items.take(MAX_PLANETS)
    val singlePage = limited.size <= 10

    val baseHeight = viewportHeight
    // 스크롤 가능 영역 계산 단순화: 기본 높이 아니면 컨텐츠 기반 높이

    // 0. 아이콘 중복 최소화 (Round-Robin 배정)
    val assignedIcons = mutableMapOf<String, Int>()
    val grouped = limited.groupBy { it.categoryValue }
    
    grouped.forEach { (category, items) ->
        val sortedItems = items.sortedBy { it.keywordId }
        val pool = categoryToPlanetPool[category].orEmpty()
        if (pool.isNotEmpty()) {
            sortedItems.forEachIndexed { index, item ->
                assignedIcons[item.keywordId] = pool[index % pool.size]
            }
        }
    }
    
    val count = limited.size
    // True Random 배치를 위해 크기를 충분히 줄여줌 (충돌 최소화)
    val densityScale = if (count > 15) 0.6f else if (count > 10) 0.7f else if (count > 6) 0.85f else 1.0f
    
    // 배치 가능한 영역
    val minX = sideSafeArea.value
    // 너비를 100% 다 쓰면 텍스트가 잘릴 수 있으므로, 우측 여백을 좀 더 줌 (텍스트 길이 고려)
    val maxX = (viewportWidth - sideSafeArea).value 
    
    val minY = topSafeArea.value
    val maxY = (viewportHeight - bottomSafeArea).value
    
    // 겹침 검사를 위한 리스트
    data class PlacedItem(val x: Float, val y: Float, val w: Float, val h: Float)
    val placedItems = mutableListOf<PlacedItem>()
    val planets = mutableListOf<KeywordPlanetUi>()
    
    val labelHeight = 24.dp.value
    val margin = 8.dp.value // 아이템 간 최소 간격
    
    // 랜덤 시드 (화면 갱신될 때마다 위치가 바뀌면 정신사나우므로 고정)
    val globalSeed = limited.sumOf { it.keywordId.hashCode() }
    val rng = Random(globalSeed)
    
    // 큰 것부터 배치하면 성공률이 높음 (선택사항, 일단 순서대로)
    // ID 순으로 정렬해서 순서는 고정
    val sortedItems = limited.sortedBy { it.keywordId }

    sortedItems.forEach { item ->
        val baseSize = sizeFromCollectionSize(item.collectionSize)
        val sizeVal = (baseSize * densityScale).value
        val planetRes = assignedIcons[item.keywordId] ?: defaultPlanetRes
        val label = item.keywordValue.takeIf{ it.isNotBlank() } ?: item.categoryValue

        // 위치 찾기 (Rejection Sampling)
        // 최대 N번 시도하여 겹치지 않는 위치를 찾음
        var bestX = minX
        var bestY = minY
        var found = false
        
        // 시도 횟수
        val maxTries = 50
        
        for (i in 0 until maxTries) {
            // 랜덤 위치 생성 (가능 영역 내)
            // itemWidth = sizeVal
            // itemHeight = sizeVal + labelHeight
            val availableW = (maxX - minX - sizeVal).coerceAtLeast(0f)
            val availableH = (maxY - minY - sizeVal - labelHeight).coerceAtLeast(0f)
            
            val candX = minX + rng.nextFloat() * availableW
            val candY = minY + rng.nextFloat() * availableH
            
            // 겹침 검사
            val candR = candX + sizeVal
            val candB = candY + sizeVal + labelHeight
            
            var overlap = false
            for (p in placedItems) {
                // 사각형 겹침 판정 (with margin)
                // A.L < B.R && A.R > B.L && A.T < B.B && A.B > B.T
                if (candX < p.x + p.w + margin && 
                    candR > p.x - margin &&
                    candY < p.y + p.h + margin &&
                    candB > p.y - margin) {
                    overlap = true
                    break
                }
            }
            
            if (!overlap) {
                bestX = candX
                bestY = candY
                found = true
                break
            }
        }
        
        // 만약 50번 시도해도 자리를 못 찾았으면?
        // (화면이 너무 꽉 찼을 때)
        // -> 그냥 랜덤 위치 or 가장 마지막 시도 위치에 배치 (겹치더라도 표시하는게 중요)
        if (!found) {
             // 겹치더라도 배치 (Fallback)
             // 사용자 경험상 아예 안 나오는 것보단 낫고, scale을 줄였으므로 드물 것임
        }

        planets.add(
            KeywordPlanetUi(
                keywordId = item.keywordId,
                planetResId = planetRes,
                xRatio = ((bestX + sizeVal / 2) / viewportWidth.value).coerceIn(0f, 1f),
                y = bestY.dp,
                size = sizeVal.dp,
                label = label,
            )
        )
        
        placedItems.add(PlacedItem(bestX, bestY, sizeVal, sizeVal + labelHeight))
    }

    // 전체 캔버스 높이 (고정)
    val canvasHeight = baseHeight

    return PlanetLayoutResult(planets = planets, canvasHeight = canvasHeight)
}

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
