package com.a602.commonproject.feature.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.component.LMNavigationDefaults.NavigationBarHeight
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.album.viewmodel.CalendarViewModel
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.model.data.SharedMedia.SyncStatus.SYNCED
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlinx.coroutines.launch

@Composable
fun CalendarRoute(
    onDateClick: (LocalDate) -> Unit,
    onGridClick: () -> Unit,
    onTempAlbumClick: () -> Unit,
    onHighLightClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> Text("불러오는 중…")
        uiState.error != null -> Text(uiState.error!!)
        else -> CalendarScreen(
            medias = uiState.medias,
            onDateClick = onDateClick,
            onGridClick = onGridClick,
            onTempAlbumClick = onTempAlbumClick,
            onHighLightClick = onHighLightClick,
            onMediaClick = onMediaClick,
        )
    }
}

data class CalendarDay(
    val date: LocalDate?,
    val representativeThumbUrl: String? = null,
    val representativeMediaId: String? = null
)

private fun weekRowCount(yearMonth: YearMonth): Int {
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val totalCells = firstDayOfWeek + yearMonth.lengthOfMonth()
    return (totalCells + 6) / 7
}

fun mapToCalendarDays(
    yearMonth: YearMonth,
    medias: List<SharedMedia>
): List<CalendarDay> {
    val grouped = medias.groupBy {
        Instant.ofEpochMilli(it.dateTaken)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    val days = mutableListOf<CalendarDay>()

    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

    // 앞 빈칸
    repeat(firstDayOfWeek) { days.add(CalendarDay(null)) }

    // 실제 날짜
    for (day in 1..yearMonth.lengthOfMonth()) {
        val date = yearMonth.atDay(day)
        val representative = grouped[date]?.firstOrNull()

        // remoteUrl을 대표 이미지로 사용
        val imageUrl = representative?.remoteUrl
        val mediaId = representative?.id

        days.add(CalendarDay(date, imageUrl, mediaId))
    }

    // 그 달이 필요한 "주(행)" 만큼까지만 채우기 (35 or 42)
    val rows = weekRowCount(yearMonth)
    val targetSize = rows * 7
    while (days.size < targetSize) {
        days.add(CalendarDay(null))
    }

    return days
}

@Composable
fun GalleryToggleRow(
    isCalendarMode: Boolean,
    onCalendarClick: () -> Unit,
    onGridClick: () -> Unit,
    onTempAlbum: () -> Unit,
    onHighlight: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Toggle Group: [Calendar | Grid]
        Row(
            modifier = Modifier
                .height(48.dp)
                .background(com.a602.commonproject.designsystem.theme.background.copy(alpha = 0.9f), CircleShape)
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Calendar Button
            GalleryToggleButton(
                isActive = isCalendarMode,
                onClick = onCalendarClick,
                icon = LMicons.Calendar,
                description = "캘린더"
            )

            Spacer(Modifier.width(2.dp))

            // Grid Button
            GalleryToggleButton(
                isActive = !isCalendarMode,
                onClick = onGridClick,
                icon = Icons.Default.GridView,
                description = "그리드"
            )
        }

        // Right Action Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Temp Album Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(com.a602.commonproject.designsystem.theme.background.copy(alpha = 0.9f), CircleShape)
                    .clickable(onClick = onTempAlbum),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.temporary),
                    contentDescription = "임시 앨범",
                    tint = com.a602.commonproject.designsystem.theme.main,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun GalleryToggleButton(
    isActive: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    description: String
) {
    val backgroundColor = if (isActive) com.a602.commonproject.designsystem.theme.main else Color.Transparent
    val iconColor = if (isActive) Color.White else Color.Gray

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun CalendarScreen(
    medias: List<SharedMedia>,
    initialMonth: YearMonth = YearMonth.now(),
    onDateClick: (LocalDate) -> Unit,
    onGridClick: () -> Unit,
    onTempAlbumClick: () -> Unit,
    onHighLightClick: () -> Unit,
    onMediaClick: (SharedMedia) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val baseMonth = remember { initialMonth }
    val centerPage = 100
    val pagerState = rememberPagerState(
        initialPage = centerPage,
        pageCount = { 200 }
    )

    // Local state for view mode
    var isCalendarMode by rememberSaveable { mutableStateOf(true) }

    // 배경 이미지 리소스
    val backgroundImage = R.drawable.gallery_background

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background Image (Full Screen)
            Image(
                painter = painterResource(id = backgroundImage),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                // 2. Body with Starry Background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {

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
                    .padding(horizontal = 16.dp)
            ) {
                // 상단 버튼과 위의 거리
                Spacer(Modifier.height(24.dp))

                GalleryToggleRow(
                    isCalendarMode = isCalendarMode,
                    onCalendarClick = { isCalendarMode = true },
                    onGridClick = { isCalendarMode = false },
                    onTempAlbum = onTempAlbumClick,
                    onHighlight = onHighLightClick
                )

                Spacer(Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .navigationBarsPadding()
                        .padding(bottom = NavigationBarHeight + 16.dp) // Clear the App Bottom Bar
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                        .padding(12.dp)
                ) {
                    if (isCalendarMode) {
                        // --- Calendar View ---
                        Column(modifier = Modifier.fillMaxSize()
                            .background(background.copy(alpha = 0.1f), RoundedCornerShape(24.dp)) // 안쪽 살짝 하얀 배경 추가
                            .clip(RoundedCornerShape(24.dp)))
                        {
                             // 월 헤더 (<  >)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowLeft,
                                        contentDescription = "Previous Month",
                                        tint = Color.White
                                    )
                                }

                                val currentMonth = baseMonth.plusMonths(
                                    pagerState.currentPage - centerPage.toLong()
                                )
                                Text(
                                    text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp
                                    ),
                                    color = Color.White
                                )

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Next Month",
                                        tint = Color.White
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // 좌우 스와이프
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) { page ->

                                val month = baseMonth.plusMonths(page - centerPage.toLong())
                                val rows = weekRowCount(month)
                                val days = remember(month, medias) {
                                    mapToCalendarDays(month, medias)
                                }

                                CalendarPhotoViewFill(
                                    days = days,
                                    rows = rows,
                                    modifier = Modifier.fillMaxSize(),
                                    onDateClick = { date, _ ->
                                        onDateClick(date)
                                    }
                                )
                            }
                        }
                    } else {
                        // --- Grid View (Date Grouped) ---
                        Column(modifier = Modifier.fillMaxSize()
                            .background(background.copy(alpha = 0.1f), RoundedCornerShape(24.dp)) // 안쪽 살짝 하얀 배경 추가
                            .clip(RoundedCornerShape(24.dp)))
                        {
                            DateGroupedGridView(
                                medias = medias,
                                onMediaClick = onMediaClick
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

@Composable
fun DateGroupedGridView(
    medias: List<SharedMedia>,
    onMediaClick: (SharedMedia) -> Unit
) {
    // Group medias by date string (e.g. "2026.02.02")
    val grouped = remember(medias) {
        medias.groupBy {
            val date = Instant.ofEpochMilli(it.dateTaken)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            "${date.year}.${String.format("%02d", date.monthValue)}.${String.format("%02d", date.dayOfMonth)}"
        }
    }

    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .simpleVerticalScrollbar(listState),
        verticalArrangement = Arrangement.spacedBy(16.dp), // 사진 간 세로 간격
        horizontalArrangement = Arrangement.spacedBy(12.dp), // 사진 간 가로 간격
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 12.dp, end = 12.dp, top = 16.dp, bottom = 24.dp 
        ) // 안쪽 영역에 닿지 않게 충분한 여백
    ) {
        grouped.forEach { (dateHeader, dateMedias) ->
            // Header Item
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(3) }) {
                Text(
                    text = dateHeader,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 16.dp, bottom = 8.dp) // 날짜를 오른쪽으로 조금 더 이동
                )
            }

            items(dateMedias) { media ->
                com.a602.coommonproject.ui.FramelessPhotoItem(
                    media = media,
                    onClick = { onMediaClick(media) } // 🚀 [FIX] Removed item padding
                )
            }
        }
    }
}

@Composable
fun CalendarPhotoViewFill(
    days: List<CalendarDay>,
    rows: Int,
    modifier: Modifier = Modifier,
    onDateClick: (LocalDate, String?) -> Unit) {
    Column(modifier = modifier.fillMaxWidth()) {
        DayOfWeekHeader()
        Spacer(modifier = Modifier.height(8.dp))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val cellW = maxWidth / 7
            val rowH = maxHeight / rows

            val hSpace = (cellW * 0.18f).coerceIn(10.dp, 22.dp)
            val vSpace = (rowH * 0.15f).coerceIn(8.dp, 20.dp) // Reduced factor and range for tighter vertical spacing

            val contentW = (maxWidth - hSpace * 6) / 7
            val contentH = (maxHeight - vSpace * (rows - 1)) / rows

            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(7),
                horizontalArrangement = Arrangement.spacedBy(hSpace),
                verticalArrangement = Arrangement.spacedBy(vSpace),
                userScrollEnabled = false
            ) {
                items(days) { day ->
                    Box(
                        modifier = Modifier
                            .width(contentW)
                            .height(contentH),
                        contentAlignment = Alignment.Center
                    ) {
                        val ChipSize = minOf(52.dp, contentW, contentH)

                        CalendarDayChip(
                            day = day,
                            chipSize = ChipSize,
                            onClick = { day.date?.let { onDateClick(it, day.representativeMediaId) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayOfWeekHeader(modifier: Modifier = Modifier) {
    val days = listOf("일", "월", "화", "수", "목", "금", "토")
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CalendarDayChip(day: CalendarDay, chipSize: Dp,
                    onClick: () -> Unit,
                    modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current

    // Only apply click if there is a photo
    val isPhotoAvailable = day.representativeThumbUrl != null

    Box(
        modifier = modifier
            .size(chipSize)
            // Add subtle shadow if it's a photo
            .let { if (isPhotoAvailable) it.shadow(4.dp, CircleShape) else it }
            .clip(CircleShape)
            // If photo: white border backing. If empty: no background
            .background(if (isPhotoAvailable) Color.White else Color.Transparent)
            .clickable(enabled = isPhotoAvailable, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isPhotoAvailable) {
            // Inner image (slightly smaller to show border)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp) // Border thickness
                    .clip(CircleShape)
            ) {
                 if (isPreview) {
                    Box(Modifier.fillMaxSize().background(Color.LightGray))
                } else {
                    AsyncImage(
                        model = day.representativeThumbUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(alpha = 0.9f),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Date Text
        if (day.date != null) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    // If photo exists, use white text with shadow for contrast. Else just white.
                    shadow = if (isPhotoAvailable) androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        blurRadius = 4f
                    ) else null
                ),
                // Text color is always white in Space theme for visibility against dark glass or photo
                color = if (isPhotoAvailable) Color.White else Color.White.copy(alpha = 0.7f),
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val sampleMedias = listOf(
        SharedMedia(
            id = "1",
            type = SharedMedia.MediaType.PHOTO,
            localUri = null,
            caption = null,
            remoteUrl = "https://via.placeholder.com/300",
            thumbnailUrl = null,
            dateTaken = System.currentTimeMillis(),
            orientation = 0,
            uploaderName = "엄마",
            syncStatus = SYNCED
        )
    )

    LMTheme() {

        CalendarScreen(
            medias = sampleMedias,
            initialMonth = YearMonth.now(),
            onDateClick = {},
            onGridClick = {},
            onTempAlbumClick = {},
            onHighLightClick = {},
            onMediaClick = {}
        )
    }
}

fun Modifier.simpleVerticalScrollbar(
    state: LazyGridState,
    width: Dp = 4.dp
): Modifier = composed {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration),
        label = "ScrollbarAlpha"
    )

    drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val elementCount = state.layoutInfo.totalItemsCount
        val visibleCount = state.layoutInfo.visibleItemsInfo.size

        if (alpha > 0f && elementCount > visibleCount && firstVisibleElementIndex != null) {
            val scrollbarHeight = size.height * (visibleCount.toFloat() / elementCount.toFloat())
            val scrollbarOffsetY = size.height * (firstVisibleElementIndex.toFloat() / elementCount.toFloat())

            val minScrollbarHeight = 20.dp.toPx()
            val finalHeight = scrollbarHeight.coerceAtLeast(minScrollbarHeight)

            drawRoundRect(
                color = Color.White.copy(alpha = 0.8f),
                topLeft = Offset(size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), finalHeight),
                alpha = alpha,
                cornerRadius = CornerRadius(width.toPx())
            )
        }
    }
}
