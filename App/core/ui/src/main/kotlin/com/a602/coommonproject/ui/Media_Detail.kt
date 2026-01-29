package com.a602.coommonproject.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.IconActionBar
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.component.Polaroid
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color1
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.Dp
import com.a602.commonproject.model.data.SharedMedia


@Composable
fun MediaDetailScreen(
    media: SharedMedia,
    modifier: Modifier = Modifier
    ) {
    // TopAppBar - Content - BottomBar
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = background,
    ) { innerPadding ->
        // Content, 본문 내용, 즉 폴라로이드 부분
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 폴라로이드가 남는 공간을 다 차지하고, 액션바는 bottomBar로 고정
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Polaroid(
                        media = media,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun MediaDetailScreenPreview() {
    val fakeMedia = SharedMedia(
        id = "1",
        type = SharedMedia.MediaType.PHOTO, // 사진 혹은 VIDEO
        localUri = null,
        remoteUrl = "https://picsum.photos/600/800", // 랜덤 이미지
        thumbnailUrl = null,
        subLocalUri = null,
        subRemoteUrl = null,
        subThumbnailUrl = null,
        cameraFacing = "DUAL",
        caption = "가짜 사진 테스트",
        dateTaken = System.currentTimeMillis(),
        orientation = 0,
        uploaderName = "엄마",
        syncStatus = SharedMedia.SyncStatus.SYNCED
    )

    MediaDetailScreen(media = fakeMedia)
}
