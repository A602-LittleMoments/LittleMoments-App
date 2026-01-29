/*
package com.a602.commonproject.feature.memory

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.coommonproject.ui.PolaroidData
import kotlinx.coroutines.delay

//@Composable
//fun MemoryRoute(
//    items: List<TempKeywordDto>,
//    onPlanetClick: (String) -> Unit,
//    loadingMillis: Long = 5_000L, // 5초 동안 로딩 화면(MemoryMakeScreen) 보여줌
//) {
//    // 데이터 없으면
//    if (items.isEmpty()) {
//        MemoryEmptyScreen()
//        return
//    }
//
//    // 행성이 바뀔 때만 보여줌
//    var showLoading by remember(items) { mutableStateOf(true) }
//
//    // planets에 값이 새로 들어오거나 바뀔 때 마다 실행
//    LaunchedEffect(items) {
//        showLoading = true
//        delay(loadingMillis)
//        showLoading = false
//    }
//    // 데이터 있으면 로딩 화면 보여준 후 메모리 화면
//    if (showLoading) {
//        MemoryMakeScreen(totalMillis = loadingMillis.toInt())
//    } else {
//        MemoryScreen(
//            items = items,
//            onPlanetClick = onPlanetClick,
//        )
//    }
//}

// 작동 확인용 화면 연결 및 더미 데이터
*/
/**
 * Memory 탭 엔트리
 * - 행성(MemoryScreen) -> 그리드(MediaGridScreen) -> 상세(MediaDetailRoute)
 * - 서버 전이라 더미 데이터로 흐름만 확인
 *//*


@Composable
fun MemoryRoute() {
    // 1) 화면 상태
    var selectedKeywordId by remember { mutableStateOf<String?>(null) }
    var selectedMediaId by remember { mutableStateOf<String?>(null) } // ✅ 상세용

    // 2) 더미 키워드 목록(행성용)
    val items = remember {
        listOf(
            TempKeywordDto("c1", "물건", "k1", "인형", 12),
            TempKeywordDto("c2", "음식", "k2", "밥", 20),
            TempKeywordDto("c3", "인물", "k3", "엄마", 30),
            TempKeywordDto("c4", "기념", "k4", "생일", 5),
            TempKeywordDto("c5", "여행", "k5", "바다", 50),
        )
    }

    // 3) 화면 분기
    when {
        // ---------- A) 행성 화면 ----------
        selectedKeywordId == null -> {
            MemoryScreen(
                items = items,
                onPlanetClick = { keywordId ->
                    selectedKeywordId = keywordId
                    selectedMediaId = null
                }
            )
        }

        // ---------- B) 그리드 화면 ----------
        selectedMediaId == null -> {
            MediaGridScreen(
                mapped = dummyMapped(selectedKeywordId!!),
                title = "키워드 앨범",
                onBackClick = {
                    selectedKeywordId = null
                    selectedMediaId = null
                },
                onMediaClick = { mediaId ->
                    // 여기서 상세로 넘어감!
                    selectedMediaId = mediaId
                }
            )
        }

        // ---------- C) 상세 화면 ----------
        else -> {
            // "상세 연결" 이 부분이 바로 여기야!
            MediaDetailRoute(
                keywordId = selectedKeywordId!!,
                mediaId = selectedMediaId!!,
                onBack = { selectedMediaId = null } // 뒤로 -> 그리드로
            )
        }
    }
}

*/
/* ---------------- 더미 Grid 데이터 ---------------- *//*


private fun dummyMapped(keywordId: String): List<Pair<String, PolaroidData>> {
    fun bmp(color: Int) =
        Bitmap.createBitmap(600, 800, Bitmap.Config.ARGB_8888)
            .apply { eraseColor(color) }
            .asImageBitmap()

    val base = when (keywordId) {
        "k1" -> 0xFFE6E6E6.toInt()
        "k2" -> 0xFFDDEEFF.toInt()
        else -> 0xFFFFE9D6.toInt()
    }

    // mediaId를 일부러 m1/m2로 맞춰놔야 상세 더미 규칙(isVideo = m2)이 먹음
    return listOf(
        "m1" to PolaroidData(
            rearImage = bmp(base),
            frontImage = bmp(base - 0x00111111),
            meta = PolaroidMeta("2026.01.20", "엄마", "$keywordId 더미 1")
        ),
        "m2" to PolaroidData(
            rearImage = bmp(base - 0x00222222),
            frontImage = bmp(base - 0x00333333),
            meta = PolaroidMeta("2026.01.21", "아빠", "$keywordId 더미 2")
        )
    )
}
*/
