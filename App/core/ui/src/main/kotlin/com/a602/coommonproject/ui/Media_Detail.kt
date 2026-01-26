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
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color1
import androidx.compose.foundation.clickable

// 화면에 필요한 모든 상태를 담은 객체
data class MediaDetailUiState(
    val title: String,
    val isVideo: Boolean,
    val isPlaying: Boolean,
    val rearImage: ImageBitmap,
    val frontImage: ImageBitmap,
    val meta: PolaroidMeta,
)

// 사용자가 화면에서 할 수 있는 행동
sealed interface MediaDetailAction {
    data object Back : MediaDetailAction
    data object TogglePlayPause : MediaDetailAction
    data object Delete : MediaDetailAction
    data object Download : MediaDetailAction
    data object Edit : MediaDetailAction
}

// 상세화면 조립
@Composable
fun MediaDetailScreen(
    // 화면이 어떤 모습인지
    uiState: MediaDetailUiState,
    // 사용자가 무슨 행동을 했는지 밖에 알리는 것
    onAction: (MediaDetailAction) -> Unit,
    // 다른 화면이나 Preview에서 재사용 가능하게
    modifier: Modifier = Modifier,
) {
    // 화면 골격
    // TopAppBar - Content - BottomBar
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = background,
        topBar = {
            LMTopAppBar(
                title = uiState.title,
                onNavigationClick = { onAction(MediaDetailAction.Back) },
                // 필요하면 우측 액션 아이콘 추가해서 사용:
                // actionIcon = LMicons.Mypage,
                // onActionClick = { ... }
            )
        },
        bottomBar = {
            // Action_Bar는 항상 하단 고정 (Scaffold bottomBar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                IconActionBar(
                    onDelete = { onAction(MediaDetailAction.Delete) },
                    onDownload = { onAction(MediaDetailAction.Download) },
                    onEdit = { onAction(MediaDetailAction.Edit) },
                    modifier = Modifier.widthIn(min = 300.dp)
                )
            }
        }
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
                        rearImage = uiState.rearImage,
                        frontImage = uiState.frontImage,
                        meta = uiState.meta,
                        modifier = Modifier.fillMaxWidth(),
                        photoOverlay = {
                            // 영상일 때만 Polaroid 위에 재생/일시정지 오버레이
                            if (uiState.isVideo) {
                                PolaroidVideoControlsLayer(
                                    isPlaying = uiState.isPlaying,
                                    onTogglePlayPause = {
                                        onAction(MediaDetailAction.TogglePlayPause)
                                    },
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }

                    )
                }
            }
        }
    }
}

// ✅ 재생 중이면 버튼 숨김 + 화면 터치로 멈춤
@Composable
fun PolaroidVideoControlsLayer(
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {

        // 1) 재생 중일 때: "화면 전체"를 탭하면 멈추게 하는 투명 레이어
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onTogglePlayPause) // ✅ 영상 화면 터치 -> 멈춤
            )
        }

        // 2) 멈춘 상태일 때: 중앙 재생 버튼 노출 (노란색)
        if (!isPlaying) {
            VideoPlayButton(
                onClick = onTogglePlayPause,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun VideoPlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .background(color1) // ✅ 너희 노란색
            .padding(6.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "play",
            tint = Color.White
        )
    }
}

/* -------------------- Preview helpers -------------------- */

private fun previewBitmap(
    width: Int = 1080,
    height: Int = 1440,
    color: Int
): ImageBitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bmp.eraseColor(color)
    return bmp.asImageBitmap()
}

/* -------------------- Previews -------------------- */

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun MediaDetailScreenPreview_Photo() {
    MaterialTheme {
        val rear = previewBitmap(color = Color(0xFF1B1B1F).toArgb())
        val front = previewBitmap(width = 600, height = 600, color = Color(0xFF9BB7D4).toArgb())

        Surface(color = background) {
            MediaDetailScreen(
                uiState = MediaDetailUiState(
                    title = "자세히 보기",
                    isVideo = false,
                    isPlaying = false,
                    rearImage = rear,
                    frontImage = front,
                    meta = PolaroidMeta(
                        date = "2026.01.02",
                        role = "엄마",
                        comment = "오늘은 잘 웃어줬다"
                    )
                ),
                onAction = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun MediaDetailScreenPreview_Video() {
    MaterialTheme {
        val rear = previewBitmap(color = Color(0xFF1B1B1F).toArgb())
        val front = previewBitmap(width = 600, height = 600, color = Color(0xFF9BB7D4).toArgb())

        Surface(color = background) {
            MediaDetailScreen(
                uiState = MediaDetailUiState(
                    title = "자세히 보기",
                    isVideo = true,
                    isPlaying = false,
                    rearImage = rear,
                    frontImage = front,
                    meta = PolaroidMeta(
                        date = "2026.01.02",
                        role = "아빠",
                        comment = "첫 걸음마 영상!"
                    )
                ),
                onAction = {}
            )
        }
    }
}
