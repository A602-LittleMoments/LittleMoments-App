package com.a602.commonproject.feature.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.navigation3.runtime.NavKey
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.theme.NiaTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.model.data.SharedMedia.SyncStatus.SYNCED
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

data class CalendarDay(
    val date: LocalDate?,
    val representativeThumbUrl: String? = null
)

fun mapToCalendarDays(
    yearMonth: YearMonth,
    medias: List<SharedMedia>
): List<CalendarDay> {

    // 날짜별 그룹핑
    val grouped = medias.groupBy {
        Instant.ofEpochMilli(it.dateTaken)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    val days = mutableListOf<CalendarDay>()

    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

    // 앞 빈칸
    repeat(firstDayOfWeek) {
        days.add(CalendarDay(date = null))
    }

    // 실제 날짜
    for (day in 1..yearMonth.lengthOfMonth()) {
        val date = yearMonth.atDay(day)
        val representative = grouped[date]?.firstOrNull()

        days.add(
            CalendarDay(
                date = date,
                representativeThumbUrl = representative?.thumbnailUrl
            )
        )
    }

    return days
}

@Composable
fun CalendarScreen(
    medias: List<SharedMedia>,
    initialMonth: YearMonth = YearMonth.now(),
    onDateClick: (LocalDate) -> Unit,
) {
    var currentMonth by remember { mutableStateOf(initialMonth) }

    val days = remember(currentMonth, medias) {
        mapToCalendarDays(currentMonth, medias)
    }
    Column(
        modifier = Modifier
            .background(background)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        lightbackground,
                        shape = RoundedCornerShape(20.dp)
                    ).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 월 이동 헤더]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("<", Modifier.clickable { currentMonth = currentMonth.minusMonths(1) }, style = MaterialTheme.typography.headlineLarge)
                    Text("${currentMonth.year}년 ${currentMonth.monthValue}월", style = MaterialTheme.typography.headlineLarge)
                    Text(">", Modifier.clickable { currentMonth = currentMonth.plusMonths(1) }, style = MaterialTheme.typography.headlineLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))

                CalendarPhotoView(days = days, onDateClick = onDateClick)
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun CalendarPhotoView(
    days: List<CalendarDay>,
    onDateClick: (LocalDate) -> Unit
) {
    Column {
        DayOfWeekHeader()

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(days) { day ->
                CalendarDayItem(
                    day = day,
                    onClick = {
                        day.date?.let { onDateClick(it) }
                    }
                )
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
                // 프리뷰용 원형
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
            remoteUrl = "https://via.placeholder.com/150",
            thumbnailUrl = "https://via.placeholder.com/150",
            dateTaken = System.currentTimeMillis(),
            orientation = 0,
            uploaderName = "엄마",
            syncStatus = SYNCED
        )
    )
    NiaTheme() {
        Surface {
            CalendarScreen(
                medias = sampleMedias,
                initialMonth = YearMonth.now(),
                onDateClick = {}
            )
        }
    }
}
