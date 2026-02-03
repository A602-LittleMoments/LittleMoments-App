package com.a602.commonproject.feature.album

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.a602.commonproject.designsystem.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.FillWrapButton
import com.a602.commonproject.designsystem.component.LMNavigationDefaults.NavigationBarHeight
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.feature.album.viewmodel.GridGalleryViewmodel
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.sp
import com.a602.commonproject.model.data.SharedMedia
import com.a602.coommonproject.ui.GalleryGridFrameless
import com.a602.coommonproject.ui.GalleryGridPolaroid
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.lightbackground
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.runtime.LaunchedEffect


@Composable
fun GridRoute(
    date: LocalDate? = null,
    keywordId: String? = null,
    title: String? = null,
    onBackClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit,
    viewModel: GridGalleryViewmodel = hiltViewModel(),
    ) {
    LaunchedEffect(keywordId, title) {
        viewModel.setFilter(keywordId, title)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Params decide UI mode immediately
    val showCalendarButton = keywordId == null
    val topBarTitle = title ?: uiState.title

    val filtered = remember(uiState.medias, date) {
        if (date == null) uiState.medias
        else uiState.medias.filter { it.isSameDay(date) }
    }

    val headerText = remember(date) {
        date?.let { "${it.year}년 ${it.monthValue}월 ${it.dayOfMonth}일" } ?: "Recent"
    }

    GridGalleryScreen(
        medias = filtered,
        headerText = headerText,
        onCalendarClick = onCalendarClick,
        onMediaClick = onMediaClick,
        onBackClick = onBackClick,
        title = topBarTitle,
        showCalendarButton = showCalendarButton
    )
}
private fun SharedMedia.isSameDay(target: LocalDate): Boolean {
    val day = Instant.ofEpochMilli(this.dateTaken)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return day == target
}

// 격자 보기
@Composable
fun GridGalleryScreen(
    medias: List<SharedMedia>,
    onCalendarClick: () -> Unit,
    onBackClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit,
    headerText: String = "Recent",
    modifier: Modifier = Modifier,
    title: String = "갤러리",
    showCalendarButton: Boolean = true
) {
    Scaffold(
        topBar = {
            LMTopAppBar(
                title = title,
                onNavigationClick = onBackClick,
            )
        }
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.gallery_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // Rocket background element
            Image(
                painter = painterResource(id = R.drawable.rocket4),
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .offset(x = (-60).dp, y = 80.dp)
                    .graphicsLayer(rotationZ = -35f),
                alpha = 0.8f
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()) // Top padding from Scaffold (AppBar)
                    .padding(horizontal = 8.dp), // Side margin only

            ) {
                // Main Container (Glass-like with Dark Theme)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) // Bottom is flat or rounded? User said "go to the end". Usually means flat bottom or rounded? Let's keep rounded but maybe modify shape. "RoundedCornerShape(16.dp)" is all corners. If it goes to bottom, maybe bottom corners should be 0? Or keep them.
                        // Let's keep 16.dp as requested style, just extending down.
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .border(1.dp, androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(start = 16.dp, end = 16.dp, top = 24.dp) // Content padding increased
                        .navigationBarsPadding() // Push content up above nav bar
                        .padding(bottom = 32.dp) // Extra bottom padding for visuals increased
                ) {
                    // 1. 상단 헤더 영역 (캘린더 보기 버튼이 있을 때만 표시 = 앨범 모드일 때만)
                    if (showCalendarButton) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = headerText,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                color = androidx.compose.ui.graphics.Color.White
                            )

                            FillWrapButton(
                                text = "캘린더 보기",
                                onClick = onCalendarClick,
                            )
                        }
                    }

                    // Grid Area
                    Box(modifier = Modifier.weight(1f)) {
                        GalleryGridFrameless(
                            medias = medias,
                            onClick = onMediaClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun fakeMediaList(): List<SharedMedia> {
    return List(6) { i ->
        SharedMedia(
            id = i.toString(),
            type = SharedMedia.MediaType.PHOTO,
            localUri = null,
            remoteUrl = "https://picsum.photos/600/80${i}",
            thumbnailUrl = null,
            subLocalUri = null,
            subRemoteUrl = "https://picsum.photos/300/40${i}",
            subThumbnailUrl = null,
            cameraFacing = "DUAL",
            caption = "프리뷰입니다프리뷰프리뷰프리뷰프리뷰",
            dateTaken = System.currentTimeMillis(),
            orientation = 0,
            uploaderName = "엄마",
            syncStatus = SharedMedia.SyncStatus.SYNCED,
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun GridGalleryScreenPreview() {
    LMTheme {
        GridGalleryScreen(
            medias = fakeMediaList(),
            onCalendarClick = {},
            onMediaClick = {},
            onBackClick = {}
        )
    }
}
