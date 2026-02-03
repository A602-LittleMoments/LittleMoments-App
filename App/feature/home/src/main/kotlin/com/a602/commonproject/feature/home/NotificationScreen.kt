package com.a602.commonproject.feature.home

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a602.commonproject.database.model.NotificationEntity
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.feature.home.viewmodel.NotificationUiState
import com.a602.commonproject.feature.home.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            LMTopAppBar(
                title = "알림",
                navigationIcon = LMicons.Back,
                onNavigationClick = onBackClick
            )
        },
        containerColor = background
    ) { paddingValues ->
        // Status Bar Customization for this screen
        val view = androidx.compose.ui.platform.LocalView.current
        if (!view.isInEditMode) {
            androidx.compose.runtime.DisposableEffect(Unit) {
                val window = (view.context as android.app.Activity).window
                val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, view)

                // Save original values
                val originalStatusBarColor = window.statusBarColor
                val originalAppearanceLightStatusBars = insetsController.isAppearanceLightStatusBars

                // Set new values for NotificationScreen: Light background (Ivory/White) + Dark Icons
                window.statusBarColor = android.graphics.Color.parseColor("#FFFAEB") // IvoryCream
                insetsController.isAppearanceLightStatusBars = true

                onDispose {
                    // Restore original values (Global Theme: NavyBlue + Light Icons)
                    window.statusBarColor = originalStatusBarColor
                    insetsController.isAppearanceLightStatusBars = originalAppearanceLightStatusBars
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is NotificationUiState.Loading -> {
                   Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                       CircularProgressIndicator()
                   }
                }
                is NotificationUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("새로운 알림이 없습니다.", color = Color.Gray)
                    }
                }
                is NotificationUiState.Success -> {
                    NotificationList(notifications = state.notifications)
                }
            }
        }
    }
}

@Composable
fun NotificationList(notifications: List<NotificationEntity>) {
    // 날짜별 그룹핑 (오늘 / 그 외)
    val now = System.currentTimeMillis()
    val todayNotifications = notifications.filter {
        DateUtils.isToday(it.timestamp)
    }
    val otherNotifications = notifications.filter {
        !DateUtils.isToday(it.timestamp)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        if (todayNotifications.isNotEmpty()) {
            item {
                NotificationHeader(title = "오늘")
            }
            items(todayNotifications) { notification ->
                NotificationItem(notification)
            }
        }

        if (otherNotifications.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                NotificationHeader(title = "이전 알림")
            }
            items(otherNotifications) { notification ->
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
fun NotificationItem(item: NotificationEntity) {
    val timeAgo = DateUtils.getRelativeTimeSpanString(
        item.timestamp,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable {
                // TODO: 딥링크 처리 또는 상세 이동
            }
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
                    text = timeAgo,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
