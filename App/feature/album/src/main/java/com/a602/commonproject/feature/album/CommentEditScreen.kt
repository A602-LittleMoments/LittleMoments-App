package com.a602.commonproject.feature.album

import Polaroid
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.feature.album.viewmodel.CommentEditUiState
import com.a602.commonproject.feature.album.viewmodel.CommentEditViewModel
import com.a602.commonproject.model.data.SharedMedia
import kotlinx.coroutines.launch

@Composable
fun CommentEditRoute(
    mediaId: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: CommentEditViewModel = hiltViewModel()
) {
    LaunchedEffect(mediaId) { viewModel.setMediaId(mediaId) }

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

    LaunchedEffect(uiState.media?.id) {
        if (uiState.media != null) focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = background,
        topBar = {
            LMTopAppBar(
                title = "코멘트 수정",
                onNavigationClick = onBack,
                actionIcon = LMicons.Download,
                actionIconContentDescription = "저장",
                onActionClick = {
                    if (uiState.isLoading) return@LMTopAppBar
                    if (uiState.error != null) return@LMTopAppBar
                    if (uiState.media == null) return@LMTopAppBar
                    if (uiState.isSaving) return@LMTopAppBar
                    onDone()
                }
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
            when {
                uiState.isLoading -> {
                    Text("불러오는 중…")
                }

                uiState.error != null || uiState.media == null -> {
                    Text(uiState.error ?: "사진을 불러오지 못했어요")
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Polaroid(
                                media = uiState.media,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )

                            TextField(
                                value = uiState.caption,
                                onValueChange = onCaptionChange,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .align(Alignment.Center)
                                    .padding(bottom = 80.dp)
                                    .focusRequester(focusRequester),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    textAlign = TextAlign.Center
                                ),
                                placeholder = {
                                    Text(
                                        text = "코멘트를 입력하세요",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color.Gray
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = lightbackground,
                                    unfocusedContainerColor = lightbackground,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = Color.Gray
                                )
                            )
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
