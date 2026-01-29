package com.a602.commonproject.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold // 표준 Scaffold 사용
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.NavEntry
// ✨ 디자인 시스템 컴포넌트 임포트
import com.a602.commonproject.designsystem.component.LMNavigationBar
import com.a602.commonproject.designsystem.component.LMNavigationBarItem
import com.a602.commonproject.navigation.AlbumNavKey
import com.a602.commonproject.navigation.HomeNavKey
import com.a602.commonproject.navigation.MemoryNavKey
import com.a602.commonproject.navigation.MyPageNavKey
import com.a602.commonproject.navigation.Navigator
import com.a602.commonproject.navigation.TOP_LEVEL_NAV_ITEMS
import com.a602.commonproject.navigation.rememberNavigationState
import com.a602.commonproject.navigation.toEntries


@Composable
fun LTApp() {
    val navState = rememberNavigationState(
        startKey = HomeNavKey,
        topLevelKeys = TOP_LEVEL_NAV_ITEMS.keys
    )
    val navigator = remember(navState) { Navigator(navState) }

    // 1. NavigationSuiteScaffold 대신 표준 Scaffold 사용
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // 2. ✨ 사용자님이 만든 LMNavigationBar 적용
            LMNavigationBar {
                TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                    val isSelected = navState.currentTopLevelKey == navKey

                    // 3. ✨ 사용자님이 만든 LMNavigationBarItem 적용
                    LMNavigationBarItem(
                        selected = isSelected,
                        onClick = { navigator.navigate(navKey) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) navItem.selectedIcon else navItem.unselectedIcon,
                                contentDescription = stringResource(navItem.iconTextId),
                            )
                        },
                        label = { Text(stringResource(navItem.iconTextId)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val entries = navState.toEntries { key ->
            NavEntry(key) { route ->
                when (route) {
                    is HomeNavKey -> Text("메인 화면 (준비 중)")
                    is AlbumNavKey -> Text("메인 화면 (준비 중)")
                    is MemoryNavKey -> Text("메인 화면 (준비 중)")
                    is MyPageNavKey ->  Text("메인 화면 (준비 중)")
                    else -> Text("Unknown Route")
                }
            }
        }

        // 4. 화면 표시 영역 (하단 바 높이만큼 padding 적용)
        NavDisplay(
            entries= entries,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // ✨ 하단 바에 가려지지 않게 처리
            onBack = {
                try {
                    navigator.goBack()
                } catch (e: Exception) {
                    // 예외 처리
                }
            }
        )
    }
}

/*

// 1. User 더미 데이터
val dummyUser = User(
    id = "user_12345",
    email = "parent@example.com",
    nickname = "행복한엄마",
    profileImageUrl = "https://example.com/profiles/user_1.jpg" // 또는 null
)

// 2. Baby 더미 데이터
val dummyBaby = Baby(
    babyId = "baby_67890",
    babyName = "튼튼이",
    birthDate = "2023-05-20",
    gender = Baby.Gender.MALE,
    imageUrl = "https://example.com/babies/baby_1.jpg" // 또는 null
)

val items = listOf(
    TempKeywordDto("c1", "물건", "k1", "인형", 12),
    TempKeywordDto("c2", "음식", "k2", "밥", 20),
    TempKeywordDto("c3", "인물", "k3", "엄마", 30),
    TempKeywordDto("c4", "기념", "k4", "생일", 5),
    TempKeywordDto("c5", "여행", "k5", "바다", 50),
)

val dummyPolaroidData = PolaroidData(
    // 테스트용 빈 비트맵 생성 (1x1 크기)
    rearImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImageBitmap(),
    frontImage = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).asImageBitmap(),
    meta = PolaroidMeta(
        comment = "오늘 우리 아기랑 첫 산책 성공! 날씨가 너무 좋았어요.",
        date = "2024-05-20",
        role = "행복한엄마"
    )
)

val dummyPolaroidList: List<PolaroidData> = List(10) { index ->
    dummyPolaroidData.copy(
        // 만약 PolaroidMeta 등에 id가 있다면 index를 붙여서 고유하게 만듦
        meta = dummyPolaroidData.meta.copy(comment = "사진 $index")
    )
}

*/
