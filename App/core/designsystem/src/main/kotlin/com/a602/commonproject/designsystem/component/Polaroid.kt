import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.model.data.SharedMedia
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 프리뷰용 import
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight


@Composable
fun Polaroid(
    media: SharedMedia,
    modifier: Modifier = Modifier,
    useThumbnail: Boolean = false, // 🚀 Grid 모드 최적화 플래그
) {
    // URL 결정 로직
    val rearUrl = if (useThumbnail) {
        // 썸네일 우선 (없으면 원본)
        media.thumbnailUrl ?: media.remoteUrl ?: media.localUri
    } else {
        // 원본 우선 (없으면 썸네일)
        media.remoteUrl ?: media.localUri ?: media.thumbnailUrl
    }

    val frontUrl = if (useThumbnail) {
        media.subThumbnailUrl ?: media.subRemoteUrl ?: media.subLocalUri
    } else {
        media.subRemoteUrl ?: media.subLocalUri ?: media.subThumbnailUrl
    }

    // 날짜 포맷
    val dateText = remember(media.dateTaken) {
        SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
            .format(Date(media.dateTaken))
    }

    // 캡션
    val caption = media.caption

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = lightbackground,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            // 사진 영역 (DUAL 고정)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .background(Color.Black)
            ) {
                // rear (큰 사진)
                // Grid 모드거나 로컬 파일이면 Progressive Loading 불필요 -> 하나만 로드
                if (useThumbnail || media.localUri != null) {
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                            .data(rearUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Detail 모드 & 원본 로딩 필요 -> Progressive Loading (썸네일 깔고 원본 덮기)
                    if (media.thumbnailUrl != null) {
                         AsyncImage(
                            model = media.thumbnailUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                            .data(rearUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // front (작은 사진)
                val frontModifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .fillMaxWidth(0.4f)
                    .aspectRatio(3f / 4f)

                if (useThumbnail || media.subLocalUri != null) {
                     AsyncImage(
                        model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                            .data(frontUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = frontModifier,
                        contentScale = ContentScale.Crop
                    )
                } else {
                     if (media.subThumbnailUrl != null) {
                        AsyncImage(
                            model = media.subThumbnailUrl,
                            contentDescription = null,
                            modifier = frontModifier,
                            contentScale = ContentScale.Crop
                        )
                    }
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                            .data(frontUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = frontModifier,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // 메타 정보
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (!media.uploaderName.isNullOrBlank()) {
                    Text(
                        text = "by ${media.uploaderName}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (!caption.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = caption,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = color3
                )
            }
        }
    }
}


// 여기서부터는 프리뷰 코드

// 앞뒤 둘다 검정이라 사진 구분 안돼서
// 그 밑에 비트맵으로 색 다르게 한 거 볼 수 있음
@Preview(showBackground = true)
@Composable
fun PolaroidPreview_DualPhoto() {
    val fakeMedia = SharedMedia(
        id = "1",
        type = SharedMedia.MediaType.PHOTO,
        localUri = null,
        remoteUrl = "https://picsum.photos/600/800",
        thumbnailUrl = null,
        subLocalUri = null,
        subRemoteUrl = "https://picsum.photos/300/400",
        subThumbnailUrl = null,
        cameraFacing = "DUAL",
        caption = "DUAL 사진 폴라로이드 테스트 📸",
        dateTaken = System.currentTimeMillis(),
        orientation = 0,
        uploaderName = "엄마",
        syncStatus = SharedMedia.SyncStatus.SYNCED
    )
    LMTheme {
        Polaroid(
            media = fakeMedia,
            modifier = Modifier
                .padding(16.dp)
                .width(320.dp)
        )
    }
}


private fun previewBitmap(
    width: Int = 600,
    height: Int = 800,
    color: Int
): ImageBitmap {
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
        eraseColor(color)
    }.asImageBitmap()
}

@Composable
private fun PolaroidPreviewOnly(
    rear: ImageBitmap,
    front: ImageBitmap,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = lightbackground,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            // 📸 사진 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .background(Color.Black)
            ) {
                Image(
                    bitmap = rear,
                    contentDescription = "rear",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Image(
                    bitmap = front,
                    contentDescription = "front",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .fillMaxWidth(0.4f)
                        .aspectRatio(3f / 4f),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(12.dp))

            // 📅 날짜 / by
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "2026.01.29",
                    style = MaterialTheme.typography.bodySmall,
                    color = color3 // 🔥 headlineMedium 색상 override 확인용
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "by 엄마",
                    style = MaterialTheme.typography.bodySmall,
                    color = color3
                )
            }

            Spacer(Modifier.height(6.dp))

            // 📝 caption
            Text(
                text = "프리뷰용 캡션입니다. 폰트/컬러/정렬 확인!",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PolaroidPreview_ColorTest() {
    LMTheme {
        val rear = previewBitmap(color = android.graphics.Color.DKGRAY)
        val front = previewBitmap(
            width = 300,
            height = 400,
            color = android.graphics.Color.LTGRAY
        )

        PolaroidPreviewOnly(
            rear = rear,
            front = front,
            modifier = Modifier
                .padding(16.dp)
                .width(320.dp)
        )
    }
}
