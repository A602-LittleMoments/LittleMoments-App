package com.a602.commonproject.feature.album

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a602.commonproject.feature.album.viewmodel.MultiPhotoUploadViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.R as DesignR
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.feature.album.viewmodel.UploadState
import Polaroid
import com.a602.commonproject.model.data.SharedMedia
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester


@Composable
fun MultiPhotoUploadScreen(
    mediaIds: List<String>,
    onBackClick: () -> Unit,
    onUploadSuccess: () -> Unit,
    viewModel: MultiPhotoUploadViewModel = hiltViewModel()
) {
    LaunchedEffect(mediaIds) {
        viewModel.setTargetIds(mediaIds)
    }

    val selectedMedias by viewModel.selectedMedias.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val userNickname by viewModel.userNickname.collectAsStateWithLifecycle()

    // Manage captions locally
    val captions = remember { mutableStateMapOf<String, String>() }

    val pagerState = rememberPagerState(pageCount = { selectedMedias.size })
    val snackbarHostState = remember { SnackbarHostState() }

    // Popup Input State
    var showInput by remember { mutableStateOf(false) }
    var currentEditingCaption by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Handle success/error state
    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UploadState.Success -> {
                snackbarHostState.showSnackbar("공유 앨범에 저장되었습니다!")
                kotlinx.coroutines.delay(800) // Give user time to see it
                onUploadSuccess()
            }
            is UploadState.Error -> {
                snackbarHostState.showSnackbar((uploadState as UploadState.Error).message)
            }
            else -> {}
        }
    }

    LaunchedEffect(showInput) {
        if (showInput) focusRequester.requestFocus()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            val titleText = when {
                uploadState is UploadState.Uploading -> "공유 앨범에 저장 중..."
                selectedMedias.isEmpty() -> "공유 앨범 저장"
                else -> "${pagerState.currentPage + 1} / ${selectedMedias.size}"
            }
            LMTopAppBar(
                title = titleText,
                navigationIcon = LMicons.Back,
                onNavigationClick = {
                     if (showInput) showInput = false else onBackClick()
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = DesignR.drawable.gallery_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (selectedMedias.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 1. Photo Pager with Polaroid
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 40.dp),
                            pageSpacing = 16.dp
                        ) { page ->
                            val media = selectedMedias[page]
                            val currentCaption = captions[media.id] ?: ""

                            // Map TempMedia to SharedMedia for Polaroid
                            val sharedMedia = remember(media, currentCaption, userNickname) {
                                SharedMedia(
                                    id = media.id,
                                    type = SharedMedia.MediaType.PHOTO,
                                    localUri = media.localUri,
                                    remoteUrl = null,
                                    thumbnailUrl = null,
                                    subLocalUri = media.subLocalUri,
                                    subRemoteUrl = null,
                                    subThumbnailUrl = null,
                                    cameraFacing = if (media.subLocalUri != null) "DUAL" else "REAR",
                                    caption = currentCaption,
                                    dateTaken = media.takenAt,
                                    orientation = 0,
                                    uploaderName = userNickname,
                                    syncStatus = SharedMedia.SyncStatus.SYNCED
                                )
                            }

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Polaroid(
                                    media = sharedMedia,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // 2. Modify Comment Button (Small, Navy)
                        if (!showInput) {
                            Button(
                                onClick = {
                                    val currentMedia = selectedMedias[pagerState.currentPage]
                                    currentEditingCaption = captions[currentMedia.id] ?: ""
                                    showInput = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1B2430)
                                ),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text(
                                    text = "코멘트 수정하기",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Upload Button
                        Button(
                            onClick = {
                                viewModel.upload(captions) { }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(56.dp),
                            enabled = uploadState !is UploadState.Uploading,
                             colors = ButtonDefaults.buttonColors(
                                containerColor = com.a602.commonproject.designsystem.theme.main // Or standard primary
                            )
                        ) {
                            if (uploadState is UploadState.Uploading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("공유 앨범에 저장")
                            }
                        }
                    }
                }

                // 4. Popup Input Overlay
                if (showInput) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { showInput = false }
                            .imePadding(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clickable(enabled = false) {},
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = com.a602.commonproject.designsystem.theme.lightbackground
                            ),
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
                                    value = currentEditingCaption,
                                    onValueChange = { currentEditingCaption = it },
                                    label = "코멘트",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(focusRequester),
                                    singleLine = false,
                                    placeholder = "소중한 추억을 기록해보세요"
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { showInput = false }) {
                                        Text("취소")
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            val currentMedia = selectedMedias[pagerState.currentPage]
                                            captions[currentMedia.id] = currentEditingCaption
                                            showInput = false
                                        }
                                    ) {
                                        Text("완료")
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                 Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     CircularProgressIndicator()
                 }
            }
        }
    }
}
