package com.a602.commonproject.feature.album

import Polaroid
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Scaffold
import androidx.compose.ui.draw.clip
import com.a602.commonproject.designsystem.R
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.a602.commonproject.designsystem.icon.LMicons
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> Text("불러오는 중…")
            uiState.allMedias.isNotEmpty() -> {
                val initialIndex = remember(uiState.allMedias, mediaId) {
                    val idx = uiState.allMedias.indexOfFirst { it.id == mediaId }
                    if (idx == -1) 0 else idx
                }

                MediaDetailPagerScreen(
                    medias = uiState.allMedias,
                    initialIndex = initialIndex,
                    onBack = onBack,
                    onPageChanged = { index ->
                        viewModel.setMediaId(uiState.allMedias[index].id)
                    },
                    onDelete = viewModel::deleteCurrent,
                    onDownload = viewModel::downloadCurrent,
                    onEdit = onEdit,
                    isDownloading = uiState.isDownloading,
                    isDeleting = uiState.isDeleting,
                    snackbarHostState = snackbarHostState
                )
            }
            else -> Text("사진을 불러오지 못했어요")
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
    }
}




@Composable
fun MediaDetailPagerScreen(
    medias: List<SharedMedia>,
    initialIndex: Int,
    onBack: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    onEdit: () -> Unit,
    isDownloading: Boolean = false,
    isDeleting: Boolean = false,
    snackbarHostState: SnackbarHostState
) {
    // SharedMediaDetailScreen Code INLINED here per user request to reuse Album directly without intermediate shared file.
    var showDeleteDialog by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(initialPage = initialIndex, pageCount = { medias.size })

    // START INLINED CODE
    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            val date = Instant.ofEpochMilli(medias[pagerState.currentPage].dateTaken)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            LMTopAppBar(
                title = "${date.monthValue}월 ${date.dayOfMonth}일",
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
        Box(modifier = Modifier.fillMaxSize()) {
            // Background (Fixed)
            Image(
                painter = painterResource(id = R.drawable.gallery_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                val media = medias.getOrNull(page) ?: return@HorizontalPager
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(Modifier.height(100.dp))
                    MediaDetailContent(media = media)
                    Spacer(Modifier.height(100.dp)) // Action bar space
                }
            }

            // Action Bar (Fixed at bottom)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            ) {
                IconActionBar(
                    onDelete = { showDeleteDialog = true },
                    onDownload = onDownload,
                    onEdit = onEdit,
                    enabledDelete = !isDeleting && !isDownloading,
                    enabledDownload = !isDownloading && !isDeleting,
                    enabledEdit = !isDeleting && !isDownloading
                )
            }
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

@Composable
fun MediaDetailContent(
    media: SharedMedia,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(horizontal = 40.dp)
    ) {
        // 1. Polaroid Frame
        Column(
            modifier = Modifier
                .width(300.dp)
                .shadow(12.dp, RoundedCornerShape(2.dp))
                .background(Color.White)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
        ) {
            // Main Photo Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = media.remoteUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Sub Image
                if (media.subRemoteUrl != null || media.subThumbnailUrl != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(80.dp)
                            .border(1.dp, Color.Black)
                            .background(Color.Gray)
                    ) {
                        AsyncImage(
                            model = media.subRemoteUrl ?: media.subThumbnailUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

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
            Spacer(Modifier.height(16.dp))

            // Text Area
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                val date = Instant.ofEpochMilli(media.dateTaken)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                Text(
                    text = "${date.year}.${String.format("%02d", date.monthValue)}.${String.format("%02d", date.dayOfMonth)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = media.caption ?: "코멘트가 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // Decorations (Stars)
        val purpleStarColor = Color(0xFFEDBDFF)
        val yellowStarColor = Color(0xFFFFF5BA)

        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = purpleStarColor,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-32).dp, y = (-32).dp)
                .size(80.dp)
                .graphicsLayer(rotationZ = -25f)
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
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = yellowStarColor,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-64).dp, y = (-24).dp)
                .size(40.dp)
                .graphicsLayer(rotationZ = 15f)
        )

        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = yellowStarColor,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (28).dp, y = (-28).dp)
                .size(72.dp)
                .graphicsLayer(rotationZ = 25f)
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

        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = yellowStarColor,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = (60).dp)
                .size(100.dp)
                .graphicsLayer(rotationZ = -15f)
        )
    }

                    // 4. Bottom Left - Big Yellow Star
                    Icon(
                        painter = painterResource(id = DesignR.drawable.star),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = (-20).dp, y = 20.dp)
                            .size(90.dp)
                            .rotate(-30f),
                        tint = Color(0xFFFFF59D) // Pastel Yellow
                    )

    Spacer(Modifier.height(24.dp))

    // 3. Three White Stars
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}

                    // 5. Bottom Center/Right - White Stars Row
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 10.dp, end = 40.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(3) {
                             Icon(
                                painter = painterResource(id = DesignR.drawable.star),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .rotate(10f * (it + 1)),
                                tint = Color.White
                            )
                        }
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
    // END INLINED CODE
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
@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "Detail - Pager")
@Composable
private fun Preview_Detail_Pager() {
    LMTheme {
        MediaDetailPagerScreen(
            medias = listOf(fakePhotoMedia()),
            initialIndex = 0,
            onBack = {},
            onPageChanged = {},
            onDelete = {},
            onDownload = {},
            onEdit = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
