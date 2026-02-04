package com.a602.commonproject.feature.album

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.a602.commonproject.model.data.SharedMedia
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

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
            Image(
                painter = painterResource(id = com.a602.commonproject.designsystem.R.drawable.gallery_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            when {
                uiState.isLoading -> {
                    Text("불러오는 중…")
                }

                uiState.error != null || uiState.media == null -> {
                    Text(uiState.error ?: "사진을 불러오지 못했어요")
                }

                else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // 1. Background Visuals (Same as Detail Screen)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(80.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 40.dp)
                    ) {
                        // Polaroid Frame
                        Column(
                            modifier = Modifier
                                .width(300.dp)
                                .shadow(12.dp, RoundedCornerShape(2.dp))
                                .background(Color.White)
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .background(Color.LightGray)
                            ) {
                                AsyncImage(
                                    model = uiState.media.remoteUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                if (uiState.media.subRemoteUrl != null || uiState.media.subThumbnailUrl != null) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(8.dp)
                                            .size(80.dp)
                                            .border(1.dp, Color.Black)
                                            .background(Color.Gray)
                                    ) {
                                        AsyncImage(
                                            model = uiState.media.subRemoteUrl ?: uiState.media.subThumbnailUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Date Info
                            val date = Instant.ofEpochMilli(uiState.media.dateTaken)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            Text(
                                text = "${date.year}.${String.format("%02d", date.monthValue)}.${String.format("%02d", date.dayOfMonth)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(Modifier.height(20.dp))
                        }

                        // Decorations (Stars)
                        val purpleStarColor = Color(0xFFEDBDFF)
                        val yellowStarColor = Color(0xFFFFF5BA)

                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = purpleStarColor,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(x = (-32).dp, y = (-32).dp)
                                .size(80.dp)
                                .graphicsLayer(rotationZ = -25f)
                        )

                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = yellowStarColor,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (28).dp, y = (-28).dp)
                                .size(72.dp)
                                .graphicsLayer(rotationZ = 25f)
                        )
                    }
                }

                // 2. Popup Input Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)) // Semi-transparent dimming
                        .imePadding(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .shadow(16.dp, RoundedCornerShape(24.dp))
                            .background(Color.White, RoundedCornerShape(24.dp))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "코멘트 수정",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = uiState.caption,
                                onValueChange = onCaptionChange,
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester),
                                textStyle = MaterialTheme.typography.bodyLarge,
                                placeholder = {
                                    Text(
                                        text = "소중한 추억을 기록해보세요",
                                        color = Color.Gray
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF5F5F5),
                                    unfocusedContainerColor = Color(0xFFF5F5F5),
                                    focusedIndicatorColor = com.a602.commonproject.designsystem.theme.main,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = com.a602.commonproject.designsystem.theme.main
                                ),
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 5
                            )

                            Spacer(Modifier.width(12.dp))

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .shadow(4.dp, RoundedCornerShape(12.dp))
                                    .background(com.a602.commonproject.designsystem.theme.main, RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (!uiState.isSaving) onDone()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isSaving) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                        contentDescription = "저장",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
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
