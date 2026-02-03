
package com.a602.commonproject.feature.gallery

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.feature.gallery.viewmodel.HighlightResultViewModel
import com.a602.commonproject.model.data.Slideshow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.a602.commonproject.designsystem.R

private val koreanDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREA)

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
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = background
    ) { innerPadding ->
        HighlightResultScreen(
            slideshow = uiState.slideshow,
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
fun HighlightResultScreen(
    slideshow: Slideshow?,
    isLoading: Boolean,
    errorMessage: String?,
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

    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.gallery_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        LMTopAppBar(
            title = "하이라이트",
            navigationIcon = LMicons.Back,
            onNavigationClick = onBack,
            actionIcon = if (canDownload) LMicons.Download else null,
            actionIconContentDescription = "저장",
            onActionClick = { if (canDownload) onDownload() }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // 제목
            Text(
                text = slideshow?.title ?: "하이라이트 영상",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            // 영상 플레이어 / 썸네일
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
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

            Spacer(Modifier.height(20.dp))

            // 메타 정보 (사진 수, 길이, 날짜)
            if (slideshow != null) {
                SlideshowMetaRow(slideshow)
            }

            Spacer(Modifier.height(24.dp))

            // 삭제 버튼
            if (slideshow != null) {
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("삭제")
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

    // 삭제 확인 다이얼로그
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("하이라이트 삭제") },
            text = { Text("이 하이라이트 영상을 삭제하시겠어요?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}

@Composable
private fun SlideshowMetaRow(slideshow: Slideshow) {
    val createdDateText = remember(slideshow.createdAt) {
        Instant.ofEpochMilli(slideshow.createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(koreanDateFormatter)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        InfoChip("사진", "${slideshow.mediaCount}장")
        InfoChip("길이", "${slideshow.durationSec}초")
        InfoChip("날짜", createdDateText)
    }
}

@Composable
private fun InfoChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun VideoPlayer(uri: Uri, modifier: Modifier = Modifier) {
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
            }
        },
        update = { it.player = player }
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun Preview_HighlightResult_Completed() {
    val fake = Slideshow(
        id = "temp",
        title = "추억 하이라이트",
        thumbnailUrl = "https://picsum.photos/600/900",
        remoteVideoUrl = "https://example.com/video.mp4",
        status = Slideshow.MakeStatus.COMPLETED,
        mediaCount = 24,
        durationSec = 18,
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
