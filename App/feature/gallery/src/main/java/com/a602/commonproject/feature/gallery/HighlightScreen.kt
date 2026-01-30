//package com.a602.commonproject.feature.gallery
//
//import android.net.Uri
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.media3.common.MediaItem
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.ui.PlayerView
//import coil.compose.AsyncImage
//import com.a602.commonproject.database.model.SlideshowEntity
//import com.a602.commonproject.designsystem.theme.background
//
//@Composable
//fun HighlightResultScreen(
//    slideshow: SlideshowEntity,
//    modifier: Modifier = Modifier,
//    topBarSpacerHeight: Dp = 56.dp,
//    bottomBarSpacerHeight: Dp = 80.dp,
//) {
//    val context = LocalContext.current
//
//    // 재생할 소스 결정(로컬 우선)
//    val videoUri: Uri? = remember(slideshow.localVideoPath, slideshow.remoteVideoUrl) {
//        slideshow.localVideoPath?.let { Uri.parse(it) }
//            ?: slideshow.remoteVideoUrl?.let { Uri.parse(it) }
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .background(background)
//            .padding(horizontal = 16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Spacer(Modifier.height(topBarSpacerHeight))
//
//        // 가운데 영역
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                // 제목
//                Text(
//                    text = slideshow.title,
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(Modifier.height(12.dp))
//
//                Surface(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .aspectRatio(26f / 9f) //
//                        .clip(RoundedCornerShape(20.dp)),
//                    tonalElevation = 2.dp
//                ) {
//                    if (videoUri != null && slideshow.status in listOf("COMPLETED", "DOWNLOADED")) {
//                        VideoPlayer(
//                            uri = videoUri,
//                            modifier = Modifier.fillMaxSize()
//                        )
//                    } else {
//
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .background(MaterialTheme.colorScheme.surfaceVariant),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            if (!slideshow.thumbnailUrl.isNullOrBlank()) {
//                                AsyncImage(
//                                    model = slideshow.thumbnailUrl,
//                                    contentDescription = null,
//                                    modifier = Modifier.fillMaxSize()
//                                )
//                            }
//                            Column(
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                modifier = Modifier.padding(16.dp)
//                            ) {
//                                Text(
//                                    text = when (slideshow.status) {
//                                        "QUEUED" -> "대기 중이에요"
//                                        "PROCESSING" -> "하이라이트 생성 중..."
//                                        else -> "영상을 불러올 수 없어요"
//                                    },
//                                    style = MaterialTheme.typography.titleMedium,
//                                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//                                Spacer(Modifier.height(6.dp))
//                                Text(
//                                    text = "잠시만 기다려주세요",
//                                    style = MaterialTheme.typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//                            }
//                        }
//                    }
//                }
//
//                Spacer(Modifier.height(14.dp))
//
//                // 메타 정보(가볍게)
//                Text(
//                    text = "사진 ${slideshow.mediaCount}장 · ${slideshow.duration}초",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        }
//
//        Spacer(Modifier.height(bottomBarSpacerHeight))
//    }
//}
//
///**
// * ✅ Media3 ExoPlayer 기반 플레이어
// * - 컴포즈에서 생명주기 맞춰 release 처리
// */
//@Composable
//private fun VideoPlayer(
//    uri: Uri,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//
//    val player = remember(key1 = uri) {
//        ExoPlayer.Builder(context).build().apply {
//            setMediaItem(MediaItem.fromUri(uri))
//            prepare()
//            playWhenReady = true
//        }
//    }
//
//    DisposableEffect(player) {
//        onDispose { player.release() }
//    }
//
//    AndroidView(
//        modifier = modifier,
//        factory = { ctx ->
//            PlayerView(ctx).apply {
//                this.player = player
//                useController = true
//            }
//        },
//        update = { it.player = player }
//    )
//}
//
//@Preview(showBackground = true, widthDp = 360, heightDp = 760)
//@Composable
//private fun HighlightResultScreenPreview() {
//    val fake = SlideshowEntity(
//        slideshowId = "s1",
//        localVideoPath = null,
//        remoteVideoUrl = null, // 프리뷰에서는 null로 두고 썸네일/상태만 보이게
//        thumbnailUrl = "https://picsum.photos/600/900",
//        mediaCount = 24,
//        title = "2026 겨울 하이라이트",
//        duration = 18,
//        createAt = System.currentTimeMillis(),
//        status = "PROCESSING"
//    )
//
//    MaterialTheme {
//        HighlightResultScreen(slideshow = fake)
//    }
//}
