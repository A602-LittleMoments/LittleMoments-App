package com.a602.commonproject.feature.home.components

import Polaroid
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.model.data.SharedMedia
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.paging.compose.LazyPagingItems

fun LazyListScope.timelineSection(
    pagingItems: LazyPagingItems<SharedMedia>, // ✨ Paging Items 수신
    birthDate: String,
    onPhotoClick: () -> Unit,
) {
    if (pagingItems.itemCount > 0) {
        item {
            Text(
                text = "추억 타임라인",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    items(
        count = pagingItems.itemCount,
        key = { index ->
            // Paging3 Key: 아이템이 있으면 ID, 없으면(placeholder) index
            pagingItems.peek(index)?.id ?: index
        },
        contentType = { index ->
            if (pagingItems.peek(index) == null) "PLACEHOLDER" else "TIMELINE_ITEM"
        }
    ) { index ->
        val media = pagingItems[index]

        if (media != null) {
            // 날짜 헤더 표시 여부 계산
            // 1. 첫 번째 아이템이거나
            // 2. 이전 아이템과 날짜가 다르면 헤더 표시
            val isFirst = index == 0
            val showDate = if (isFirst) {
                true
            } else {
                val prevMedia = pagingItems.peek(index - 1)
                if (prevMedia != null) {
                    val currentDate = formatDate(media.dateTaken)
                    val prevDate = formatDate(prevMedia.dateTaken)
                    currentDate != prevDate
                } else {
                    true // 안전하게 표시
                }
            }
            
            // 날짜 문자열 계산
            val dateString = if (showDate) {
                 val dDayString = getDaysSinceBirth(birthDate, media.dateTaken)
                 val baseDate = formatDate(media.dateTaken)
                 if (dDayString.isNotEmpty()) "$baseDate $dDayString" else baseDate
            } else ""

            TimelineItem(
                media = media,
                showDate = showDate,
                dateString = dateString,
                isLastInGroup = false, // 그룹 개념이 모호해지므로 일단 false (필요 시 다음 아이템 비교)
                onPhotoClick = onPhotoClick
            )
            
            // 아이템 간 간격 (같은 날짜면 40dp, 아니면 좀 더 넒게?)
            // TimelineItem 내부에서 Spacer(40.dp)를 쓰고 있음.
            // 날짜가 바뀌는 지점(다음 아이템이 다른 날짜)이면 추가 여백을 줄 수도 있음.
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy.MM.dd (E)", java.util.Locale.KOREA)
    return sdf.format(java.util.Date(timestamp))
}

@Composable
fun TimelineItem(
    media: SharedMedia,
    showDate: Boolean,
    dateString: String,
    isLastInGroup: Boolean,
    onPhotoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(intrinsicSize = androidx.compose.foundation.layout.IntrinsicSize.Min) // 높이 맞춤
    ) {
        // 왼쪽 타임라인 선
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(20.dp)
                //.fillMaxHeight() // Row 높이에 맞춤
        ) {
            if (showDate) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.Gray)
                )
            } else {
                 // 점이 없을 때 위쪽 공간을 선으로 채워야 자연스럽게 연결됨?
                 // 아니면 그냥 선이 쭉 이어지면 됨.
                 // 첫 번째 아이템이 아니면 위에서 내려오는 선이 필요.
                 // 여기서는 그냥 weight로 채우는 게 나을 수 있음.
                 // 하지만 점 위치(10dp)를 고려해야 함.
                 // 점의 중심이 5dp. 상단에서 5dp 위치.
                 // 선은 width 2dp.
                 // 그냥 하나의 Box로 전체를 채우면 됨.
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(Color.LightGray)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 콘텐츠
        Column(modifier = Modifier.weight(1f)) {
            // 날짜 및 Info
            if (showDate) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = dateString,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // 폴라로이드 컴포넌트 사용
            Polaroid(
                media = media,
//                imageAspectRatio = 4f / 5f, // [UI Resize] 비율 4:5로 조정 (덜 길어보이게)
                modifier = Modifier
                    .fillMaxWidth(0.9f) // [UI Resize] 가로 폭 90%
                    .align(Alignment.CenterHorizontally) // [UI Align] 가운데 정렬
                    .clickable { onPhotoClick() } // [Navigation] 클릭 시 앨범 이동
            )

            // 같은 날짜 내의 아이템 간 간격
            if (!isLastInGroup) {
                Spacer(modifier = Modifier.height(40.dp)) // [UI Spacing] 간격 40dp
            }
        }
    }
}



private fun getDaysSinceBirth(birthDate: String, currentTimestamp: Long): String {
    return try {
        // birthDate format: "yyyy-MM-dd"
        val birthDateParsed = java.time.LocalDate.parse(birthDate)
        
        // Convert timestamp to LocalDate (System Default Zone)
        val imageDate = java.time.Instant.ofEpochMilli(currentTimestamp)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()

        val days = java.time.temporal.ChronoUnit.DAYS.between(birthDateParsed, imageDate)
        "D+${days + 1}" // D+1 meaning: Birth Day is Day 1
    } catch (e: Exception) {
        ""
    }
}
