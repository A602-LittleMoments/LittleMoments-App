package com.a602.commonproject.feature.memory

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.coommonproject.ui.GalleryGridPolaroid
import com.a602.commonproject.model.data.SharedMedia

@Composable
fun MediaGridScreen(
    title: String,
    medias: List<SharedMedia>,
    onBackClick: () -> Unit,
    onMediaClick: (mediaId: String) -> Unit,
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
        GalleryGridPolaroid(
            medias = medias,
            modifier = modifier.padding(
                top = innerPadding.calculateTopPadding(),
            ),
            onClick = { clicked ->
                onMediaClick(clicked.id)
            }
        )
    }
}

/* -------- Preview -------- */

private fun fakeMediaList(): List<SharedMedia> {
    return List(6) { i ->
        SharedMedia(
            id = i.toString(),
            type = SharedMedia.MediaType.PHOTO,
            localUri = null,
            remoteUrl = "https://picsum.photos/600/80$i",
            thumbnailUrl = null,
            subLocalUri = null,
            subRemoteUrl = "https://picsum.photos/300/40$i",
            subThumbnailUrl = null,
            cameraFacing = "DUAL",
            caption = "프리뷰",
            dateTaken = System.currentTimeMillis(),
            orientation = 0,
            uploaderName = "엄마",
            syncStatus = SharedMedia.SyncStatus.SYNCED
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun MediaGridPreview() {
    LMTheme {
        MediaGridScreen(
            title = "추억 모음",
            medias = fakeMediaList(),
            onBackClick = {},
            onMediaClick = {},
        )
    }
}

