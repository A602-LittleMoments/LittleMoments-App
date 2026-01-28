package com.a602.commonproject.feature.gallery

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.coommonproject.ui.MediaDetailAction
import com.a602.coommonproject.ui.MediaDetailScreen
import com.a602.coommonproject.ui.MediaDetailUiState

@Composable
fun PhotoDetail(
    uiState: MediaDetailUiState,
    onAction: (MediaDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MediaDetailScreen(
        uiState = uiState,
        onAction = onAction,
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
private fun PhotoDetailPreview() {
        val rear = Bitmap.createBitmap(1080, 1440, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.Gray.toArgb())
        }.asImageBitmap()
        val front = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.LightGray.toArgb())
        }.asImageBitmap()

        PhotoDetail(
            uiState = MediaDetailUiState(
                title = "사진 상세 보기",
                isVideo = false,
                isPlaying = false,
                rearImage = rear,
                frontImage = front,
                meta = PolaroidMeta(
                    date = "2024.05.20",
                    role = "엄마",
                    comment = "행복한 순간"
                )
            ),
            onAction = {}
        )

}
