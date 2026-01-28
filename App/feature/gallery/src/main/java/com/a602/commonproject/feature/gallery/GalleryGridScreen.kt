package com.a602.commonproject.feature.gallery

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.FillWrapButton
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.coommonproject.ui.GalleryGridPolaroid
import com.a602.coommonproject.ui.PolaroidData

// 격자 보기
@Composable
fun GridGallery(
    polaroids: List<PolaroidData>,
    modifier: Modifier = Modifier
) {

        Column(modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 16.dp)

        ){
            Spacer(modifier = Modifier.height(56.dp))

            // 1. 상단 헤더 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 10.dp,),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )

                FillWrapButton(
                    text = "캘린더 보기",
                    onClick= {/*이동 구현 해야함*/ },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                GalleryGridPolaroid(
                    polaroids = polaroids,
                    onClick = { clickedItem ->}
                )
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
private val samplePolaroids = List(9) { i ->
    PolaroidData(
        rearImage = previewBitmap(color = 0xFF1B1B1F.toInt() + i * 0x00101010),
        frontImage = previewBitmap(width = 200, height = 200, color = 0xFF9BB7D4.toInt() + i * 0x00080808),
        meta = PolaroidMeta(
            date = "2026.01.0${i + 1}",
            role = "엄마",
            comment =  "오늘 사진"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun GridGalleryPreview() {
    GridGallery(
        polaroids = samplePolaroids
    )
}
