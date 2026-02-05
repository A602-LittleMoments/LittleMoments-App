package com.a602.commonproject.feature.album

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.feature.album.viewmodel.CommentEditUiState
import com.a602.commonproject.feature.album.viewmodel.CommentEditViewModel
import com.a602.commonproject.designsystem.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import Polaroid
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.a602.commonproject.model.data.SharedMedia
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@Composable
fun CommentEditRoute(
    mediaId: String,
    isTemp: Boolean = false,
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: CommentEditViewModel = hiltViewModel()
) {
    LaunchedEffect(mediaId) { viewModel.setMediaId(mediaId, isTemp) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    when {
        uiState.isLoading -> {
            CommentEditScreen(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onCaptionChange = viewModel::updateCaption,
                onBack = onBack,
                onDone = {},
            )
        }

        uiState.error != null || uiState.media == null -> {
            CommentEditScreen(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onCaptionChange = viewModel::updateCaption,
                onBack = onBack,
                onDone = {},
            )
        }

        else -> {
            CommentEditScreen(
                uiState = uiState,
                snackbarHostState = snackbarHostState,
                onCaptionChange = viewModel::updateCaption,
                onBack = onBack,
                onDone = {
                    if (uiState.isSaving) return@CommentEditScreen
                    viewModel.saveCaption(
                        onSuccess = onDone,
                        onError = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                },
            )
        }
    }
}



@Composable
fun CommentEditScreen(
    uiState: CommentEditUiState,
    snackbarHostState: SnackbarHostState,
    onCaptionChange: (String) -> Unit,
    onBack: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    // 키보드 자동 포커스
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            LMTopAppBar(
                title = "코멘트 수정",
                onNavigationClick = onBack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // 1. Background Image
            Image(
                painter = painterResource(id = R.drawable.gallery_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            if (uiState.isLoading) {
                androidx.compose.material3.CircularProgressIndicator(color = Color.White)
            } else if (uiState.media == null) {
                Text(
                    text = uiState.error ?: "사진을 불러오지 못했어요",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // 2. Polaroid Preview (Background Context)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                     val displayMedia = remember(uiState.media, uiState.caption) {
                        uiState.media!!.copy(caption = uiState.caption)
                    }

                    // Show Polaroid nicely in the back
                    Polaroid(
                        media = displayMedia,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .graphicsLayer { alpha = 0.6f }
                    )
                }

                // 3. Dimmed Overlay (Simulating Modal)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) {}
                )

                // 4. Edit Card (The "Popup")
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .imePadding(), // Move up with keyboard
                    shape = RoundedCornerShape(24.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = com.a602.commonproject.designsystem.theme.lightbackground
                    ),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "코멘트 작성",
                            style = MaterialTheme.typography.headlineSmall,
                            color = com.a602.commonproject.designsystem.theme.color3
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        com.a602.commonproject.designsystem.component.LMEditInputField(
                            value = uiState.caption,
                            onValueChange = onCaptionChange,
                            label = "코멘트",
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            singleLine = false,
                            placeholder = "소중한 추억을 기록해보세요"
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            androidx.compose.material3.TextButton(
                                onClick = onBack
                            ) {
                                Text("취소")
                            }
                            Spacer(Modifier.width(8.dp))
                            androidx.compose.material3.Button(
                                onClick = {
                                    if (!uiState.isSaving) {
                                        onDone()
                                    }
                                },
                                enabled = !uiState.isSaving,
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = com.a602.commonproject.designsystem.theme.main
                                )
                            ) {
                                if (uiState.isSaving) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("저장")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "CommentEdit - Loading")
@Composable
private fun Preview_CommentEdit_Loading() {
    val snackbar = remember { SnackbarHostState() }
    CommentEditScreen(
        uiState = CommentEditUiState(isLoading = true),
        snackbarHostState = snackbar,
        onCaptionChange = {},
        onBack = {},
        onDone = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "CommentEdit - Error")
@Composable
private fun Preview_CommentEdit_Error() {
    val snackbar = remember { SnackbarHostState() }
    CommentEditScreen(
        uiState = CommentEditUiState(isLoading = false, error = "사진을 불러오지 못했어요"),
        snackbarHostState = snackbar,
        onCaptionChange = {},
        onBack = {},
        onDone = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760, name = "CommentEdit - Content")
@Composable
private fun Preview_CommentEdit_Content() {
    val snackbar = remember { SnackbarHostState() }
    val fakeMedia = SharedMedia(
        id = "1",
        type = SharedMedia.MediaType.PHOTO,
        localUri = null,
        remoteUrl = "https://picsum.photos/600/800",
        thumbnailUrl = null,
        subLocalUri = null,
        subRemoteUrl = null,
        subThumbnailUrl = null,
        cameraFacing = "DUAL",
        caption = "원래 캡션",
        dateTaken = System.currentTimeMillis(),
        orientation = 0,
        uploaderName = "엄마",
        syncStatus = SharedMedia.SyncStatus.SYNCED
    )

    CommentEditScreen(
        uiState = CommentEditUiState(
            media = fakeMedia,
            caption = "행복 하 하 하 하다",
            isLoading = false
        ),
        snackbarHostState = snackbar,
        onCaptionChange = {},
        onBack = {},
        onDone = {}
    )
}
