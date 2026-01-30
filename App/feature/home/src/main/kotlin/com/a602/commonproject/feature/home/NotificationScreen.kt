package com.a602.commonproject.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            LMTopAppBar(
                title = "알림",
                navigationIcon = LMicons.Back,
                onNavigationClick = onBackClick
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                NotificationHeader(title = "오늘")
            }
            items(getMockNotifications().filter { it.isToday }) { notification ->
                NotificationItem(notification)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                NotificationHeader(title = "이번주")
            }
             items(getMockNotifications().filter { !it.isToday }) { notification ->
                NotificationItem(notification)
            }
        }
    }
}

@Composable
fun NotificationHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun NotificationItem(item: NotificationUiModel) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF3E0)), // 연한 주황색
                contentAlignment = Alignment.Center
            ) {
               // 아이콘은 임시로 텍스트나 기본 아이콘 사용
                Text("🔔", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (item.body.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.body,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.timeAgo,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            if (item.hasImage) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier.size(48.dp).background(Color.LightGray, RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

data class NotificationUiModel(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val isToday: Boolean,
    val hasImage: Boolean = false
)

private fun getMockNotifications() = listOf(
    NotificationUiModel("1", "아이의 성장을 함께 볼 가족이 들어왔어요", "", "50분 전", true),
    NotificationUiModel("2", "지금 열어보면 웃게 될지도 몰라요", "짧은 순간들이 모여 하나의 추억이 됐어요", "2시간 전", true, true),
    NotificationUiModel("3", "아이와 함께한 지 N일째예요", "", "10시간 전", true),
    NotificationUiModel("4", "아이의 성장을 함께 볼 가족이 들어왔어요", "", "30분 전", false),
    NotificationUiModel("5", "지금 열어보면 웃게 될지도 몰라요", "", "2시간 전", false)
)

@Preview
@Composable
fun PreviewNotificationScreen() {
    NotificationScreen(onBackClick = {})
}
