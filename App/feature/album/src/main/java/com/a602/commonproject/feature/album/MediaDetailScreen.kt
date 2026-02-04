package com.a602.commonproject.feature.album

import Polaroid
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.ConfirmDeleteDialog
import com.a602.commonproject.designsystem.component.IconActionBar
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.feature.album.viewmodel.MediaDetailViewModel
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.designsystem.R as DesignR
// import com.a602.coommonproject.ui.SharedMediaDetailScreen // REMOVED

@Composable
fun MediaDetailRoute(
    mediaId: String,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: MediaDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(mediaId) { viewModel.setMediaId(mediaId) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // 삭제 성공 → 뒤로가기
    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            viewModel.onDeleteSuccessConsumed()
            onDeleted()
        }
    }

    // 다운로드 성공 스낵바
    LaunchedEffect(uiState.downloadSuccess) {
        if (uiState.downloadSuccess) {
            viewModel.onDownloadSuccessConsumed()
            snackbarHostState.showSnackbar("사진을 저장했어요")
        }
    }

    // 에러 스낵바
    LaunchedEffect(uiState.errorMessage) {
        val msg = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearError()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> Text("불러오는 중…")
                uiState.media != null -> {
                    MediaDetailScreen(
                        title = "자세히 보기",
                        media = uiState.media!!,
                        onBack = onBack,
                        onDelete = viewModel::deleteCurrent,
                        onDownload = viewModel::downloadCurrent,
                        onEdit = onEdit,
                    )
                }
                else -> Text("사진을 불러오지 못했어요")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .systemBarsPadding()
        )
    }
}




@Composable
fun MediaDetailScreen(
    title: String,
    media: SharedMedia,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    onEdit: () -> Unit,
) {
    // SharedMediaDetailScreen Code INLINED here per user request to reuse Album directly without intermediate shared file.

    // START INLINED CODE
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
            // Background Image
            Image(
                painter = painterResource(id = DesignR.drawable.gallery_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 폴라로이드 + 꾸미기 요소
                Box(contentAlignment = Alignment.Center) {
                    Polaroid(
                        media = media,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Decorations

                    // 1. Top Left - Pastel Purple Star
                    Icon(
                        painter = painterResource(id = DesignR.drawable.star),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 20.dp)
                            .offset(x = (-10).dp)
                            .size(50.dp)
                            .rotate(-15f),
                        tint = Color(0xFFE1BEE7) // Pastel Purple
                    )

                    // 2. Top Right - Pastel Yellow Star
                    Icon(
                        painter = painterResource(id = DesignR.drawable.star),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 10.dp)
                            .offset(x = 15.dp)
                            .size(60.dp)
                            .rotate(20f),
                        tint = Color(0xFFFFF176) // Pastel Yellow
                    )

                    // 3. Top Right Small - Cream Star
                    Icon(
                        painter = painterResource(id = DesignR.drawable.star),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 40.dp, end = 50.dp)
                            .size(30.dp)
                            .rotate(-10f),
                        tint = Color(0xFFFFF9C4) // Cream
                    )


                    // 4. Bottom Left - Big Yellow Star
                    Icon(
                        painter = painterResource(id = DesignR.drawable.star),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = (-20).dp, y = (-70).dp) // Moved up to avoid caption
                            .size(70.dp)
                            .rotate(-30f),
                        tint = Color(0xFFFFF59D) // Pastel Yellow
                    )

                    // 5. Bottom Center/Right - White Stars Row
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 80.dp, end = 40.dp), // Moved up to avoid caption
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                    }


                }

                Spacer(Modifier.height(16.dp))

                // 액션바
                IconActionBar(
                    modifier = Modifier.fillMaxWidth(),
                    onDelete = onDelete,
                    onDownload = onDownload,
                    onEdit = onEdit,
                )
            }
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
@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "Detail - Content")
@Composable
private fun Preview_Detail_Content() {
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
