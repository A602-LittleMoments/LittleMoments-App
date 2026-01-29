
package com.a602.commonproject.feature.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.designsystem.component.FillWrapButton
import com.a602.commonproject.designsystem.theme.NiaTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.model.data.SharedMedia
import com.a602.coommonproject.ui.GalleryGridPolaroid



data object GalleryNavKey : NavKey


// 격자 보기
@Composable
fun GridGallery(
    medias : List<SharedMedia>,
    onCalendarClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit,
    modifier: Modifier = Modifier
) {

        Column(modifier = modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 16.dp)

        ){
            Spacer(modifier = modifier.height(56.dp))

            // 1. 상단 헤더 영역
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 10.dp,),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.bodyMedium
                )

                FillWrapButton(
                    text = "캘린더 보기",
                    onClick = onCalendarClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                GalleryGridPolaroid(
                    medias = medias ,
                    onClick = onMediaClick
                )
            }
        }
}


@Composable
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

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun GridGalleryPreview() {
    NiaTheme {
        GridGallery(
            medias  = fakeMediaList(),
            onCalendarClick = {},
            onMediaClick = {}
        )
    }
}
