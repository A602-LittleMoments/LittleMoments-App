package com.a602.commonproject.feature.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.R.drawable.temporary
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.feature.gallery.viewmodel.CalendarViewModel
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.model.data.SharedMedia.SyncStatus.SYNCED
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlinx.coroutines.launch

@Composable
fun CalendarRoute(
    onDateClick: () -> Unit,
    onGridClick: () -> Unit,
    onTempAlbumClick: () -> Unit,
    onHighLightClick: () -> Unit,
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
        )
    }
}

data class CalendarDay(
    val date: LocalDate?,
    val representativeThumbUrl: String? = null
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

        val imageUrl = representative?.remoteUrl

        days.add(CalendarDay(date, imageUrl))
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
fun CalendarMiniToolbar(
    onGridClick: () -> Unit,
    onTempAlbum: () -> Unit,
    onHighlight: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onGridClick) {
                Icon(
                    imageVector = Icons.Outlined.GridView,
                    contentDescription = "보기 전환"
                )
            }
            IconButton(onClick = onTempAlbum) {
                Icon(
                    imageVector = ImageVector.vectorResource(temporary),
                    contentDescription = "임시앨범"
                )
            }
            IconButton(onClick = onHighlight) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = "하이라이트"
                )
            }
        }

    }
}


@Composable
fun CalendarScreen(
    medias: List<SharedMedia>,
    initialMonth: YearMonth = YearMonth.now(),
    onDateClick: () -> Unit,
    onGridClick: () -> Unit,
    onTempAlbumClick: () -> Unit,
    onHighLightClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val baseMonth = remember { initialMonth }
    val centerPage = 100
    val pagerState = rememberPagerState(
        initialPage = centerPage,
        pageCount = { 200 }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 16.dp, vertical = 100.dp)
    ) {
        CalendarMiniToolbar(
            onGridClick = onGridClick,
            onTempAlbum = onTempAlbumClick,
            onHighlight = onHighLightClick
        )

        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(lightbackground, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {

            // 월 헤더 (<  >)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "<",
                    modifier = Modifier.clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                )

                val currentMonth = baseMonth.plusMonths(
                    pagerState.currentPage - centerPage.toLong()
                )
                Text(
                    "${currentMonth.year}년 ${currentMonth.monthValue}월",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    ">",
                    modifier = Modifier.clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                )
            }

            Spacer(Modifier.height(8.dp))

            // 좌우 스와이프
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
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
                    onDateClick = { onDateClick() }
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
    onDateClick: (LocalDate) -> Unit
) {
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
            val vSpace = (rowH * 0.30f).coerceIn(18.dp, 36.dp)

            val contentW = (cellW - hSpace).coerceAtLeast(0.dp)
            val contentH = (rowH - vSpace).coerceAtLeast(0.dp)

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

                        CalendarDayChip(
                            day = day,
                            onClick = { day.date?.let(onDateClick) }
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
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CalendarDayChip(
    day: CalendarDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current

    val chipSize = 52.dp
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .size(chipSize)
            .clip(CircleShape)
            .clickable(enabled = day.date != null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (day.representativeThumbUrl != null) {
            if (isPreview) {
                Box(Modifier.fillMaxSize().background(Color.LightGray))
            } else {
                AsyncImage(
                    model = day.representativeThumbUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(alpha = 0.85f),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Text(
            text = day.date?.dayOfMonth?.toString() ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = if (day.representativeThumbUrl != null) lightbackground else color4,
        )
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
            onHighLightClick = {}
        )
    }
}
