package com.a602.commonproject.feature.gallery
import Polaroid
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.a602.commonproject.designsystem.component.IconActionBar
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.model.data.SharedMedia

@Composable
fun MediaDetailRoute(
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    onEdit: () -> Unit,
){
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val media = uiState.media ?: return@entry
    val title = "상세보기"
    MediaDetailScreen(
        title = title,
        media = media,
        onBack = onBack,
        onDelete = onDelete,
        onDownload = onDownload,
        onEdit = onEdit,
    )
}

@Composable
fun MediaDetailScreen(
    title: String,
    media: SharedMedia,
//    isPlaying: Boolean,  // 영상일 때 필요
    onBack: () -> Unit,
//    onTogglePlayPause: () -> Unit,   // 영상일 때 필요
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    onEdit: () -> Unit,
) {
    Scaffold(
        topBar = {
            LMTopAppBar(
                title = title,
                onNavigationClick = onBack,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 폴라로이드
                Polaroid(
                    media = media,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                // 액션바
                IconActionBar(
                    onDelete = onDelete,
                    onDownload = onDownload,
                    onEdit = onEdit,
                )
            }

            // 영상이면: 기존 재생 오버레이가 있으면 여기서 얹기
//            if (media.type == SharedMedia.MediaType.VIDEO) {
//                // 최소 구현: 화면 탭으로 토글
//                Box(
//                    modifier = Modifier
//                        .matchParentSize()
//                        .clickable { onTogglePlayPause },
//                )
            // 재생 버튼 UI가 따로 있으면 여기서 표시
//            }
        }
    }
}

// 프리뷰
private fun fakePhotoMedia(): SharedMedia {
    return SharedMedia(
        id = "m1",
        type = SharedMedia.MediaType.PHOTO,
        localUri = null,
        remoteUrl = "https://picsum.photos/seed/detail-rear/900/1200",
        thumbnailUrl = "https://picsum.photos/seed/detail-rear-thumb/450/600",
        subLocalUri = null,
        subRemoteUrl = "https://picsum.photos/seed/detail-front/360/480",
        subThumbnailUrl = "https://picsum.photos/seed/detail-front-thumb/180/240",
        cameraFacing = "DUAL",
        caption = "사진 디테일 프리뷰입니다 😊",
        dateTaken = System.currentTimeMillis(),
        orientation = 0,
        uploaderName = "엄마",
        syncStatus = SharedMedia.SyncStatus.SYNCED
    )
}


@Preview(
    showBackground = true,
    name = "Detail - Photo",
    device = "spec:width=411dp,height=891dp,dpi=440" // Pixel 4 XL
)
@Composable
private fun Preview_Detail_Photo_Pixel4XL() {
    LMTheme {
        MediaDetailScreen(
            title = "자세히 보기",
            media = fakePhotoMedia(),
            onBack = {},
            onDelete = {},
            onDownload = {},
            onEdit = {}
        )
    }
}
