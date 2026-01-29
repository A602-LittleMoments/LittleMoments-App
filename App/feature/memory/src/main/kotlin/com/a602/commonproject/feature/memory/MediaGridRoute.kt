/*
package com.a602.commonproject.feature.memory


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.coommonproject.ui.GalleryGridPolaroid
import com.a602.coommonproject.ui.PolaroidData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// URL -> ImageBitmap (Coil)
private suspend fun loadImageBitmap(
    imageLoader: ImageLoader,
    request: ImageRequest
): ImageBitmap? {
    val result = imageLoader.execute(request)
    val drawable = (result as? SuccessResult)?.drawable ?: return null
    val bmp = (drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap ?: return null
    return bmp.asImageBitmap()
}

@Composable
fun MediaGridRoute(
    title: String,
    medias: List<MediaItemUi>,
    onBackClick: () -> Unit,
    onMediaClick: (mediaId: String) -> Unit,
    // Preview/테스트용: 있으면 네트워크 로딩 스킵
    previewMapped: List<Pair<String, PolaroidData>>? = null,
) {
    // Preview에서는 바로 그리기
    if (previewMapped != null) {
        MediaGridScreen(
            mapped = previewMapped,
            title = title,
            onBackClick = onBackClick,
            onMediaClick = onMediaClick,
        )
        return
    }

    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }

    var mapped by remember(medias) {
        mutableStateOf<List<Pair<String, PolaroidData>>>(emptyList())
    }

    LaunchedEffect(medias) {
        // medias가 바뀌면 기존 결과 초기화 (원하면 유지해도 됨)
        mapped = emptyList()

        val result = coroutineScope {
            medias.map { m ->
                async {
                    val rearReq = ImageRequest.Builder(context)
                        .data(m.thumbUrl)
                        .allowHardware(false)
                        .build()

                    val frontReq = ImageRequest.Builder(context)
                        .data(m.subThumbUrl ?: m.thumbUrl)
                        .allowHardware(false)
                        .build()

                    val rear = loadImageBitmap(imageLoader, rearReq) ?: return@async null
                    val front = loadImageBitmap(imageLoader, frontReq) ?: rear

                    m.mediaId to PolaroidData(
                        rearImage = rear,
                        frontImage = front,
                        meta = PolaroidMeta(
                            date = m.takenAt.orEmpty(),
                            role = m.role.orEmpty(),
                            comment = m.caption.orEmpty()
                        )
                    )
                }
            }.awaitAll().filterNotNull()
        }

        mapped = result
    }

    MediaGridScreen(
        mapped = mapped,
        title = title,
        onBackClick = onBackClick,
        onMediaClick = onMediaClick,
    )
}
*/
