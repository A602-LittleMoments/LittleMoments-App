package com.a602.commonproject.feature.memory

import android.graphics.Bitmap
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.coommonproject.ui.MediaDetailAction
import com.a602.coommonproject.ui.MediaDetailScreen
import com.a602.coommonproject.ui.MediaDetailUiState

/**
 * 상세 화면 "조립/상태" 담당 Route
 * - 지금: 더미 uiState 생성 + 재생 토글 처리
 * - 나중: 서버 데이터로 uiState만 교체하면 됨
 */
@Composable
fun MediaDetailRoute(
    keywordId: String,
    mediaId: String,
    onBack: () -> Unit,
) {
    // 상세 화면 상태(재생 토글 때문에 var로 들고 있어야 함)
    var uiState by remember(keywordId, mediaId) {
        mutableStateOf(dummyDetailUiState(keywordId, mediaId))
    }

    MediaDetailScreen(
        uiState = uiState,
        onAction = { action ->
            when (action) {
                MediaDetailAction.Back -> onBack()
                MediaDetailAction.TogglePlayPause -> {
                    uiState = uiState.copy(isPlaying = !uiState.isPlaying)
                }
                MediaDetailAction.Delete -> { /* TODO: 나중 */ }
                MediaDetailAction.Download -> { /* TODO */ }
                MediaDetailAction.Edit -> { /* TODO */ }
            }
        }
    )
}

/* -------------------- 더미 생성 -------------------- */

private fun dummyDetailUiState(keywordId: String, mediaId: String): MediaDetailUiState {
    val base = when (keywordId) {
        "k1" -> 0xFF1B1B1F.toInt()
        "k2" -> 0xFF223344.toInt()
        else -> 0xFF332211.toInt()
    }

    // 더미 규칙: m2면 영상처럼(재생 버튼 보이게)
    val isVideo = mediaId == "m2"

    return MediaDetailUiState(
        title = "자세히 보기",
        isVideo = isVideo,
        isPlaying = false,
        rearImage = solidBitmap(color = base),
        frontImage = solidBitmap(color = base + 0x00111111),
        meta = PolaroidMeta(
            date = "2026.01.20",
            role = if (mediaId == "m1") "엄마" else "아빠",
            comment = "$keywordId / $mediaId 더미 상세"
        )
    )
}

private fun solidBitmap(
    width: Int = 1080,
    height: Int = 1440,
    color: Int
): ImageBitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bmp.eraseColor(color)
    return bmp.asImageBitmap()
}


/* ---------------- Preview ---------------- */

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "Detail - Photo")
@Composable
private fun Preview_MediaDetail_Photo() {
    MaterialTheme {
        MediaDetailRoute(
            keywordId = "k1",
            mediaId = "m1",
            onBack = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "Detail - Video")
@Composable
private fun Preview_MediaDetail_Video() {
    MaterialTheme {
        MediaDetailRoute(
            keywordId = "k2",
            mediaId = "m2",
            onBack = {}
        )
    }
}
