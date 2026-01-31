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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.ConfirmDeleteDialog
import com.a602.commonproject.designsystem.component.IconActionBar
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.feature.gallery.viewmodel.MediaDetailViewModel
import com.a602.commonproject.model.data.SharedMedia

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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> Text("불러오는 중…")
                uiState.media != null -> {
                    MediaDetailScreen(
                        title = "상세보기",
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
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LMTopAppBar(
                title = title,
                onNavigationClick = onBack,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Polaroid(
                media = media,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            IconActionBar(
                onDelete = { showDeleteDialog = true },
                onDownload = onDownload,
                onEdit = onEdit,
            )
        }
    }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
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

