package com.a602.commonproject.feature.memory

import android.graphics.Bitmap
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.coommonproject.ui.GalleryGridPolaroid
import com.a602.coommonproject.ui.PolaroidData

@Composable
fun MediaGridScreen(
    mapped: List<Pair<String, PolaroidData>>,
    title: String,
    onBackClick: () -> Unit,
    onMediaClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            LMTopAppBar(
                title = title,
                onNavigationClick = onBackClick,
            )
        }
    ) { innerPadding ->
        // TopAppBar 높이만큼 자동으로 패딩됨
        MediaGridContent(
            mapped = mapped,
            onMediaClick = onMediaClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun MediaGridContent(
    mapped: List<Pair<String, PolaroidData>>,
    onMediaClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    GalleryGridPolaroid(
        polaroids = mapped.map { it.second },
        modifier = modifier,
        onClick = { clicked ->
            mapped.firstOrNull { it.second == clicked }
                ?.first
                ?.let(onMediaClick)
        }
    )
}

private fun previewBitmap(color: Int) =
    Bitmap.createBitmap(600, 800, Bitmap.Config.ARGB_8888).apply {
        eraseColor(color)
    }.asImageBitmap()

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun MediaGridPreview() {
    val mapped = remember {
        listOf(
            "m1" to PolaroidData(
                rearImage = previewBitmap(0xFFE6E6E6.toInt()),
                frontImage = previewBitmap(0xFFBDBDBD.toInt()),
                meta = PolaroidMeta(date = "2026.01.20", role = "엄마", comment = "첫 산책 😊")
            ),
            "m2" to PolaroidData(
                rearImage = previewBitmap(0xFFDDEEFF.toInt()),
                frontImage = previewBitmap(0xFFAACCEE.toInt()),
                meta = PolaroidMeta(date = "2026.01.21", role = "아빠", comment = "웃음")
            ),
            "m3" to PolaroidData(
                rearImage = previewBitmap(0xFFFFE9D6.toInt()),
                frontImage = previewBitmap(0xFFFFD2A6.toInt()),
                meta = PolaroidMeta(date = "2026.01.22", role = "엄마", comment = "놀이")
            ),
        )
    }

    MediaGridScreen(
        mapped = mapped,
        title = "추억 사진 모음",
        onBackClick = {},
        onMediaClick = {},
    )
}
