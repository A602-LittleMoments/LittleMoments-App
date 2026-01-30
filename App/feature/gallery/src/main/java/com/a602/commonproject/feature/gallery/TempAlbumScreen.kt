package com.a602.commonproject.feature.gallery

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.ConfirmDeleteDialog
import com.a602.commonproject.designsystem.component.FillWrapButton
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.model.data.SharedMedia

@Composable
fun TempGridGallery(
    medias: List<SharedMedia>,
    onMediaClick: (SharedMedia) -> Unit,
    onDeleteSelected: (List<String>) -> Unit,
    onSaveSelected: (List<String>) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSelectMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<String>() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                    text = "임시 앨범의 사진은 매월 30일에 삭제 됩니다",
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
                //
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 10.dp),
                ) {
                    FillWrapButton(
                        onClick = onClearAll,
                        text = "전체비우기",
                        modifier = Modifier.align(Alignment.CenterStart),
                    )

                    FillWrapButton(
                        onClick = {
                            isSelectMode = !isSelectMode
                            if (!isSelectMode) selectedIds.clear()
                        },
                        text = if (isSelectMode) "취소" else "선택",
                        modifier = Modifier.align(Alignment.CenterEnd),
                    )
                }

                // 그리드 영역
                Box(modifier = Modifier.weight(1f)) {
                    TempImageGrid(
                        medias = medias,
                        isSelectMode = isSelectMode,
                        selectedIds = selectedIds.toList(),
                        onClick = { media ->
                            if (isSelectMode) {
                                if (selectedIds.contains(media.id)) {
                                    selectedIds.remove(media.id)
                                } else {
                                    selectedIds.add(media.id)
                                }
                            } else {
                                onMediaClick(media)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 하단 액션바
                AnimatedVisibility(
                    visible = isSelectMode && selectedIds.isNotEmpty(),
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
                            FooterActionItem(
                                icon = LMicons.Delete,
                                text = "삭제",
                                color = color3,
                                onClick = { showDeleteDialog = true },
                            )

                            FooterActionItem(
                                icon = LMicons.Download,
                                text = "저장",
                                color = color3,
                                onClick = {
                                    onSaveSelected(selectedIds.toList())
                                    selectedIds.clear()
                                    isSelectMode = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                onDeleteSelected(selectedIds.toList())
                selectedIds.clear()
                showDeleteDialog = false
                isSelectMode = false
            },
            onDismiss = {
                showDeleteDialog = false
            },
        )
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

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "TempGrid - Normal")
@Composable
private fun TempGridGalleryPreview_Normal() {
    MaterialTheme {
        TempGridGallery(
            medias = fakeTempMedias(30),
            onMediaClick = {},
            onDeleteSelected = {},
            onSaveSelected = {},
            onClearAll = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "TempGrid - Select Mode (Footer Visible)")
@Composable
private fun TempGridGalleryPreview_SelectMode() {
    MaterialTheme {
        TempGridGalleryPreviewHarness(
            initialSelectMode = true,
            initialSelectedIds = listOf("1", "3", "7")
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "TempGrid - Delete Dialog")
@Composable
private fun TempGridGalleryPreview_DeleteDialog() {
    MaterialTheme {
        TempGridGalleryPreviewHarness(
            initialSelectMode = true,
            initialSelectedIds = listOf("1", "3"),
            initialShowDeleteDialog = true
        )
    }
}

/**
 * ✅ 프리뷰 전용 Harness
 * - TempGridGallery 내부 state를 밖으로 빼서 프리뷰에서 강제로 상태를 만들 수 있게 함
 */
@Composable
private fun TempGridGalleryPreviewHarness(
    initialSelectMode: Boolean,
    initialSelectedIds: List<String>,
    initialShowDeleteDialog: Boolean = false,
) {
    val medias = remember { fakeTempMedias(30) }

    var isSelectMode by remember { mutableStateOf(initialSelectMode) }
    val selectedIds = remember { mutableStateListOf<String>().apply { addAll(initialSelectedIds) } }
    var showDeleteDialog by remember { mutableStateOf(initialShowDeleteDialog) }

    // TempGridGallery 로직을 거의 그대로 복제(프리뷰에서만 사용)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(56.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(lightblue)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "임시 앨범의 사진은 매월 30일에 삭제 됩니다",
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 10.dp),
                ) {
                    FillWrapButton(
                        onClick = { },
                        text = "전체비우기",
                        modifier = Modifier.align(Alignment.CenterStart),
                    )

                    FillWrapButton(
                        onClick = {
                            isSelectMode = !isSelectMode
                            if (!isSelectMode) {
                                selectedIds.clear()
                                showDeleteDialog = false
                            }
                        },
                        text = if (isSelectMode) "취소" else "선택",
                        modifier = Modifier.align(Alignment.CenterEnd),
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    TempImageGrid(
                        medias = medias,
                        isSelectMode = isSelectMode,
                        selectedIds = selectedIds.toList(),
                        onClick = { media ->
                            if (isSelectMode) {
                                if (selectedIds.contains(media.id)) selectedIds.remove(media.id)
                                else selectedIds.add(media.id)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                AnimatedVisibility(
                    visible = isSelectMode && selectedIds.isNotEmpty(),
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
                            FooterActionItem(
                                icon = LMicons.Delete,
                                text = "삭제",
                                color = color3,
                                onClick = { showDeleteDialog = true },
                            )

                            FooterActionItem(
                                icon = LMicons.Download,
                                text = "저장",
                                color = color3,
                                onClick = { /* preview no-op */ },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                selectedIds.clear()
                showDeleteDialog = false
                isSelectMode = false
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

/** 프리뷰용 더미 데이터 */
private fun fakeTempMedias(count: Int): List<SharedMedia> =
    List(count) { i ->
        SharedMedia(
            id = i.toString(),
            type = SharedMedia.MediaType.PHOTO,
            localUri = null,
            remoteUrl = "https://picsum.photos/600/80${i}",
            thumbnailUrl = null,
            subLocalUri = null,
            subRemoteUrl = null,
            subThumbnailUrl = null,
            cameraFacing = "DUAL",
            caption = "임시 앨범 사진 ${i + 1}",
            dateTaken = System.currentTimeMillis(),
            orientation = 0,
            uploaderName = "엄마",
            syncStatus = SharedMedia.SyncStatus.SYNCED
        )
    }
