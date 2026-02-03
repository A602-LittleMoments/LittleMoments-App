package com.a602.commonproject.feature.gallery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.component.ConfirmDeleteDialog
import com.a602.commonproject.designsystem.component.FillWrapButton
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.feature.gallery.viewmodel.TempAlbumViewModel
import com.a602.commonproject.model.data.TempMedia
import kotlinx.coroutines.launch

@Composable
fun TempGridGalleryRoute(
    onMediaClick: (TempMedia) -> Unit,
    onBackClick: () -> Unit,
    viewModel: TempAlbumViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    TempGridGallery(
        medias = uiState.medias,
        onMediaClick = onMediaClick,
        onDeleteSelected = { ids ->
            viewModel.deleteSelected(ids) {
                scope.launch {
                    snackbarHostState.showSnackbar("선택한 사진을 삭제했어요")
                }            }
        },
        onSaveSelected = { ids ->
            viewModel.saveToShared(ids) {
                scope.launch {
                    snackbarHostState.showSnackbar("사진이 가족 앨범에 저장되었어요")
                }            }
        },
        onClearAll = {
            viewModel.clearAll {
                scope.launch {
                    snackbarHostState.showSnackbar("임시 앨범을 모두 비웠어요")
                }
            }
        },
        onBackClick = {
            onBackClick()
        },
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun TempGridGallery(
    medias: List<TempMedia>,
    onMediaClick: (TempMedia) -> Unit,
    onDeleteSelected: (List<String>) -> Unit,
    onSaveSelected: (List<String>) -> Unit,
    onClearAll: () -> Unit,
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    var isSelectMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<String>() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.gallery_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(modifier = Modifier.fillMaxSize()) {
            LMTopAppBar(
                title = "임시 앨범",
                navigationIcon = LMicons.Back,
                onNavigationClick = onBackClick,
            )

            Spacer(Modifier.height(56.dp)) 

            // 상단 설명 탭
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp), 
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF001229).copy(alpha = 0.8f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = "임시 앨범의 사진은 30일 후 사라져요.\n소중한 사진은 저장해주세요!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // 상단 버튼
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 10.dp),
                ) {
                    if (medias.isNotEmpty()) {
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
                }

                // Empty / Grid 분기
                if (medias.isEmpty()) {
                    TempAlbumEmptyState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp),
                    )
                } else {
                    TempImageGrid(
                        medias = medias,
                        isSelectMode = isSelectMode,
                        selectedIds = selectedIds.toList(),
                        onClick = { media ->
                            if (isSelectMode) {
                                if (selectedIds.contains(media.id)) selectedIds.remove(media.id)
                                 else selectedIds.add(media.id)
                            } else {
                                onMediaClick(media)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // 하단 액션바
        AnimatedVisibility(
            visible = isSelectMode && selectedIds.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF001229).copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 40.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FooterActionItem(
                        icon = LMicons.Delete,
                        text = "삭제",
                        color = Color.White,
                        onClick = { showDeleteDialog = true },
                    )

                    FooterActionItem(
                        icon = LMicons.Download,
                        text = "저장",
                        color = Color.White,
                        onClick = {
                            val ids = selectedIds.toList()
                            onSaveSelected(ids)
                            selectedIds.clear()
                            isSelectMode = false
                        },
                    )
                }
            }
        }

        // 스낵바
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = Color(0xFF001229).copy(alpha = 0.9f),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }

    // 삭제 다이얼로그
    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            onConfirm = {
                onDeleteSelected(selectedIds.toList())
                selectedIds.clear()
                showDeleteDialog = false
                isSelectMode = false
            },
            onDismiss = { showDeleteDialog = false },
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

@Composable
fun TempAlbumEmptyState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = LMicons.Camera,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .size(80.dp)
                    .shadow(12.dp, CircleShape)
            )

            Text(
                text = "임시 앨범이 비어있어요",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        offset = Offset(2f, 4f),
                        blurRadius = 8f
                    )
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .background(Color(0xFF001229).copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(vertical = 6.dp, horizontal = 16.dp)
            )

            Text(
                text = "촬영한 사진이 임시로 저장되며\n가족 앨범에 고스란히 공유할 수 있어요.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = Offset(1f, 2f),
                        blurRadius = 6f
                    )
                ),
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "TempGrid - Empty")
@Composable
fun TempGridGalleryPreview_Empty() {
    val snackbarHostState = remember { SnackbarHostState() }

    LMTheme {
        TempGridGallery(
            medias = emptyList(),
            onMediaClick = {},
            onDeleteSelected = {},
            onSaveSelected = {},
            onClearAll = {},
            onBackClick =  {},
            snackbarHostState = snackbarHostState,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "TempGrid - With Medias")
@Composable
fun TempGridGalleryPreview_WithMedias() {
    val fakeMedias = List(30) { i ->
        TempMedia(
            id = i.toString(),
            localUri = "/path/to/temp/${i}.jpg",
            takenAt = System.currentTimeMillis(),
            expirationDate = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }

    LMTheme {
        TempGridGallery(
            medias = fakeMedias,
            onMediaClick = {},
            onDeleteSelected = {},
            onSaveSelected = {},
            onClearAll = {},
            onBackClick = {},
            snackbarHostState = snackbarHostState,
        )
    }
}
