package com.a602.coommonproject.ui

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.Polaroid
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.commonproject.designsystem.theme.main

data class PolaroidData(
    val rearImage: ImageBitmap,
    val frontImage: ImageBitmap,
    val meta: PolaroidMeta
)


@Composable
fun GalleryGridPolaroid(
    polaroids: List<PolaroidData>,
    modifier: Modifier = Modifier,
    onClick: (PolaroidData) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(polaroids, key = { it.rearImage.hashCode() }) { item ->
            Polaroid(
                rearImage = item.rearImage,
                frontImage = item.frontImage,
                meta = item.meta,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clickable { onClick(item) }
            )
        }
    }
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

private val samplePolaroids = List(9) { i ->
    PolaroidData(
        rearImage = previewBitmap(color = 0xFF1B1B1F.toInt() + i * 0x00101010),
        frontImage = previewBitmap(width = 200, height = 200, color = 0xFF9BB7D4.toInt() + i * 0x00080808),
        meta = PolaroidMeta(
            date = "2026.01.0${i + 1}",
            role = "엄마",
            comment =  "오늘 사진"
        )
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun GalleryGridPolaroidPreview() {
    MaterialTheme {
        GalleryGridPolaroid(polaroids = samplePolaroids)
    }
}


@Composable
fun SelectableGalleryGrid(
    polaroids: List<PolaroidData>,
    isSelectMode: Boolean,
    selectedIds: List<Int>,
    onItemClick: (PolaroidData) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(polaroids, key = { it.rearImage.hashCode() }) { item ->
            val isSelected = selectedIds.contains(item.rearImage.hashCode())

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .padding(4.dp)
            ) {
                // 1. 기존 폴라로이드
                Polaroid(
                    rearImage = item.rearImage,
                    frontImage = item.frontImage,
                    meta = item.meta,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onItemClick(item) }
                )

                // 2. 선택 모드일 때만 덮어씌우는 UI
                if (isSelectMode) {
                    // 선택 시 어두워지는 효과
                    if (isSelected) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Black.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp) // Polaroid 모서리에 맞춤
                        ) {}
                    }

                    // 왼쪽 상단 체크박스
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(28.dp)
                            .align(Alignment.TopStart),
                        shape = CircleShape,
                        color = if (isSelected) main else Color.White.copy(alpha = 0.9f),
                        border = BorderStroke(1.5.dp, if (isSelected) main else Color.LightGray)
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "선택 모드 ON (2개 선택)")
@Composable
fun SelectableGalleryPreview() {
    MaterialTheme {
        val mockSelectedIds = listOf(
            samplePolaroids[0].rearImage.hashCode(),
            samplePolaroids[2].rearImage.hashCode()
        )

        Surface(color = Color(0xFFFDF7F2)) {
            SelectableGalleryGrid(
                polaroids = samplePolaroids,
                isSelectMode = true,
                selectedIds = mockSelectedIds,
                onItemClick = { /* 클릭 시 동작 */ }
            )
        }
    }
}
