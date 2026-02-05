package com.a602.commonproject.feature.album

import Polaroid
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImagePainter.State.Empty.painter

// import com.a602.coommonproject.ui.SharedMediaDetailScreen // REMOVED



@Composable
fun MediaDetailRoute(
    mediaId: String,
    date: String? = null,
    keywordId: String? = null,
    babyId: String? = null,
    year: Int? = null,
    isTemp: Boolean = false,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit,
    viewModel: MediaDetailViewModel = hiltViewModel()
) {
    var currentId by rememberSaveable { mutableStateOf(mediaId) }

    LaunchedEffect(currentId, date, keywordId, babyId, year, isTemp) {
        viewModel.setMediaId(currentId, date, keywordId, babyId, year, isTemp)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    // 삭제 성공 → 스낵바 후 뒤로가기
    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar("삭제되었습니다")
            }
            // 스낵바가 보여질 시간을 확보 (너무 길지 않게)
            kotlinx.coroutines.delay(700)
            viewModel.onDeleteSuccessConsumed()
            onDeleted()
        }
    }

    // 다운로드(원본) 또는 캡처 성공 스낵바
    LaunchedEffect(uiState.downloadSuccess, uiState.saveBitmapSuccess) {
        if (uiState.downloadSuccess || uiState.saveBitmapSuccess) {
            viewModel.onDownloadSuccessConsumed()
            scope.launch {
                snackbarHostState.showSnackbar("사진을 저장했어요")
            }
        }
    }

    // 에러 스낵바
    LaunchedEffect(uiState.errorMessage) {
        val msg = uiState.errorMessage ?: return@LaunchedEffect
        scope.launch {
            snackbarHostState.showSnackbar(msg)
        }
        viewModel.clearError()
    }

    // ✨ [Fix] 화면이 다시 보일 때(코멘트 수정 후 복귀 등) 데이터 갱신
    val lifecycleOwner = LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                // 키워드 컬렉션 데이터 등은 일회성 Fetch이므로, 화면 복귀 시 갱신 필요
                viewModel.refreshData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                // 삭제 성공 시 에러 메시지 방지 (스낵바 유지하면서 대기)
                uiState.deleteSuccess -> {
                   // 빈 화면 유지
                }
                uiState.isLoading -> Text("불러오는 중…")
                uiState.media != null -> {
                    // [Navigation Fix] Use ALL medias, sorted by date descending (Newest first)
                    // Matches the Grid View order and allows swiping through the entire gallery.
                    val sortedMedias = remember(uiState.allMedias) {
                        uiState.allMedias.sortedByDescending { it.dateTaken }
                    }

                    val currentMedia = uiState.media!!
                    val initialIndex = remember(sortedMedias, currentMedia) {
                        val idx = sortedMedias.indexOfFirst { it.id == currentMedia.id }
                        if (idx == -1) 0 else idx
                    }

                    MediaDetailScreen(
                        title = "자세히 보기",
                        initialIndex = initialIndex,
                        medias = sortedMedias,
                        isTemp = isTemp, // Pass isTemp
                        onBack = onBack,
                        onDelete = viewModel::deleteMedia,
                        onDownload = viewModel::downloadMedia, // Legacy 원본 다운로드
                        onSaveBitmap = viewModel::saveBitmapToGallery, // ✨ 꾸며진 사진 저장
                        onEdit = onEdit,
                        onMediaIdChange = { currentId = it }
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
    initialIndex: Int,
    medias: List<SharedMedia>,
    isTemp: Boolean = false, // Added param
    onBack: () -> Unit,
    onDelete: (String) -> Unit,
    onDownload: (SharedMedia) -> Unit,
    onSaveBitmap: (Bitmap) -> Unit,
    onEdit: (String) -> Unit,
    onMediaIdChange: (String) -> Unit = {},
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { medias.size }
    )

    // Capture Trigger
    var captureTrigger by remember { mutableStateOf<Long?>(null) }

    // Delete Dialog State
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteTargetId by remember { mutableStateOf<String?>(null) }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                deleteTargetId?.let { onDelete(it) }
                showDeleteDialog = false
                deleteTargetId = null
            },
            onDismiss = {
                showDeleteDialog = false
                deleteTargetId = null
            }
        )
    }

    // Fix: Ensure pager reflects the correct initial page when data loads asynchronously
    LaunchedEffect(initialIndex, medias.size) {
        if (medias.isNotEmpty() && pagerState.currentPage != initialIndex) {
             pagerState.scrollToPage(initialIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        medias.getOrNull(pagerState.currentPage)?.let {
            onMediaIdChange(it.id)
        }
    }

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
            // Background Image (Static)
            Image(
                painter = painterResource(id = DesignR.drawable.gallery_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Content Area with Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { index -> medias.getOrNull(index)?.id ?: index }
            ) { page ->
                val media = medias.getOrNull(page)

                if (media != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 캡처를 위한 Graphics Layer
                        val graphicsLayer = rememberGraphicsLayer()

                        // 캡처 요청 발생 시 현재 페이지만 캡처
                        LaunchedEffect(captureTrigger) {
                            if (captureTrigger != null && pagerState.currentPage == page) {
                                val bitmap = graphicsLayer.toImageBitmap()
                                onSaveBitmap(bitmap.asAndroidBitmap())
                            }
                        }

                        // 폴라로이드 + 꾸미기 요소
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawWithContent {
                                    // 이 영역의 그림을 graphicsLayer에 기록합니다
                                    graphicsLayer.record {
                                        this@drawWithContent.drawContent()
                                    }
                                    // 실제 화면에도 그립니다
                                    drawLayer(graphicsLayer)
                                }
                        ) {
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
                                    .padding(top = 5.dp)
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
                                    .padding(top = 25.dp, end = 50.dp)
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
                                    .offset(x = (-20).dp, y = (-68).dp) // Moved up to avoid caption
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

                        // 액션바 (임시 앨범에서는 미표시)
                        if (!isTemp) {
                            IconActionBar(
                                modifier = Modifier.fillMaxWidth(),
                                onDelete = {
                                    deleteTargetId = media.id
                                    showDeleteDialog = true
                                },
                                onDownload = {
                                    // 캡처 트리거 실행 (현재 보고 있는 페이지만 캡처됨)
                                    captureTrigger = System.currentTimeMillis()
                                },
                                onEdit = { onEdit(media.id) },
                            )
                        }
                    }
                }
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
            medias = listOf(fakePhotoMedia()),
            initialIndex = 0,
            onBack = {},
            onDelete = {},
            onDownload = {},
            onSaveBitmap = {},
            onEdit = {}
        )
    }
}
