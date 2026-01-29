package com.a602.commonproject.feature.gallery

import android.graphics.Bitmap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.coommonproject.ui.MediaDetailAction
import com.a602.coommonproject.ui.MediaDetailScreen
import com.a602.coommonproject.ui.MediaDetailUiState

data class HighlightResultNavKey(
    val slideshowId: String
) : NavKey


// 하이라이트 완료창
@Composable
fun Highlight(
    uiState: MediaDetailUiState,
    onAction: (MediaDetailAction) -> Unit,
    modifier: Modifier = Modifier,
){
    MediaDetailScreen(
        uiState = uiState,
        onAction = onAction,
        modifier = modifier
    )
}
private fun previewBitmap(
    width: Int = 1080,
    height: Int = 1440,
    color: Int
): ImageBitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bmp.eraseColor(color)
    return bmp.asImageBitmap()
}

@Preview(showBackground = true, name = "Video - Paused State", widthDp = 360, heightDp = 760)
@Composable
fun VideoPausedPreview() {
    MaterialTheme {
        val videoFrame = previewBitmap(color = Color.Black.toArgb())
        val frontFrame = previewBitmap(width = 600, height = 600, color = Color(0xFF9BB7D4).toArgb())

        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
            Highlight(
                uiState = MediaDetailUiState(
                    title = "하이라이트",
                    isVideo = true,
                    isPlaying = false,
                    rearImage = videoFrame,
                    frontImage = frontFrame,
                    meta = PolaroidMeta(
                        date = "2026.01.02",
                        role = "아빠",
                        comment = "아이의 첫 걸음마 순간입니다! 🎥"
                    )
                ),
                onAction = {}
            )
        }
    }
}


