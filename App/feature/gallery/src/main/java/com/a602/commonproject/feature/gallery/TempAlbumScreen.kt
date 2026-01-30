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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.model.data.SharedMedia
import kotlinx.coroutines.launch


@Composable
fun TempGridGalleryRoute(

){
    TempGridGallery(
        medias = TODO(),
        onMediaClick = TODO(),
        onDeleteSelected = TODO(),
        onSaveSelected = TODO(),
        onClearAll = TODO(),
        modifier = TODO()
    )
}

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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = color3,
                    textAlign = TextAlign.Center,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                // 상단 버튼
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
        }

        AnimatedVisibility(
            visible = isSelectMode && selectedIds.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
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
                            val ids = selectedIds.toList()
                            onSaveSelected(ids)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "사진이 가족 앨범에 저장되었어요",
                                    withDismissAction = false,
                                    duration = SnackbarDuration.Short
                                )
                            }
                            selectedIds.clear()
                            isSelectMode = false
                        },
                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = lightblue,
                contentColor = color3
            )
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


/**
 *
 * 프리뷰 영역
 */

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun TempGridGalleryPreview() {
    val fakeMedias = List(30) { i ->
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

    LMTheme {
        TempGridGallery(
            medias = fakeMedias,
            onMediaClick = {},
            onDeleteSelected = {},
            onSaveSelected = {},
            onClearAll = {}
        )
    }
}
