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
    "물건" to listOf(DsR.drawable.ic_thing1, DsR.drawable.ic_thing2, DsR.drawable.ic_thing3,DsR.drawable.ic_thing4,DsR.drawable.ic_thing5,DsR.drawable.ic_thing6,DsR.drawable.ic_thing7,DsR.drawable.ic_thing8,DsR.drawable.ic_thing9,DsR.drawable.ic_thing10,DsR.drawable.ic_thing11,DsR.drawable.ic_thing12,DsR.drawable.ic_thing13),
    "행동" to listOf(DsR.drawable.ic_action1, DsR.drawable.ic_action2,DsR.drawable.ic_action3,DsR.drawable.ic_action4,DsR.drawable.ic_action5,DsR.drawable.ic_action6,DsR.drawable.ic_action7,DsR.drawable.ic_action8,DsR.drawable.ic_action9),
    "의상" to listOf(DsR.drawable.ic_clothes1, DsR.drawable.ic_clothes2,DsR.drawable.ic_clothes3,DsR.drawable.ic_clothes4,DsR.drawable.ic_clothes5,DsR.drawable.ic_clothes6,DsR.drawable.ic_clothes7,DsR.drawable.ic_clothes8),
    "장소" to listOf(DsR.drawable.ic_place1, DsR.drawable.ic_place2, DsR.drawable.ic_place3,DsR.drawable.ic_place4,DsR.drawable.ic_place5,DsR.drawable.ic_place6,DsR.drawable.ic_place7,DsR.drawable.ic_place8,DsR.drawable.ic_place9),
    "분위기" to listOf(DsR.drawable.ic_emotion1, DsR.drawable.ic_emotion2, DsR.drawable.ic_emotion3,DsR.drawable.ic_emotion4,DsR.drawable.ic_emotion5,DsR.drawable.ic_emotion6,DsR.drawable.ic_emotion7,DsR.drawable.ic_emotion8,DsR.drawable.ic_emotion9),
)

// 매칭 실패 시 기본
private val defaultPlanetRes = DsR.drawable.planet

// 전역 캐시: 앱 실행 중 키워드별 아이콘 매핑 유지
private object PlanetIconCache {
    val mapping = mutableMapOf<String, Int>()
}

/**
 * 카테고리별 아이콘을 순차적으로(1 -> 2 -> 3 ...) 우선 배정하고,
 * 한 번 배정된 아이콘은 앱 실행 동안 유지합니다.
 */
fun assignDiversePlanets(items: List<Collection>): Map<String, Int> {
    // 1. 카테고리별로 아이템 그룹화
    val grouped = items.groupBy { it.categoryValue }

    grouped.forEach { (category, categoryItems) ->
        val pool = categoryToPlanetPool[category].orEmpty()
        if (pool.isEmpty()) {
            categoryItems.forEach { PlanetIconCache.mapping[it.keywordId] = defaultPlanetRes }
            return@forEach
        }

        // 해당 카테고리에서 이미 사용 중인 아이콘들 파악 (중복 최소화를 위해)
        // [Fix] MutableSet으로 변경하여 이번 배치(loop) 안에서 할당된 것도 즉시 반영
        val usedIconsInCategory = PlanetIconCache.mapping.entries
            .filter { entry -> pool.contains(entry.value) }
            .map { it.value }
            .toMutableSet()

        // 할당이 필요한 키워드들 (이미 캐시에 있는 건 패스)
        val itemsNeedingAssignment = categoryItems
            .map { it.keywordId }
            .distinct()
            .filter { !PlanetIconCache.mapping.containsKey(it) }

        // 순차 할당 시작
        for (keywordId in itemsNeedingAssignment) {
            // 풀(pool) 순서대로 탐색: 아직 안 쓰인 아이콘 찾기 (1번, 2번, 3번...)
            var assignedResId: Int? = null
            
            // 1순위: 사용되지 않은 아이콘 중 가장 앞 번호
            for (resId in pool) {
                if (!usedIconsInCategory.contains(resId)) {
                    assignedResId = resId
                    break
                }
            }

            // 2순위: 모든 아이콘이 다 쓰였다면, 그냥 앞에서부터 순서대로 (Round-robin 느낌)
            if (assignedResId == null) {
                // 현재 할당된 총 개수를 구해서 모듈러 연산으로 순환
                // (기존 매핑 수 + 현재 루프 인덱스는 복잡하므로,
                //  그냥 랜덤보다는 "일관된 해시"나 "순차"가 나음. 여기선 순차)
                val currentIndex = PlanetIconCache.mapping.size 
                assignedResId = pool[currentIndex % pool.size]
            }
            
            // 캐시에 저장
            PlanetIconCache.mapping[keywordId] = assignedResId!!
            
            // [Critical Fix] 방금 할당한 아이콘도 사용 중 목록에 추가해야
            // 다음 루프에서 같은 아이콘을 또 할당하지 않음!
            usedIconsInCategory.add(assignedResId)
        }
    }

    // 현재 요청된 items에 대한 매핑만 반환 (또는 전체 캐시 반환해도 되지만 인터페이스 유지)
    return items.associate { it.keywordId to (PlanetIconCache.mapping[it.keywordId] ?: defaultPlanetRes) }
}


/**
 * 특정 키워드(Collection)에 대한 아이콘 리소스 ID를 반환합니다.
 * - 이미 캐시(PlanetIconCache)에 할당된 값이 있다면 그 값을 반환합니다. (메인 화면과 일치 보장)
 * - 없다면 새로운 아이콘을 순차적으로(1->2->3...) 할당하고 캐시에 저장한 뒤 반환합니다.
 */
fun getPlanetIcon(item: Collection): Int {
    // 1. 캐시 확인
    if (PlanetIconCache.mapping.containsKey(item.keywordId)) {
        return PlanetIconCache.mapping[item.keywordId]!!
    }

    // 2. 캐시에 없으면 새로 할당 (assignDiversePlanets 로직 재사용)
    //    단일 아이템이라도 전체 로직을 태우면 캐시에 안전하게 등록됨
    assignDiversePlanets(listOf(item))
    
    return PlanetIconCache.mapping[item.keywordId] ?: defaultPlanetRes
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
