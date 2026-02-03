package com.a602.commonproject.feature.album
import Polaroid
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import java.time.Instant
import java.time.ZoneId
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
import com.a602.commonproject.feature.album.viewmodel.MediaDetailViewModel
import com.a602.commonproject.model.data.SharedMedia
import androidx.compose.foundation.layout.Row
import com.a602.commonproject.designsystem.R

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

    val backgroundImage = R.drawable.gallery_background

    Scaffold(
        topBar = {
            LMTopAppBar(
                title = title,
                onNavigationClick = onBack,
            )
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {
            // Background
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                // Main Content Wrapper (Centered)
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // 1. Polaroid Frame (The Anchor)
                    Column(
                        modifier = Modifier
                            .width(300.dp)
                            .shadow(12.dp, RoundedCornerShape(2.dp))
                            .background(Color.White)
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp) // Specific padding for Polaroid visual
                    ) {
                        // Main Photo Area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f) // Square
                                .background(Color.LightGray)
                        ) {
                            AsyncImage(
                                model = media.remoteUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Sub Image (Small inset at bottom right)
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

                        Spacer(Modifier.height(16.dp)) // Expands bottom of polaroid slightly
                    }

                    // 2. Decorations (Absolute positioning relative to the frame)
                    val purpleStarColor = Color(0xFFEDBDFF)
                    val yellowStarColor = Color(0xFFFFF5BA)

                    // Purple Star - Top Left
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

                     // Small Yellow Star - Top Right (Inner)
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

                    // Large Yellow Star - Top Right (Outer)
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

                    // Extra Large Yellow Star - Bottom Left
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = yellowStarColor,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = (-42).dp, y = (42).dp)
                            .size(110.dp)
                            .graphicsLayer(rotationZ = -15f)
                    )
                }

                // 3. Three White Stars (Below the frame)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 120.dp), // Positioned clearly below frame
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) {
                         Icon(
                            imageVector = Icons.Rounded.Star, // Using Rounded for softer look
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
            }

            // Action Bar (Bottom Overlay)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                 IconActionBar(
                    onDelete = { showDeleteDialog = true },
                    onDownload = onDownload,
                    onEdit = onEdit,
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

