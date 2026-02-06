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

private const val MAX_PLANETS = 7

// 카테고리별 아이콘 3개씩 (예시 — 너희 drawable로 교체)
// 카테고리별 아이콘 (너희 drawable로 교체)
// 카테고리끼리 겹치지 않게 고유하게 배정
private val categoryToPlanetPool: Map<String, List<Int>> = mapOf(
    "물건" to listOf(DsR.drawable.ic_thing1, DsR.drawable.ic_thing2, DsR.drawable.ic_thing3,DsR.drawable.ic_thing4,DsR.drawable.ic_thing5,DsR.drawable.ic_thing6),
    "음식" to listOf(DsR.drawable.ic_food1, DsR.drawable.ic_food2,DsR.drawable.ic_food3,DsR.drawable.ic_food4,DsR.drawable.ic_food5,DsR.drawable.ic_food6,DsR.drawable.ic_food7),
    "인물" to listOf(DsR.drawable.ic_person1, DsR.drawable.ic_person2,DsR.drawable.ic_person3,DsR.drawable.ic_person4,DsR.drawable.ic_person5,DsR.drawable.ic_person6,DsR.drawable.ic_person7),
    "기념" to listOf(DsR.drawable.ic_event1, DsR.drawable.ic_event2, DsR.drawable.ic_event3,DsR.drawable.ic_event4,DsR.drawable.ic_event5),
    "여행" to listOf(DsR.drawable.ic_travle1, DsR.drawable.ic_travle2, DsR.drawable.ic_travle3,DsR.drawable.ic_travle4,DsR.drawable.ic_travle5),
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
internal fun pickStablePlanetRes(categoryValue: String, keywordId: String): Int {
    val pool = categoryToPlanetPool[categoryValue].orEmpty()
    if (pool.isEmpty()) return defaultPlanetRes
    return pool[stableIndex(keywordId, pool.size)]
}

/**
 * 카테고리별로 사용 가능한 행성 아이콘을 최대한 골고루(Round-robin) 배정합니다.
 * 동일한 keywordsId에는 동일한 아이콘이 배정되도록 map에 기록합니다.
 */
fun assignDiversePlanets(items: List<Collection>): Map<String, Int> {
    val assignment = mutableMapOf<String, Int>()
    
    // 1. 카테고리별로 아이템 그룹화
    val grouped = items.groupBy { it.categoryValue }

    grouped.forEach { (category, categoryItems) ->
        val pool = categoryToPlanetPool[category].orEmpty()
        
        if (pool.isNotEmpty()) {
            // 2. 풀을 섞어서 매번 다른 느낌을 주되 (diversity)
            //    목록 내에서는 최대한 겹치지 않게 순서대로 배정
            val shuffledPool = pool.shuffled()
            val poolSize = shuffledPool.size
            
            // keywordId 기준으로 중복 제거된 목록만 순회해야 함 (동일 키워드는 같은 아이콘)
            // (입력 items에 동일 키워드가 중복되어 있을 수 있으므로)
            val uniqueItems = categoryItems.distinctBy { it.keywordId }

            uniqueItems.forEachIndexed { index, item ->
                // Round-robin selection
                val resId = shuffledPool[index % poolSize]
                assignment[item.keywordId] = resId
            }
        } else {
            // 풀이 없으면 기본 아이콘
            categoryItems.forEach { assignment[it.keywordId] = defaultPlanetRes }
        }
    }
    return assignment
}


// collectionSize 기반 크기
// collectionSize 기반 크기 (사용자 요청: 좀 더 크게)
private fun sizeFromCollectionSize(size: Int): Dp = when {
    size >= 200 -> 180.dp // 150 -> 180
    size >= 100 -> 160.dp // 130 -> 160
    size >= 50 -> 140.dp  // 110 -> 140
    size >= 20 -> 120.dp   // 94 -> 120
    size >= 10 -> 100.dp   // 80 -> 100
    else -> 90.dp         // 70 -> 90
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
    seed: Long? = null,
): PlanetLayoutResult {

    val limited = items.take(MAX_PLANETS)
    val singlePage = limited.size <= 10

    val baseHeight = viewportHeight
    // 스크롤 가능 영역 계산 단순화: 기본 높이 아니면 컨텐츠 기반 높이

    // 0. 아이콘 중복 최소화 (Round-Robin 배정)
    // 0. 아이콘 중복 최소화 로직 제거 (Dialog와 일치를 위해 pickStablePlanetRes 사용)
    // val assignedIcons = mutableMapOf<String, Int>()
    // val grouped = limited.groupBy { it.categoryValue } ... (removed)
    
    val count = limited.size
    
    // 배치 가능한 영역
    val minX = sideSafeArea.value
    // 너비를 100% 다 쓰면 텍스트가 잘릴 수 있으므로, 우측 여백을 좀 더 줌
    val maxX = (viewportWidth - sideSafeArea).value 
    
    val minY = topSafeArea.value
    val maxY = (viewportHeight - bottomSafeArea).value
    
    // Box Collision Logic (Rectangle)
    // 원형 충돌은 '타이틀(글자)'가 겹치는 것을 완벽히 막기 어려움. 
    // 사용자 요청: "제목 부분도 겹치면 안될 거 같은데"
    // -> 따라서 (Planet Size + Label Height)를 포함하는 직사각형(Box) 충돌 검사로 변경.
    
    data class PlacedBox(val x: Float, val y: Float, val r: Float, val b: Float)
    val placedBoxes = mutableListOf<PlacedBox>()
    val planets = mutableListOf<KeywordPlanetUi>()
    
    val labelHeight = 80.dp.value // [Fix] Increased even more (60 -> 80)
    val margin = 8.dp.value // Increased margin

    val calculatedSeed = seed ?: limited.sumOf { it.keywordId.hashCode() }.toLong()
    val rng = Random(calculatedSeed)
    
    // 1. 큰 것부터 배치 (내림차순 정렬) - 사용자 요청
    val sortedItems = limited.sortedByDescending { it.collectionSize }

    // [Fix] Planet Diversity Algorithm
    // 카테고리별로 골고루 배정된 매핑 테이블 생성
    val planetMap = assignDiversePlanets(limited)

    sortedItems.forEach { item ->
        val baseSize = sizeFromCollectionSize(item.collectionSize)
        val densityScale = if (count > 15) 0.8f else if (count > 10) 0.9f else 1.0f
        
        var currentScale = 1.0f
        var bestX: Float = minX
        var bestY: Float = minY
        var finalSizeVal = 0f
        var found = false
        
        // Adaptive Resizing
        val scaleAttempts = 5 
        
        outer@ for (s in 0 until scaleAttempts) {
            val scaleFactor = 1.0f - (s * 0.1f) 
            val sizeVal = (baseSize * densityScale * scaleFactor).value
            
            // 직사각형 크기 (Planet + Label)
            val collisionPadding = 10.dp.value // [Fix] Add explicit padding for visual separation
            val itemW = sizeVal + collisionPadding 
            val itemH = sizeVal + labelHeight
            
            val maxTries = 500 // [Fix] Try harder to find space (200 -> 500)
            
            // 랜덤 마진 (기존 4.dp 고정 -> 2~12.dp 랜덤)
            // 아이템마다 여백이 달라지면 "열이 맞춰진 느낌"이 깨지고 더 불규칙해 보임.
            val randomMargin = (2 + rng.nextFloat() * 10).dp.value

            for (i in 0 until maxTries) {
                // 랜덤 위치 (좌상단 기준)
                val availableW = (maxX - minX - itemW).coerceAtLeast(0f)
                val availableH = (maxY - minY - itemH).coerceAtLeast(0f)
                
                val candX = minX + rng.nextFloat() * availableW
                val candY = minY + rng.nextFloat() * availableH
                
                // 마진 포함된 후보 영역 (충돌 검사용)
                val cLeft = candX - randomMargin
                val cTop = candY - randomMargin
                val cRight = candX + itemW + randomMargin
                val cBottom = candY + itemH + randomMargin
                
                // Box Collision Check
                var overlap = false
                for (p in placedBoxes) {
                    // A.Left < B.Right && A.Right > B.Left && A.Top < B.Bottom && A.Bottom > B.Top
                    if (cLeft < p.r && cRight > p.x && cTop < p.b && cBottom > p.y) {
                        overlap = true
                        break
                    }
                }
                
                if (!overlap) {
                    bestX = candX
                    bestY = candY
                    finalSizeVal = sizeVal
                    
                    // 배치 확정 (실제 영역 + 마진 기록)
                    placedBoxes.add(PlacedBox(cLeft, cTop, cRight, cBottom))
                    found = true
                    break@outer
                }
            }
        }
        
        // Fallback: 겹치더라도 "최소한으로" 겹치는 곳 찾기 (Best Fit)
        if (!found) {
            val minScale = 0.75f 
            finalSizeVal = (baseSize * densityScale * minScale).value
            val collisionPadding = 8.dp.value // Fallback padding
            val itemW = finalSizeVal + collisionPadding
            val itemH = finalSizeVal + labelHeight
            val randomMargin = 2.dp.value // Fallback에선 최소 마진 사용

            var minOverlapArea = Float.MAX_VALUE
            var bestFallbackX = minX
            var bestFallbackY = minY
            
            val fallbackTries = 100 // [Fix] Try harder in fallback too (50 -> 100)
            
            for (k in 0 until fallbackTries) {
                val candX = minX + rng.nextFloat() * (maxX - minX - itemW)
                val candY = minY + rng.nextFloat() * (maxY - minY - itemH)
                
                val cLeft = candX - randomMargin
                val cTop = candY - randomMargin
                val cRight = candX + itemW + randomMargin
                val cBottom = candY + itemH + randomMargin
                
                // 겹침 면적 계산
                var currentOverlap = 0f
                for (p in placedBoxes) {
                    // 교차 영역 구하기
                    val interL = max(cLeft, p.x)
                    val interR = kotlin.math.min(cRight, p.r)
                    val interT = max(cTop, p.y)
                    val interB = kotlin.math.min(cBottom, p.b)
                    
                    if (interL < interR && interT < interB) {
                         currentOverlap += (interR - interL) * (interB - interT)
                    }
                }
                
                if (currentOverlap < minOverlapArea) {
                    minOverlapArea = currentOverlap
                    bestFallbackX = candX
                    bestFallbackY = candY
                    
                    if (currentOverlap == 0f) break // 운좋게 빈공간 찾음
                }
            }
            
            bestX = bestFallbackX
            bestY = bestFallbackY
            
            // 기록
            val cLeft = bestX - randomMargin
            val cTop = bestY - randomMargin
            val cRight = bestX + itemW + randomMargin
            val cBottom = bestY + itemH + randomMargin
            placedBoxes.add(PlacedBox(cLeft, cTop, cRight, cBottom))
        }

        // [Fix] Use the diverse map
        val planetRes = planetMap[item.keywordId] ?: defaultPlanetRes
        val label = item.keywordValue.takeIf{ it.isNotBlank() } ?: item.categoryValue

        planets.add(
            KeywordPlanetUi(
                keywordId = item.keywordId,
                planetResId = planetRes,
                xRatio = ((bestX + finalSizeVal / 2) / viewportWidth.value).coerceIn(0f, 1f),
                y = bestY.dp,
                size = finalSizeVal.dp,
                label = label,
            )
        )
    }

    return PlanetLayoutResult(planets = planets, canvasHeight = baseHeight)
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
