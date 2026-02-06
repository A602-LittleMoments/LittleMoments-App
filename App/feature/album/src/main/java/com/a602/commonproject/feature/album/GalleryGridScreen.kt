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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.component.FillWrapButton
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.feature.album.viewmodel.GridGalleryViewmodel
import com.a602.commonproject.feature.album.viewmodel.SortOrder
import com.a602.commonproject.model.data.SharedMedia
import com.a602.coommonproject.ui.GalleryGridFrameless
import com.a602.coommonproject.ui.FramelessPhotoItem

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.launch

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
    LaunchedEffect(keywordId, title, babyId, year) {
        viewModel.setFilter(keywordId, title, babyId, year)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Params decide UI mode
    val showCalendarButton = keywordId == null
    val topBarTitle = if (uiState.isSelectMode) "" else (title ?: uiState.title)
    val isYearHistoryMode = babyId != null && year != null

    if (isYearHistoryMode) {
        YearHistoryLayout(
            title = uiState.title,
            medias = uiState.medias,
            sortOrder = uiState.sortOrder,
            onToggleSort = viewModel::toggleSortOrder,
            onBackClick = onBackClick,
            onMediaClick = onMediaClick
        )
    } else if (date != null) {
        val grouped = remember(uiState.medias) {
            uiState.medias.groupBy {
                Instant.ofEpochMilli(it.dateTaken)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }.toSortedMap()
        }
        val availableDates = remember(grouped) { grouped.keys.toList() }

        val initialPage = remember(availableDates, date) {
            val idx = availableDates.indexOf(date)
            if (idx == -1) 0 else idx
        }

        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { availableDates.size }
        )

        var initialDateSynced by rememberSaveable { mutableStateOf(false) }

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
            onBackClick = onBackClick,
            actions = {
                if (uiState.medias.isNotEmpty()) {
                    if (uiState.isSelectMode) {
                        // 현재 페이지(날짜)에 해당하는 미디어만 선택 대상으로 설정
                        val currentTargetMedias = if (availableDates.isNotEmpty()) {
                            val currentDate = availableDates.getOrNull(pagerState.currentPage)
                            if (currentDate != null) grouped[currentDate] ?: emptyList() else uiState.medias
                        } else {
                            uiState.medias
                        }

                        FillWrapButton(
                            onClick = {
                                if (uiState.selectedIds.size == currentTargetMedias.size) {
                                    viewModel.clearSelection()
                                } else {
                                    viewModel.selectAll(currentTargetMedias.map { it.id })
                                }
                            },
                            text = if (uiState.selectedIds.size == currentTargetMedias.size && currentTargetMedias.isNotEmpty()) "선택해제" else "전체선택",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    FillWrapButton(
                        onClick = viewModel::toggleSelectMode,
                        text = if (uiState.isSelectMode) "취소" else "선택",
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            }
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
                        showCalendarButton = true,
                        onCalendarClick = onCalendarClick,
                        onMediaClick = onMediaClick,
                        topPadding = innerPadding.calculateTopPadding(),
                        isSelectMode = uiState.isSelectMode,
                        selectedIds = uiState.selectedIds,
                        onToggleSelectMode = viewModel::toggleSelectMode,
                        onToggleSelect = { viewModel.toggleSelect(it.id) },
                        onLongClick = {
                            if (!uiState.isSelectMode) {
                                viewModel.toggleSelectMode()
                                viewModel.toggleSelect(it.id)
                            }
                        },
                        onSelectAll = { viewModel.selectAll(pageMedias.map { it.id }) },
                        onDeleteSelected = {
                            viewModel.deleteSelected(
                                onSuccess = { scope.launch { snackbarHostState.showSnackbar("삭제되었습니다.") } },
                                onError = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
                            )
                        },
                        snackbarHostState = snackbarHostState,
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
                    Text("사진이 없습니다.", color = Color.White)
                }
            }
        }
    } else {
        GridGalleryScreen(
            medias = uiState.medias,
            headerText = "Recent",
            sortOrder = uiState.sortOrder,
            onToggleSort = viewModel::toggleSortOrder,
            onCalendarClick = onCalendarClick,
            onMediaClick = onMediaClick,
            onBackClick = onBackClick,
            title = topBarTitle,
            showCalendarButton = showCalendarButton,
            isSelectMode = uiState.isSelectMode,
            selectedIds = uiState.selectedIds,
            onToggleSelectMode = viewModel::toggleSelectMode,
            onToggleSelect = { viewModel.toggleSelect(it.id) },
            onLongClick = {
                if (!uiState.isSelectMode) {
                    viewModel.toggleSelectMode()
                    viewModel.toggleSelect(it.id)
                }
            },
            onSelectAll = {
                if (uiState.selectedIds.size == uiState.medias.size && uiState.medias.isNotEmpty()) {
                    viewModel.clearSelection()
                } else {
                    viewModel.selectAll(uiState.medias.map { it.id })
                }
            },
            onDeleteSelected = {
                viewModel.deleteSelected(
                    onSuccess = { scope.launch { snackbarHostState.showSnackbar("삭제되었습니다.") } },
                    onError = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
                )
            },
            snackbarHostState = snackbarHostState
        )
    }
}

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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(top = 24.dp)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 30.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(12.dp)
            ) {
                 if (medias.isEmpty()) {
                     Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         Text("아직 추억이 없어요.", color = Color.White)
                     }
                 } else {
                     androidx.compose.foundation.lazy.LazyColumn(
                         modifier = Modifier.fillMaxSize()
                             .clip(RoundedCornerShape(24.dp)),
                         verticalArrangement = Arrangement.spacedBy(24.dp),
                         contentPadding = PaddingValues(
                             start = 12.dp, end = 12.dp, top = 16.dp, bottom = 24.dp
                         )
                     ) {
                         item {
                             Row(
                                 modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                 horizontalArrangement = Arrangement.End
                             ) {
                                 Text(
                                     text = if (sortOrder == SortOrder.LATEST) "최신순 ↓" else "오래된순 ↑",
                                     style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                     color = Color.White.copy(alpha = 0.7f),
                                     modifier = Modifier
                                         .clickable(onClick = onToggleSort)
                                         .padding(8.dp)
                                 )
                             }
                         }

                         groupedFn.forEach { (date, dailyMedias) ->
                             item {
                                 Box(
                                     modifier = Modifier
                                         .fillMaxWidth()
                                         .padding(start = 12.dp, top = 16.dp, bottom = 8.dp)
                                 ) {
                                     Text(
                                         text = "${date.year}.${String.format("%02d", date.monthValue)}.${String.format("%02d", date.dayOfMonth)}",
                                         style = MaterialTheme.typography.bodyLarge.copy(
                                             fontWeight = FontWeight.Bold,
                                             fontSize = 16.sp
                                         ),
                                         color = Color.White
                                     )
                                 }
                             }

                             item {
                                 Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                     dailyMedias.chunked(3).forEach { rowMedias ->
                                         Row(
                                             horizontalArrangement = Arrangement.spacedBy(4.dp),
                                             modifier = Modifier.fillMaxWidth()
                                         ) {
                                             rowMedias.forEach { media ->
                                                 Box(modifier = Modifier.weight(1f).aspectRatio(3f/4f)) {
                                                      FramelessPhotoItem(
                                                          media = media,
                                                          onClick = { onMediaClick(media) }
                                                      )
                                                 }
                                             }
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

@Composable
fun GridGalleryShell(
    title: String,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            LMTopAppBar(
                title = title,
                onNavigationClick = onBackClick,
                actions = actions
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
    isSelectMode: Boolean = false,
    selectedIds: Set<String> = emptySet(),
    onToggleSelectMode: () -> Unit = {},
    onToggleSelect: (SharedMedia) -> Unit = {},
    onLongClick: (SharedMedia) -> Unit = {},
    onSelectAll: () -> Unit = {},
    onDeleteSelected: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onHeaderPrevClick: (() -> Unit)? = null,
    onHeaderNextClick: (() -> Unit)? = null
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding)
                .padding(horizontal = 8.dp)
                .padding(top = 24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 30.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(12.dp)
            ) {
                // 1. 상단 옵션 버튼 영역을 제거 (상단 바로 이동됨)

                if (showCalendarButton) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (onHeaderPrevClick != null && onHeaderNextClick != null) {
                             IconButton(onClick = onHeaderPrevClick) {
                                 Icon(
                                     imageVector = Icons.Default.KeyboardArrowLeft,
                                     contentDescription = "Previous Date",
                                     tint = Color.White
                                 )
                             }

                             Text(
                                text = headerText,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = onHeaderNextClick) {
                                 Icon(
                                     imageVector = Icons.Default.KeyboardArrowRight,
                                     contentDescription = "Next Date",
                                     tint = Color.White
                                 )
                             }

                        } else {
                            Text(
                                text = headerText,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                ),
                                 color = Color.White,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        if (onHeaderPrevClick == null) {
                            Text(
                                text = if (sortOrder == SortOrder.LATEST) "최신순 ↓" else "오래된순 ↑",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .clickable(onClick = onToggleSort)
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)
                    .background(background.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp)))
                {
                    GalleryGridFrameless(
                        medias = medias,
                        isSelectMode = isSelectMode,
                        selectedIds = selectedIds,
                        contentPadding = PaddingValues(
                            start = 12.dp, end = 12.dp, top = 16.dp, bottom = 24.dp
                        ),
                        onClick = {
                            if (isSelectMode) onToggleSelect(it)
                            else onMediaClick(it)
                        },
                        onLongClick = onLongClick
                    )
                }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .navigationBarsPadding() // 네비게이션 바 고려
                    , // 기존 30dp -> 80dp로 상향 조정
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FillWrapButton(
                        text = "삭제",
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B2430)
                        )
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
                containerColor = Color(0xFF001229).copy(alpha = 0.9f),
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (showDeleteDialog) {
            Dialog(onDismissRequest = { showDeleteDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = lightbackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "사진 삭제",
                            style = MaterialTheme.typography.headlineSmall,
                            color = color3
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "선택한 ${selectedIds.size}장의 사진을 삭제할까요?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = color4,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("취소", color = color4)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showDeleteDialog = false
                                    onDeleteSelected()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = main
                                )
                            ) {
                                Text("삭제", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

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
    showCalendarButton: Boolean = true,
    isSelectMode: Boolean = false,
    selectedIds: Set<String> = emptySet(),
    onToggleSelectMode: () -> Unit = {},
    onToggleSelect: (SharedMedia) -> Unit = {},
    onLongClick: (SharedMedia) -> Unit = {},
    onSelectAll: () -> Unit = {},
    onDeleteSelected: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    GridGalleryShell(
        title = if (isSelectMode) "" else title,
        onBackClick = onBackClick,
        actions = {
            if (medias.isNotEmpty()) {
                if (isSelectMode) {
                    FillWrapButton(
                        onClick = onSelectAll,
                        text = if (selectedIds.size == medias.size) "선택해제" else "전체선택",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                FillWrapButton(
                    onClick = onToggleSelectMode,
                    text = if (isSelectMode) "취소" else "선택",
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }
    ) { innerPadding ->
        GridGalleryContent(
            medias = medias,
            headerText = headerText,
            sortOrder = sortOrder,
            onToggleSort = onToggleSort,
            showCalendarButton = showCalendarButton,
            onCalendarClick = onCalendarClick,
            onMediaClick = onMediaClick,
            topPadding = innerPadding.calculateTopPadding(),
            isSelectMode = isSelectMode,
            selectedIds = selectedIds,
            onToggleSelectMode = onToggleSelectMode,
            onToggleSelect = onToggleSelect,
            onLongClick = onLongClick,
            onSelectAll = onSelectAll,
            onDeleteSelected = onDeleteSelected,
            snackbarHostState = snackbarHostState,
            onHeaderPrevClick = null,
            onHeaderNextClick = null
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun GridGalleryScreenPreview() {
    LMTheme {
        GridGalleryScreen(
            medias = emptyList(),
            onCalendarClick = {},
            onMediaClick = {},
            onBackClick = {}
        )
    }
}
