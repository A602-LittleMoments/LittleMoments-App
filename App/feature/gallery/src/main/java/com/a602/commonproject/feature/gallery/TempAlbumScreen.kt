/*
package com.a602.commonproject.feature.gallery

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.ConfirmDeleteDialog
import com.a602.commonproject.designsystem.component.FillWrapButton
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.coommonproject.ui.GalleryGridPolaroid
import com.a602.coommonproject.ui.PolaroidData
import com.a602.coommonproject.ui.SelectableGalleryGrid

//임시 앨범
@Composable
fun TempGridGallery(
    polaroids: List<PolaroidData>,
    modifier: Modifier = Modifier,

    ) {
    var isSelectMode by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<Int>() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            // 상단 설명 탭
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(lightblue)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center

            ) {
                Text(
                    text = "임시 앨범의 사진은 매월 1일에 삭제 됩니다",
                    style = MaterialTheme.typography.bodyLarge,
                    color = color3,
                    textAlign = TextAlign.Center,

                    )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                // 1. 상단 버튼 위치
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 10.dp),
                ) {
                    FillWrapButton(
                        onClick = { */
/* 전체 비우기 로직 *//*
 },
                        text = "전체비우기",
                        modifier = Modifier.align(Alignment.CenterStart),
                    )

                    FillWrapButton(
                        onClick = {
                            isSelectMode = !isSelectMode
                            if (!isSelectMode) selectedItems.clear()
                        },
                        text = if (isSelectMode) "취소" else "선택",
                        modifier = Modifier.align(Alignment.CenterEnd),
                    )
                }

                // 2. 그리드 영역 (상태에 따라 스위칭)
                Box(modifier = Modifier.weight(1f)) {
                    if (isSelectMode) {
                        // 선택 모드일 때
                        SelectableGalleryGrid(
                            polaroids = polaroids,
                            isSelectMode = true,
                            selectedIds = selectedItems.toList(),
                            onItemClick = { item ->
                                val id = item.rearImage.hashCode()
                                if (selectedItems.contains(id)) selectedItems.remove(id)
                                else selectedItems.add(id)
                            },
                        )
                    } else {

                        GalleryGridPolaroid(
                            polaroids = polaroids,
                            onClick = { */
/* 상세 화면 이동 등 *//*
 },
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isSelectMode && selectedItems.isNotEmpty(),
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                ) {
                    Surface(
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth(),
                        color = lightbackground,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // 삭제 버튼
                            FooterActionItem(
                                icon = LMicons.Delete,
                                text = "삭제",
                                color = color3,
                                onClick = {
                                    showDeleteDialog = true
                                },
                            )

                            // 저장 버튼
                            FooterActionItem(
                                icon = LMicons.Download,
                                text = "저장",
                                color = color3,
                                onClick = { */
/* 저장 로직 *//*
 },
                            )
                        }
                    }
                }
                if (showDeleteDialog) {
                    ConfirmDeleteDialog(
                        onConfirm = {
                            selectedItems.clear()
                            showDeleteDialog = false
                            isSelectMode = false
                        },
                        onDismiss = {
                            showDeleteDialog = false
                        },
                    )
                }
            }
        }
    }
}
@Composable
fun FooterActionItem(
    icon: ImageVector,
    text: String,
    color: Color,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = text, tint = color)
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}


private fun previewBitmap(
    width: Int = 1080,
    height: Int = 1440,
    color: Int,
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
            comment = "오늘 사진",
        ),
    )
}

@Preview(showBackground = true, name = "임시 앨범 - 일반 상태")
@Composable
fun TempGridGalleryNormalPreview() {
    MaterialTheme {
            TempGridGallery(polaroids = samplePolaroids)
    }
}

@Preview(showBackground = true, name = "임시 앨범 - 선택 모드 활성화")
@Composable
fun TempGridGallerySelectModePreview() {
    MaterialTheme {
        Surface(color = Color(0xFFFDF7F2)) {
            TempGridGallery(polaroids = samplePolaroids)
        }
    }
}

*/
