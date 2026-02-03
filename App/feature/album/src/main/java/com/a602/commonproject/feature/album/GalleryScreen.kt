package com.a602.commonproject.feature.album

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.component.FillWrapButton
import com.a602.commonproject.designsystem.component.LMNavigationDefaults.NavigationBarHeight
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.feature.album.viewmodel.CalendarViewModel
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.model.data.SharedMedia.SyncStatus.SYNCED
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@Composable
fun CalendarRoute(
    onBack: () -> Unit,
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
            onBack=onBack,
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

        // remoteUrl을 대표 이미지로 사용
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

// Placeholder - I will look up GridRoute file next.
@Composable
fun CalendarScreen(
    medias: List<SharedMedia>,
    initialMonth: YearMonth = YearMonth.now(),
    onBack: () -> Unit,
    onDateClick: () -> Unit,
    onGridClick: () -> Unit,
    onTempAlbumClick: () -> Unit,
    onHighLightClick: () -> Unit,
) {
    var currentMonth by remember { mutableStateOf(initialMonth) }

    val rows: Int = remember(currentMonth) { weekRowCount(currentMonth) }

    val days = remember(currentMonth, medias) {
        mapToCalendarDays(currentMonth, medias)
    }

    Column(
        modifier = Modifier
            .background(background)
            .fillMaxSize()
            .padding(bottom = NavigationBarHeight)
            .navigationBarsPadding()
    ) {
        LMTopAppBar(
            title = "캘린더",
            navigationIcon = LMicons.Back,
            onNavigationClick = onBack,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    FillWrapButton(
                        text = "그리드 보기",
                        onClick = onGridClick,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                        .background(lightbackground, shape = RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "<",
                            Modifier.clickable { currentMonth = currentMonth.minusMonths(1) },
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            "${currentMonth.year}년 ${currentMonth.monthValue}월",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            ">",
                            Modifier.clickable { currentMonth = currentMonth.plusMonths(1) },
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CalendarPhotoView(
                        days = days,
                        rows = rows,
                        onDateClick = { onDateClick() }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    FillWrapButton(
                        text = "임시앨범",
                        onClick = onTempAlbumClick,
                    )

                    FillWrapButton(
                        text = "하이라이트 생성",
                        onClick = onHighLightClick,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarPhotoView(
    days: List<CalendarDay>,
    rows: Int, // 5 or 6
    onDateClick: (LocalDate) -> Unit
) {
    val vSpace = 12.dp
    val hSpace = 8.dp

    Column {
        DayOfWeekHeader()
        Spacer(modifier = Modifier.height(4.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cellSize = (maxWidth - hSpace * 6) / 7
            val oneRowHeight = cellSize + vSpace
            val fixedGridHeight = cellSize * 6 + vSpace * 5

            val missing = (6 - rows).coerceAtLeast(0)
            val offsetY = (missing * oneRowHeight) / 2

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(fixedGridHeight)
            ) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offsetY),
                    columns = GridCells.Fixed(7),
                    verticalArrangement = Arrangement.spacedBy(vSpace),
                    horizontalArrangement = Arrangement.spacedBy(hSpace),
                    userScrollEnabled = false
                ) {
                    items(days) { day ->
                        CalendarDayItem(
                            day = day,
                            onClick = { day.date?.let { onDateClick(it) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayOfWeekHeader() {
    val days = listOf("일", "월", "화", "수", "목", "금", "토")

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        userScrollEnabled = false
    ) {
        items(days) { day ->
            Text(
                text = day,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CalendarDayItem(
    day: CalendarDay,
    onClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clickable(enabled = day.date != null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (day.representativeThumbUrl != null) {
            if (isPreview) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            } else {
                AsyncImage(
                    model = day.representativeThumbUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .graphicsLayer(alpha = 0.85f),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Text(
            text = day.date?.dayOfMonth?.toString() ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = if (day.representativeThumbUrl != null) lightbackground else color3,
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
            remoteUrl = "https://via.placeholder.com/300", // ✅ remoteUrl 사용
            thumbnailUrl = null, // ✅ 없어도 됨
            dateTaken = System.currentTimeMillis(),
            orientation = 0,
            uploaderName = "엄마",
            syncStatus = SYNCED
        )
    )

    LMTheme() {
        Surface {
            CalendarScreen(
                medias = sampleMedias,
                initialMonth = YearMonth.now(),
                onBack = {},
                onDateClick = {},
                onGridClick = {},
                onTempAlbumClick = {},
                onHighLightClick = {}
            )
        }
    }
}
