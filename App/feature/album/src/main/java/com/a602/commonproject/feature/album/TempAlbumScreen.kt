package com.a602.commonproject.feature.album

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
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.feature.album.viewmodel.TempAlbumViewModel
import com.a602.commonproject.model.data.TempMedia
import kotlinx.coroutines.launch

@Composable
fun TempGridGalleryRoute(
    onMediaClick: (TempMedia) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToUpload: (List<String>) -> Unit,
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
            onNavigateToUpload(ids)
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

            // 상단 설명 텍스트 (이미지와 유사하게)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFF9E6)) // Ivory/Cream background
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "30일 뒤 사라질 우리들의 순간을 담아봐요",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // 상단 버튼 (전체 선택 / 선택)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 4.dp),
                ) {
                    if (medias.isNotEmpty()) {
                        // 전체 선택 / 해제 버튼
                        if (isSelectMode) {
                            FillWrapButton(
                                onClick = {
                                    if (selectedIds.size == medias.size) {
                                        selectedIds.clear() // 전체 해제
                                    } else {
                                        selectedIds.clear()
                                        selectedIds.addAll(medias.map { it.id }) // 전체 선택
                                    }
                                },
                                text = if (selectedIds.size == medias.size) "선택해제" else "전체선택",
                                modifier = Modifier.align(Alignment.CenterStart),
                            )
                        } else {
                            // 일반 모드일 때는 전체 선택 버튼 (Design Guide image 1 - Left button seems to be '전체선택' even in normal mode? 
                            // Or maybe it's cleaner to show it only when relevant. 
                            // The user said "임의의 사진 하나를 꾹 누르면...". 
                            // Let's keep "전체선택" visible if desired, or maybe just "Select" button.
                            // Image 1 shows "전체선택" on left and "선택" on right.
                             FillWrapButton(
                                onClick = {
                                    isSelectMode = true
                                    selectedIds.clear()
                                    selectedIds.addAll(medias.map { it.id })
                                },
                                text = "전체선택",
                                modifier = Modifier.align(Alignment.CenterStart),
                            )
                        }

                        // 선택 / 취소 버튼
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
                        onLongClick = { media ->
                            if (!isSelectMode) {
                                isSelectMode = true
                                selectedIds.add(media.id)
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
                color = Color.White,
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 20.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 삭제 버튼 (NavyBlue 배경)
                    FillWrapButton(
                        text = "삭제",
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B2430) // NavyBlue
                        )
                    )

                    // 저장 버튼 (NavyBlue 배경)
                    FillWrapButton(
                        text = "저장",
                        onClick = {
                            val ids = selectedIds.toList()
                            onSaveSelected(ids)
                            selectedIds.clear()
                            isSelectMode = false
                        },
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B2430) // NavyBlue
                        )
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
