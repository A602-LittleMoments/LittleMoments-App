package com.a602.coommonproject.ui

import Polaroid
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.model.data.SharedMedia

@Composable
fun GridPolaroid(
    media: SharedMedia,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f) // Polaroid 내부 비율과 맞춤
    ) {
        Polaroid(
            media = media,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
        )
    }
}

@Composable
fun GalleryGridPolaroid(
    medias: List<SharedMedia>,
    modifier: Modifier = Modifier,
    onClick: (SharedMedia) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = medias,
            key = { it.id }
        ) { item ->

            GridPolaroid(
                media = item,
                onClick = { onClick(item) }
            )
        }
    }
}

@Composable
fun SelectableGalleryGrid(
    medias: List<SharedMedia>,
    isSelectMode: Boolean,
    selectedIds: List<String>,
    onClick: (SharedMedia) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = medias,
            key = { it.id }
        ) { item ->
            val isSelected = selectedIds.contains( item.id)

            Box(
                modifier = Modifier
                    .padding(4.dp)
            ) {

                GridPolaroid(
                    media = item,
                    onClick = { onClick(item) }
                )

                if (isSelectMode) {

                    if (isSelected) {
                        Surface(
                            modifier = Modifier.matchParentSize(),
                            color = Color.Black.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(18.dp)
                        ) {}
                    }

                    Surface(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(28.dp)
                            .align(Alignment.TopStart),
                        shape = CircleShape,
                        color = if (isSelected) main else Color.White.copy(alpha = 0.9f),
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) main else Color.LightGray
                        )
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



/*
* 프리뷰 용
* */

private fun fakeMediaList(): List<SharedMedia> {
    return List(6) { i ->
        SharedMedia(
            id = i.toString(),
            type = SharedMedia.MediaType.PHOTO,
            localUri = null,
            remoteUrl = "https://picsum.photos/600/80${i}",
            thumbnailUrl = null,
            subLocalUri = null,
            subRemoteUrl = "https://picsum.photos/300/40${i}",
            subThumbnailUrl = null,
            cameraFacing = "DUAL",
            caption = "프리뷰입니다프리뷰프리뷰프리뷰프리뷰",
            dateTaken = System.currentTimeMillis(),
            orientation = 0,
            uploaderName = "엄마",
            syncStatus = SharedMedia.SyncStatus.SYNCED
        )
    }
}
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun GalleryGridPreview() {
    MaterialTheme {
        Surface(color = Color(0xFFFDF7F2)) {
            GalleryGridPolaroid(
                medias = fakeMediaList(),
                onClick = {}
            )
        }
    }
}
@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 640,
    name = "선택 모드"
)
@Composable
fun SelectableGalleryPreview() {
    MaterialTheme {
        Surface(color = Color(0xFFFDF7F2)) {
            SelectableGalleryGrid(
                medias = fakeMediaList(),
                isSelectMode = true,
                selectedIds = listOf("1", "3"),
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Selectable Preview Interactive")
@Composable
fun SelectableGalleryInteractivePreview() {
    val medias = fakeMediaList()

    // 프리뷰용
    val selectedIds = remember { mutableStateListOf<String>("1", "3") }

    MaterialTheme {
        Surface(color = Color(0xFFFDF7F2)) {
            SelectableGalleryGrid(
                medias = medias,
                isSelectMode = true,
                selectedIds = selectedIds.toList(),
                onClick = { media ->
                    if (selectedIds.contains(media.id)) {
                        selectedIds.remove(media.id)
                    } else {
                        selectedIds.add(media.id)
                    }
                }
            )
        }
    }
}
