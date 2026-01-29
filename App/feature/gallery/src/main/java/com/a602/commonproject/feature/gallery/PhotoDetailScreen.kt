package com.a602.commonproject.feature.gallery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.theme.NiaTheme
import com.a602.coommonproject.ui.MediaDetailScreen
import com.a602.commonproject.model.data.SharedMedia


@Composable
fun PhotoDetail(
    media: SharedMedia,
    modifier: Modifier = Modifier,
) {
    MediaDetailScreen(
        media = media,
        modifier = modifier
    )
}
@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 760
)
@Composable
private fun PhotoDetailPreview() {

    val fakeMedia = SharedMedia(
        id = "1",
        type = SharedMedia.MediaType.PHOTO,
        localUri = null,
        remoteUrl = "https://picsum.photos/600/800",
        thumbnailUrl = null,
        subLocalUri = null,
        subRemoteUrl = null,
        subThumbnailUrl = "https://picsum.photos/150/150",
        cameraFacing = "DUAL",
        caption = "vmvmvmvmmvmvmflvm",
        dateTaken = System.currentTimeMillis(),
        orientation = 0,
        uploaderName = "엄마",
        syncStatus = SharedMedia.SyncStatus.SYNCED
    )

    NiaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PhotoDetail(media = fakeMedia)
        }
    }
}
