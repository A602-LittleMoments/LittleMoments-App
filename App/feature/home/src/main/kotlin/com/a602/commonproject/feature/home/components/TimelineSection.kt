package com.a602.commonproject.feature.home.components

import Polaroid
import androidx.compose.foundation.background
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

@Composable
fun TimelineSection(
    mediaList: List<SharedMedia>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "추억 타임라인",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        mediaList.forEach { media ->
            TimelineItem(media = media)
        }
    }
}

@Composable
fun TimelineItem(media: SharedMedia) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            // .height(450.dp) // Polaroid handles its own height ratio
    ) {
        // 왼쪽 타임라인 선
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.Gray)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f) // Fills remaining space
                    .background(Color.LightGray)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 콘텐츠
        Column(modifier = Modifier.weight(1f)) {
            // 날짜 및 Info (Polaroid 내부에도 날짜가 있지만, 디자인상 타임라인 헤더 날짜도 있을 수 있음)
            // 여기서는 타임라인 헤더로서 날짜 표시
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = formatDate(media.dateTaken),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            // 폴라로이드 컴포넌트 사용
            Polaroid(
                media = media,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy.MM.dd (E)", Locale.KOREA)
    return sdf.format(Date(timestamp))
}
