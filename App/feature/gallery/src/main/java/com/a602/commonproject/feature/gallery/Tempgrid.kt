package com.a602.commonproject.feature.gallery


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.model.data.TempMedia

@Composable
fun TempImageGrid(
    medias: List<TempMedia>,
    isSelectMode: Boolean,
    selectedIds: List<String>,
    onClick: (TempMedia) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = medias,
            key = { it.id }
        ) { media ->
            val isSelected = selectedIds.contains(media.id)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { onClick(media) }
            ) {
                // 이미지
                AsyncImage(
                    model = media.localUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // 선택 모드일 때만 오버레이 표시
                if (isSelectMode) {
                    // 선택된 경우 어두운 오버레이
                    if (isSelected) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = main.copy(alpha = 0.4f)
                        ) {}
                    }

                    // 체크박스
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                            .align(Alignment.TopEnd),
                        shape = CircleShape,
                        color = if (isSelected) main else Color.White.copy(alpha = 0.8f),
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) Color.White else Color.Gray.copy(alpha = 0.5f)
                        ),
                        shadowElevation = 2.dp
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun TempImageGridPreview() {
    val fakeMedias = List(6) { i ->
        TempMedia(
            id = i.toString(),
            localUri = "/path/to/temp/${i}.jpg",
            takenAt = System.currentTimeMillis(),
            expirationDate = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L
        )
    }

    LMTheme {
        Surface(color = Color(0xFFFDF7F2)) {
            TempImageGrid(
                medias = fakeMedias,
                isSelectMode = true,
                selectedIds = listOf("1", "3"),
                onClick = {}
            )
        }
    }
}
