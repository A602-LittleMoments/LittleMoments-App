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
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.shadow
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
    // 날짜별 그룹핑
    val now = System.currentTimeMillis()
    val todayNotifications = notifications.filter {
        DateUtils.isToday(it.timestamp)
    }
    // "이번주": 오늘이 아니면서 최근 7일 이내
    val weekNotifications = notifications.filter {
        !DateUtils.isToday(it.timestamp) && (now - it.timestamp < DateUtils.WEEK_IN_MILLIS)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)
    ) {
        if (todayNotifications.isNotEmpty()) {
            item {
                NotificationHeader(title = "오늘")
            }
            items(todayNotifications) { notification ->
                NotificationItem(notification)
            }
        }

        if (weekNotifications.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                NotificationHeader(title = "이번주")
            }
            items(weekNotifications) { notification ->
                NotificationItem(notification)
            }
        }
    }
}

@Composable
fun NotificationHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp, // 조금 더 키움
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6D625E), // color3 (Text 메인)
        modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun NotificationItem(item: NotificationEntity) {
    // 알림 타입에 따른 스타일 분기
    // 예시 로직: DB에 저장된 type 문자열에 따라 아이콘/색상 결정
    // type이 null이면 기본값(종 아이콘) 사용
    val isHighlightOrSlideshow = item.type == "HIGHLIGHT" || item.type == "SLIDESHOW" || item.type == "LOVE"
    val isNewMemory = item.type == "NEW_ALBUM" || item.type == "MEMORY" || item.type == "FAMILY"

    // 기본값: 노란 종
    var iconVector = LMicons.Notifications_Filled
    var iconTint = com.a602.commonproject.designsystem.theme.PointYellow
    // 배경은 아이콘 색의 아주 연한 버전 (여기서는 임의로 정하거나 테마 색상 활용)
    var iconBgColor = Color(0xFFFFFAD6) // 연한 노랑

    if (isNewMemory) {
        // 파란 배지
        iconVector = LMicons.New_Badge
        iconTint = com.a602.commonproject.designsystem.theme.NavyBlue
        iconBgColor = com.a602.commonproject.designsystem.theme.lightblue
    } else {
         // 그 외 (Highlight 포함 기본)
         iconVector = LMicons.Notifications_Filled
         iconTint = com.a602.commonproject.designsystem.theme.PointYellow
         iconBgColor = Color(0xFFFFF8E1) 
    }

    // 시간 포맷팅
    val timeFormat = java.text.SimpleDateFormat("a h:mm", java.util.Locale.KOREA)
    val formattedTime = timeFormat.format(java.util.Date(item.timestamp))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = com.a602.commonproject.designsystem.theme.lightbackground), // OffWhite
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // 그림자는 내부 아이콘에만? 혹은 카드 자체에도? 이미지는 카드 자체는 플랫해보임.
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable {
                // TODO: 딥링크 처리 또는 상세 이동
            }
            .shadow(
                 elevation = 2.dp,
                 shape = RoundedCornerShape(16.dp),
                 spotColor = Color.Black.copy(alpha = 0.1f),
                 ambientColor = Color.Black.copy(alpha = 0.1f)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘 박스 (그림자 포함)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(alpha = 0.2f),
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .clip(CircleShape)
                    .background(Color.White), // 아이콘 배경은 흰색? 이미지상 흰색 원 안에 아이콘이 있는 듯 함.
                contentAlignment = Alignment.Center
            ) {
                 // 실제 아이콘
                 Icon(
                     imageVector = iconVector,
                     contentDescription = null,
                     tint = iconTint,
                     modifier = Modifier.size(24.dp)
                 )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // 제목 (한 줄 제한) + 시간 (우측 정렬)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false) // 제목이 너무 길면 줄어들도록
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedTime,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                // 내용 (한 줄 제한)
                if (item.body.isNotEmpty()) {
                    Text(
                        text = item.body,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PreviewNotificationItem() {
    com.a602.commonproject.designsystem.theme.LMTheme {
        Column(modifier = Modifier.padding(10.dp)) {
            NotificationItem(
                item = NotificationEntity(
                    id = "1",
                    title = "새로운 추억이 도착했습니다",
                    body = "아이와의 소중한 순간을 확인해보세요.",
                    timestamp = System.currentTimeMillis(),
                    type = "MEMORY",
                    isRead = false
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            NotificationItem(
                item = NotificationEntity(
                    id = "2",
                    title = "하이라이트 생성 완료",
                    body = "멋진 하이라이트 영상이 만들어졌어요!",
                    timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                    type = "HIGHLIGHT",
                    isRead = true
                )
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PreviewNotificationScreen() {
    com.a602.commonproject.designsystem.theme.LMTheme {
        val now = System.currentTimeMillis()
        val dummyNotifications = listOf(
            // 오늘
            NotificationEntity(id = "1", title = "오늘의 추억", body = "오늘 찍은 사진을 확인하세요.", timestamp = now - 3600000, type = "MEMORY", isRead = false),
            // 이번주 (2일 전)
            NotificationEntity(id = "2", title = "이번주 하이라이트", body = "멋진 영상이 준비되었습니다.", timestamp = now - (2 * 86400000), type = "HIGHLIGHT", isRead = true),
            // 이전 알림 (10일 전)
            NotificationEntity(id = "3", title = "오래된 알림", body = "지난 추억을 되돌아보세요.", timestamp = now - (10 * 86400000), type = "FAMILY", isRead = true)
        )
        
        Scaffold(
            topBar = {
                LMTopAppBar(title = "알림", navigationIcon = LMicons.Back, onNavigationClick = {})
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NotificationList(notifications = dummyNotifications)
            }
        }
    }
}
