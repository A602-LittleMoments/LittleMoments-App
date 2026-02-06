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
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.a602.commonproject.feature.album.viewmodel.SortOrder
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip


@Composable
fun GridRoute(
    date: LocalDate? = null,
    keywordId: String? = null,
    title: String? = null,
    babyId: String? = null,
    year: Int? = null,
    onBackClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit,
    viewModel: GridGalleryViewmodel = hiltViewModel(),
) {
    // Import for coroutine scope
    androidx.compose.runtime.LaunchedEffect(keywordId, title, babyId, year) {
        viewModel.setFilter(keywordId, title, babyId, year)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ✨ [Fix] 화면 복귀 시 (예: 상세화면에서 삭제 후) 데이터 갱신
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                 viewModel.refreshData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Params decide UI mode immediately
    val showCalendarButton = keywordId == null
    val topBarTitle = title ?: uiState.title

    // [New] Year History Mode Check
    val isYearHistoryMode = babyId != null && year != null

    if (isYearHistoryMode) {
        // --- Special UI for Year History ---
        YearHistoryLayout(
            title = uiState.title, // ViewModel sets this to "Namw와의 Year년 추억"
            medias = uiState.medias,
            sortOrder = uiState.sortOrder,
            onToggleSort = viewModel::toggleSortOrder,
            onBackClick = onBackClick,
            onMediaClick = onMediaClick
        )
    } else if (date != null) {
        // ... (Existing Date Pager Logic) ...
        // 1. Group all available media by Date
        val grouped = remember(uiState.medias) {
            uiState.medias.groupBy {
                Instant.ofEpochMilli(it.dateTaken)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }.toSortedMap()
        }
        val availableDates = remember(grouped) { grouped.keys.toList() }

        // 2. Find initial page
        val initialPage = remember(availableDates, date) {
            val idx = availableDates.indexOf(date)
            if (idx == -1) 0 else idx
        }

        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { availableDates.size }
        )

        val scope = androidx.compose.runtime.rememberCoroutineScope()
        var initialDateSynced by rememberSaveable { mutableStateOf(false) }

        // Fix: Data loading delay causes initialPage to be 0 (oldest).
        // Sync pager to the correct date once data is available.
        LaunchedEffect(availableDates, date) {
            if (!initialDateSynced && availableDates.isNotEmpty()) {
                val idx = availableDates.indexOf(date)
                if (idx != -1) {
                    pagerState.scrollToPage(idx)
                    initialDateSynced = true
                }
            }
        }

        GridGalleryShell(
            title = topBarTitle,
            onBackClick = onBackClick
        ) { innerPadding ->
            if (availableDates.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val pageDate = availableDates[page]
                    val pageMedias = grouped[pageDate] ?: emptyList()
                    val pageHeaderText = "${pageDate.year}년 ${pageDate.monthValue}월 ${pageDate.dayOfMonth}일"

                    GridGalleryContent(
                        medias = pageMedias,
                        headerText = pageHeaderText,
                        sortOrder = uiState.sortOrder,
                        onToggleSort = viewModel::toggleSortOrder,
                        showCalendarButton = true, // 날짜 보기 모드에서는 헤더(날짜+화살표) 표시
                        onCalendarClick = onCalendarClick,
                        onMediaClick = onMediaClick,
                        topPadding = innerPadding.calculateTopPadding(),
                        onHeaderPrevClick = {
                            if (pagerState.currentPage > 0) {
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                            }
                        },
                        onHeaderNextClick = {
                             if (pagerState.currentPage < pagerState.pageCount - 1) {
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                            }
                        }
                    )
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("사진이 없습니다.", color = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    } else {
        // Fallback to original single-view logic for keyword/baby/all
        val filtered = remember(uiState.medias, date) {
             uiState.medias // date is null here
        }
        val headerText = "Recent" // Or dynamic based on filters

        GridGalleryScreen(
            medias = filtered,
            headerText = headerText,
            sortOrder = uiState.sortOrder,
            onToggleSort = viewModel::toggleSortOrder,
            onCalendarClick = onCalendarClick,
            onMediaClick = onMediaClick,
            onBackClick = onBackClick,
            title = topBarTitle,
            showCalendarButton = showCalendarButton
        )
    }
}
private fun SharedMedia.isSameDay(target: LocalDate): Boolean {
    val day = Instant.ofEpochMilli(this.dateTaken)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return day == target
}

// 🚀 [NEW] Year History Layout Component
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun YearHistoryLayout(
    title: String,
    medias: List<SharedMedia>,
    sortOrder: SortOrder = SortOrder.LATEST,
    onToggleSort: () -> Unit = {},
    onBackClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit
) {
    // 1. Group by Date
    val groupedFn = remember(medias, sortOrder) {
        val grouped = medias.groupBy {
            Instant.ofEpochMilli(it.dateTaken)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
        if (sortOrder == SortOrder.LATEST) {
            grouped.toSortedMap(compareByDescending { it })
        } else {
            grouped.toSortedMap(compareBy { it })
        }
    }

    Scaffold(
        topBar = {
            LMTopAppBar(
                title = title,
                onNavigationClick = onBackClick, // This handles the "Back" arrow
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background
            Image(
                painter = painterResource(id = R.drawable.gallery_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // Content Container (Dark Glass card - Matched with GridGalleryContent)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(top = 24.dp) // ✨ Extra spacing from TopBar
                    .navigationBarsPadding() // ✨ Prevent overlap with bottom bar
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 30.dp) // ✨ Match Planet Photo style (was 16.dp)
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .border(1.dp, androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(12.dp) // Padding inside the card
            ) {
                 if (medias.isEmpty()) {
                     Box(Modifier.fillMaxSize().background(background.copy(alpha = 0.1f), shape = RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
                         Text("아직 추억이 없어요.", color = androidx.compose.ui.graphics.Color.White)
                     }
                 } else {
                     androidx.compose.foundation.lazy.LazyColumn(
                         modifier = Modifier.fillMaxSize()
                             .background(background.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                             .clip(RoundedCornerShape(24.dp)),
                         verticalArrangement = Arrangement.spacedBy(24.dp),
                         contentPadding = androidx.compose.foundation.layout.PaddingValues(
                             start = 12.dp, end = 12.dp, top = 16.dp, bottom = 24.dp
                         )
                     ) {
                         // Sort Button In Year History
                         item {
                             Row(
                                 modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                 horizontalArrangement = Arrangement.End
                             ) {
                                 Text(
                                     text = if (sortOrder == SortOrder.LATEST) "최신순 ↓" else "오래된순 ↑",
                                     style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                     color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                                     modifier = Modifier
                                         .clickable(onClick = onToggleSort)
                                         .padding(8.dp)
                                 )
                             }
                         }

                         groupedFn.forEach { (date, dailyMedias) ->
                             // Header (Sticky-like behavior within list)
                             // [Design Match] Removed light gray, added transparent/dark style
                             // Header
                             // [Design Match] Changed from stickyHeader to item (scrolls with content)
                             // Removed background to show just text, matching main Gallery Grid style.
                             item {
                                 Box(
                                     modifier = Modifier
                                         .fillMaxWidth()
                                         .padding(start = 12.dp, top = 16.dp, bottom = 8.dp) // 날짜를 오른쪽으로 조금 더 이동
                                 ) {
                                     Text(
                                         text = "${date.year}.${String.format("%02d", date.monthValue)}.${String.format("%02d", date.dayOfMonth)}",
                                         style = MaterialTheme.typography.bodyLarge.copy(
                                             fontWeight = FontWeight.Bold,
                                             fontSize = 16.sp
                                         ),
                                         color = androidx.compose.ui.graphics.Color.White
                                     )
                                 }
                             }

                             // Grid Items for this date
                             // using flow row or simple chunking since LazyColumn can't nest LazyVerticalGrid easily without fixed height
                             item {
                                 // Simple Flow Layout
                                 Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                     dailyMedias.chunked(3).forEach { rowMedias ->
                                         Row(
                                             horizontalArrangement = Arrangement.spacedBy(4.dp),
                                             modifier = Modifier.fillMaxWidth()
                                         ) {
                                             rowMedias.forEach { media ->
                                                 // [Fix] Changed aspect ratio from 1f (Square) to 3f/4f (Polaroid/Portrait)
                                                 // to match the main Gallery Grid style as requested.
                                                 Box(modifier = Modifier.weight(1f).aspectRatio(3f/4f)) {
                                                     com.a602.coommonproject.ui.FramelessPhotoItem(
                                                         media = media,
                                                         onClick = { onMediaClick(media) }
                                                     )
                                                 }
                                             }
                                             // Fill empty slots if last row has < 3 items
                                             repeat(3 - rowMedias.size) {
                                                 Spacer(modifier = Modifier.weight(1f))
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
}


// 격자 보기
@Composable
// Shell Component (Scaffold + Background)
fun GridGalleryShell(
    title: String,
    onBackClick: () -> Unit,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            LMTopAppBar(
                title = title,
                onNavigationClick = onBackClick,
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
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

            content(innerPadding)
        }
    }
}

// Inner Content Component (Glass Box + Header + Grid)
@Composable
fun GridGalleryContent(
    medias: List<SharedMedia>,
    headerText: String,
    sortOrder: SortOrder = SortOrder.LATEST,
    onToggleSort: () -> Unit = {},
    showCalendarButton: Boolean,
    onCalendarClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit,
    topPadding: androidx.compose.ui.unit.Dp,
    onHeaderPrevClick: (() -> Unit)? = null,
    onHeaderNextClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding)
            .padding(horizontal = 8.dp)
            .padding(top = 24.dp), // Increased top spacing
    ) {
        // Main Container (Glass-like with Dark Theme)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding() // Move up to act as margin
                .padding(bottom = 30.dp) // Almost touching the bottom bar
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .border(1.dp, androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                .padding(12.dp) // Adjust inner padding to match calendar
        ) {
            // 1. 상단 헤더 영역
            if (showCalendarButton) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (onHeaderPrevClick != null && onHeaderNextClick != null) {
                         // Date Mode: Center Align with Arrows
                         androidx.compose.material3.IconButton(onClick = onHeaderPrevClick) {
                             androidx.compose.material3.Icon(
                                 imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowLeft,
                                 contentDescription = "Previous Date",
                                 tint = androidx.compose.ui.graphics.Color.White
                             )
                         }

                         Text(
                            text = headerText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                            ),
                            color = androidx.compose.ui.graphics.Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )

                        androidx.compose.material3.IconButton(onClick = onHeaderNextClick) {
                             androidx.compose.material3.Icon(
                                 imageVector = androidx.compose.material.icons.Icons.Default.KeyboardArrowRight,
                                 contentDescription = "Next Date",
                                 tint = androidx.compose.ui.graphics.Color.White
                             )
                         }

                    } else {
                        // Regular Mode: Start Align
                        Text(
                            text = headerText,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                            ),
                             color = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.padding(start = 8.dp) // 헤더를 오른쪽으로 조금 더 이동
                        )
                    }

                    // Sort Toggle Area (Only if not in Date Pager mode which is usually sorted by date context)
                    if (onHeaderPrevClick == null) {
                        Text(
                            text = if (sortOrder == SortOrder.LATEST) "최신순 ↓" else "오래된순 ↑",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                            modifier = Modifier
                                .clickable(onClick = onToggleSort)
                                .padding(8.dp)
                        )
                    }
                }
            }

            // Grid Area
            Box(modifier = Modifier.fillMaxSize()
                .background(background.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp)))
            {
                GalleryGridFrameless(
                    medias = medias,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 12.dp, end = 12.dp, top = 16.dp, bottom = 24.dp
                    ),
                    onClick = onMediaClick,
                )
            }
        }
    }
}

// Legacy wrapper for Preview and simple usage
@Composable
fun GridGalleryScreen(
    medias: List<SharedMedia>,
    onCalendarClick: () -> Unit,
    onBackClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit,
    sortOrder: SortOrder = SortOrder.LATEST,
    onToggleSort: () -> Unit = {},
    headerText: String = "Recent",
    modifier: Modifier = Modifier,
    title: String = "갤러리",
    showCalendarButton: Boolean = true
) {
    GridGalleryShell(title = title, onBackClick = onBackClick) { innerPadding ->
        GridGalleryContent(
            medias = medias,
            headerText = headerText,
            sortOrder = sortOrder,
            onToggleSort = onToggleSort,
            showCalendarButton = showCalendarButton,
            onCalendarClick = onCalendarClick,
            onMediaClick = onMediaClick,
            topPadding = innerPadding.calculateTopPadding(),
            onHeaderPrevClick = null,
            onHeaderNextClick = null
        )
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
