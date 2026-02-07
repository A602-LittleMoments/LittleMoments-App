package com.a602.coommonproject.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import Polaroid

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
                .clickable { onClick() },
            useThumbnail = true // 🚀 Grid에서는 썸네일 사용 강제
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

// 🚀 [NEW] 프레임 없는 사진 아이템 (사용자 요청)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FramelessPhotoItem(
    media: SharedMedia,
    modifier: Modifier = Modifier,
    isSelectMode: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    // Polaroid.kt의 내부 로직 재사용 (프레임만 제거)
    // 썸네일 우선 사용
    val rearUrl = media.thumbnailUrl ?: media.remoteUrl ?: media.localUri
    val frontUrl = media.subThumbnailUrl ?: media.subRemoteUrl ?: media.subLocalUri

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(12.dp)) // Round applied
            .background(Color.LightGray) // 로딩 전 배경
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            )
    ) {
        // Rear (Main)
        AsyncImage(
            model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                .data(rearUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Front (PIP) - 있으면 표시
        if (frontUrl != null) {
            AsyncImage(
                model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    .data(frontUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .width(40.dp) // 작게 표시 (비율 유지)
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(8.dp)) // PIP also rounded slightly less
                    .background(Color.Black), // 테두리 느낌?
                contentScale = ContentScale.Crop
            )
        }

        // 🚀 [ADD] Selection UI
        if (isSelectMode) {
            // Selected Overlay
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                )
            }

            // Checkbox
            Surface(
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
                    .align(Alignment.TopStart),
                shape = CircleShape,
                color = if (isSelected) main else Color.White.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, if (isSelected) main else Color.LightGray)
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

@Composable
fun GalleryGridFrameless(
    medias: List<SharedMedia>,
    modifier: Modifier = Modifier,
    isSelectMode: Boolean = false,
    selectedIds: Set<String> = emptySet(),
    state: androidx.compose.foundation.lazy.grid.LazyGridState = androidx.compose.foundation.lazy.grid.rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(12.dp),
    headerContent: (@Composable () -> Unit)? = null,
    onClick: (SharedMedia) -> Unit = {},
    onLongClick: (SharedMedia) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxSize(),
        state = state,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (headerContent != null) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                headerContent()
            }
        }
        items(
            items = medias,
            key = { it.id }
        ) { item ->
            FramelessPhotoItem(
                media = item,
                isSelectMode = isSelectMode,
                isSelected = selectedIds.contains(item.id),
                onClick = { onClick(item) },
                onLongClick = { onLongClick(item) }
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
