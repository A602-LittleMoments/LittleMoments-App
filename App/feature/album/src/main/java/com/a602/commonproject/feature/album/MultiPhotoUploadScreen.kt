package com.a602.commonproject.feature.album

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a602.commonproject.feature.album.viewmodel.MultiPhotoUploadViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import java.io.File
import androidx.compose.ui.res.painterResource
import com.a602.commonproject.designsystem.R as DesignR
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.feature.album.viewmodel.UploadState


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

    // Manage captions locally
    val captions = remember { mutableStateMapOf<String, String>() }

    val pagerState = rememberPagerState(pageCount = { selectedMedias.size })
    val snackbarHostState = remember { SnackbarHostState() }

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

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            LMTopAppBar(
                title = "${pagerState.currentPage + 1} / ${selectedMedias.size}",
                navigationIcon = LMicons.Back,
                onNavigationClick = onBackClick
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

            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (selectedMedias.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Photo Pager
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) { page ->
                        val media = selectedMedias[page]
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = rememberAsyncImagePainter(File(media.localUri)),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    // Caption Input
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("코멘트 작성 (선택)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))

                            val currentMedia = selectedMedias[pagerState.currentPage]
                            val text = captions[currentMedia.id] ?: ""

                            TextField(
                                value = text,
                                onValueChange = { captions[currentMedia.id] = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { 
                                    Text(
                                        "사진에 대한 설명을 남겨주세요",
                                        color = Color.Gray.copy(alpha = 0.5f),
                                        style = MaterialTheme.typography.bodyMedium
                                    ) 
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                        }
                    }

                    // Upload Button
                    Button(
                        onClick = {
                            viewModel.upload(captions) {
                                // ViewModel's onComplete handles basic navigation, 
                                // but we use LaunchedEffect for better UX with snackbar.
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        enabled = uploadState !is UploadState.Uploading
                    ) {
                        if (uploadState is UploadState.Uploading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("공유 앨범에 올리기")
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
}
