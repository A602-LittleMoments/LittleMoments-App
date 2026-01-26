package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.color4


import androidx.compose.ui.graphics.toArgb
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview


data class PolaroidMeta(
    val date: String,          // "2026.01.02"
    val role: String,          // "엄마"
    val comment: String? = null
)

@Composable
fun Polaroid(
    rearImage: ImageBitmap,     // 후면 큰 사진
    frontImage: ImageBitmap,    // 전면 작은 사진
    meta: PolaroidMeta,
    modifier: Modifier = Modifier,
    photoOverlay: (@Composable BoxScope.() -> Unit)? = null,
) {
    // 폴라로이드 느낌: 흰 종이 + 살짝 둥근 모서리
    val paperShape = RoundedCornerShape(18.dp)
    val photoShape = RoundedCornerShape(10.dp)

    Surface(
        modifier = modifier,
        shape = paperShape,
        color = lightbackground,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp) // 종이 테두리 두께 느낌
                .fillMaxWidth()
        ) {
            // 1) 사진 영역 (후면 + 전면 PIP)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f) // 폴라로이드 사진 비율 느낌
                    .clip(photoShape)
                    .background(Color.Black)
            ) {
                // 후면 (큰 사진)
                Image(
                    bitmap = rearImage,
                    contentDescription = "rear",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // 전면 (우하단 작은 사진) - 하나의 사진처럼 붙여놓는 느낌
                Image(
                    bitmap = frontImage,
                    contentDescription = "front",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .size(94.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, lightbackground, RoundedCornerShape(12.dp)), // 종이 프레임처럼
                    contentScale = ContentScale.Crop
                )

                photoOverlay?.invoke(this)
            }

            Spacer(Modifier.height(12.dp))

            // 2) 하단 정보 영역 (1줄: 날짜/작성자, 2줄: 코멘트)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = meta.date,
                        style = MaterialTheme.typography.labelMedium,
                        color = color4
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "by ${meta.role}",
                        style = MaterialTheme.typography.labelMedium,
                        color = color4
                    )
                }

                if (!meta.comment.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = meta.comment!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = color3,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun previewBitmap(
    width: Int = 1080,
    height: Int = 1440,
    color: Int
): ImageBitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bmp.eraseColor(color)
    return bmp.asImageBitmap()
}

@Preview(showBackground = true)
@Composable
fun PolaroidPreview_WithComment() {
    MaterialTheme {
        val rear = previewBitmap(color = Color(0xFF1B1B1F).toArgb())   // 어두운 배경
        val front = previewBitmap(width = 600, height = 600, color = Color(0xFF9BB7D4).toArgb()) // 밝은 배경

        Polaroid(
            rearImage = rear,
            frontImage = front,
            meta = PolaroidMeta(
                date = "2026.01.02",
                role = "엄마",
                comment = "랄랄랄라W~~W~~~~~"
            ),
            modifier = Modifier
                .padding(16.dp)
                .width(320.dp)
        )
    }
}

// Uri → ImageBitmap 변환 코드
//fun imageBitmapFromUri(
//    context: Context,
//    uri: Uri
//): ImageBitmap {
//    val source = ImageDecoder.createSource(context.contentResolver, uri)
//    val bitmap = ImageDecoder.decodeBitmap(source)
//    return bitmap.asImageBitmap()
//}

// 화면에서 Polaroid 호출 예시
//@Composable
//fun PolaroidItem(
//    rearUri: Uri,
//    frontUri: Uri,
//    meta: PolaroidMeta
//) {
//    val context = LocalContext.current
//
//    val rearBitmap = remember(rearUri) {
//        imageBitmapFromUri(context, rearUri)
//    }
//    val frontBitmap = remember(frontUri) {
//        imageBitmapFromUri(context, frontUri)
//    }
//
//    Polaroid(
//        rearImage = rearBitmap,
//        frontImage = frontBitmap,
//        meta = meta
//    )
//}
