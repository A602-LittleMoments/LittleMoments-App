
package com.a602.commonproject.feature.album

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.width
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.feature.album.viewmodel.HighlightResultViewModel
import com.a602.commonproject.model.data.Slideshow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.R



@Composable
fun HighlightResultRoute(
    slideshowId: String,
    onBack: () -> Unit,
    viewModel: HighlightResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(slideshowId) {
        viewModel.observeSlideshow(slideshowId)
        viewModel.refreshSlideshow(slideshowId)
    }



    HighlightResultScreen(
        slideshow = uiState.slideshow,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        isDownloading = uiState.isDownloading,
        downloadProgress = uiState.downloadProgress,
        snackbarHostState = snackbarHostState, // [Fix] Pass host state
        onBack = onBack,
        onDownload = {
            viewModel.downloadSlideshow(
                slideshowId = slideshowId,
                onSuccess = {
                    scope.launch {
                        snackbarHostState.showSnackbar("다운로드 완료!")
                    }
                },
                onFailure = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        },
        onDelete = {
            viewModel.deleteSlideshow(
                slideshowId = slideshowId,
                onSuccess = {
                    scope.launch {
                        snackbarHostState.showSnackbar("삭제 완료")
                    }
                    onBack()
                },
                onFailure = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HighlightResultScreen(
    slideshow: Slideshow?,
    isLoading: Boolean,
    errorMessage: String?,
    isDownloading: Boolean = false,
    downloadProgress: Float = 0f,
    snackbarHostState: SnackbarHostState? = null, // [Fix] Added parameter
    onBack: () -> Unit = {},
    onDownload: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 재생 가능한 URL 결정: 로컬 우선, 없으면 리모트
    val playableUrl: String? = remember(slideshow?.localVideoPath, slideshow?.remoteVideoUrl) {
        slideshow?.localVideoPath ?: slideshow?.remoteVideoUrl
    }
    val videoUri: Uri? = remember(playableUrl) { playableUrl?.let(Uri::parse) }

    // 다운로드 가능 여부 (완료됐고 아직 로컬에 없을 때)
    val canDownload = remember(slideshow?.status, slideshow?.localVideoPath) {
        slideshow?.status == Slideshow.MakeStatus.COMPLETED &&
            slideshow.localVideoPath.isNullOrBlank()
    }

    // [Fix] Refactor to Scaffold
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent, // Ensure background shows through
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0), // Edge-to-Edge
        topBar = {
            // [Fix] TopAppBar: Remove Download button, static title
            LMTopAppBar(
                title = "하이라이트", // [Fix] Changed from "추억 하이라이트"
                navigationIcon = LMicons.Back,
                onNavigationClick = onBack,
                actionIcon = LMicons.Delete, // Trash icon
                onActionClick = { showDeleteDialog = true }
            )
        },
        snackbarHost = {
            if (snackbarHostState != null) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 100.dp) // [Fix] Raised position
                )
            }
        }
    ) {  paddingValues ->
        // Use paddingValues where appropriate, OR ignore if immersive content
        // Since we want the background image to be FULL SCREEN (behind topbar), we put it in a Box
        // and add statusBarsPadding to the content column instead.
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.gallery_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Placeholder for TopBar height to avoid overlap
                Spacer(Modifier.height(56.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // [Fix] Adjusted top spacing to be closer to TopBar
                    Spacer(Modifier.height(24.dp))

                    // [Fix] Title style updated: Smaller (headlineSmall) but Bold
                    // [Fix] The user said "Video upper part title".
                    Text(
                        text = "추억 하이라이트",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            // [Fix] displaySmall -> headlineSmall
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 30.sp,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(4f, 4f),
                                blurRadius = 7f
                            )
                        ),
                    )

                    // [Fix] Adjusted spacing
                    Spacer(Modifier.height(24.dp))

                    // 영상 플레이어 / 썸네일
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f) // [Fix] Changed to 3:4 (Portrait Photo) ratio
                            .clip(RoundedCornerShape(20.dp)),
                        tonalElevation = 2.dp,
                        shadowElevation = 4.dp
                    ) {
                        when {
                            isLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            // 영상 재생 가능
                            videoUri != null -> {
                                VideoPlayer(uri = videoUri, modifier = Modifier.fillMaxSize())
                            }
                            // 에러 또는 영상 없음
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // 썸네일 표시
                                    if (!slideshow?.thumbnailUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = slideshow?.thumbnailUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    // 에러 메시지
                                    if (errorMessage != null) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .background(
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .padding(24.dp)
                                        ) {
                                            Text(
                                                text = errorMessage,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // [Fix] Increased spacing -> Reduced
                    Spacer(Modifier.height(24.dp))

                    // [Fix] Source Info (Keyword or Date) only. Removed MetaRow.
                    // Assuming slideshow.title contains the source info (e.g., "Smile" or "2023.10.01~").
                    if (slideshow != null) {
                        SourceInfoChip(slideshow.title)
                    }

                    // [Fix] Increased spacing -> Reduced
                    Spacer(Modifier.height(24.dp))

                    // [Fix] Download Button moved here (below Source Info)
                    if (slideshow != null && canDownload && !isDownloading) {
                        Button(
                            onClick = onDownload,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("영상 저장하기", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    // 다운로드 진행률 표시
                    if (isDownloading) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "저장 중... ${(downloadProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }

        if (showDeleteDialog) {
            Dialog(onDismissRequest = { showDeleteDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = lightbackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "하이라이트 삭제",
                            style = MaterialTheme.typography.headlineSmall,
                            color = color3
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "정말 삭제하시겠습니까?\n삭제된 영상은 복구할 수 없습니다.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = color4,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("취소", color = color4)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showDeleteDialog = false
                                    onDelete()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = main
                                )
                            ) {
                                Text("삭제", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// [Fix] New Component for Source Info
@Composable
private fun SourceInfoChip(sourceText: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Text(
            text = sourceText,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
        )
    }
}

// [Fix] Removed SlideshowMetaRow and InfoChip as requested
// [Fix] Removed koreanDateFormatter unused

@Composable
private fun VideoPlayer(uri: Uri, modifier: Modifier = Modifier) {
    // [Fix] 프리뷰 모드인지 확인
    if (androidx.compose.ui.platform.LocalInspectionMode.current) {
        Box(
            modifier = modifier.background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.gallery_background), // 아무 이미지나
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.5f)
            )
            Text("Video Player (Preview)", color = Color.White)
        }
        return
    }

    val context = LocalContext.current

    val player = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        update = { it.player = player }
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, showSystemUi = true)
@Composable
private fun Preview_HighlightResult_Completed() {
    val fake = Slideshow(
        id = "temp",
        title = "추억 하이라이트",
        thumbnailUrl = "https://picsum.photos/600/900",
        remoteVideoUrl = "https://example.com/video.mp4",
        status = Slideshow.MakeStatus.COMPLETED,
        mediaCount = 24,
        durationSec = 18L,
        localVideoPath = null,
        createdAt = System.currentTimeMillis()
    )
    LMTheme {
        HighlightResultScreen(
            slideshow = fake,
            isLoading = false,
            errorMessage = null
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, showSystemUi = true)
@Composable
private fun Preview_HighlightResult_Loading() {
    LMTheme {
        HighlightResultScreen(
            slideshow = null,
            isLoading = true,
            errorMessage = null
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, showSystemUi = true)
@Composable
private fun Preview_HighlightResult_Downloading() {
    val fake = Slideshow(
        id = "temp",
        title = "추억 하이라이트",
        thumbnailUrl = "https://picsum.photos/600/900",
        remoteVideoUrl = "https://example.com/video.mp4",
        status = Slideshow.MakeStatus.COMPLETED,
        mediaCount = 24,
        durationSec = 18L,
        localVideoPath = null,
        createdAt = System.currentTimeMillis()
    )
    LMTheme {
        HighlightResultScreen(
            slideshow = fake,
            isLoading = false,
            errorMessage = null,
            isDownloading = true,
            downloadProgress = 0.45f
        )
    }
}
