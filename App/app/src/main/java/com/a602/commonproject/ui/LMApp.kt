package com.a602.commonproject.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold // 표준 Scaffold 사용
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.a602.commonproject.feature.login.navigation.loginEntries
import com.a602.commonproject.designsystem.component.LMNavigationBar
import com.a602.commonproject.designsystem.component.LMNavigationBarItem
import com.a602.commonproject.feature.baby.navigation.babyEntries // NEW
import com.a602.commonproject.navigation.TOP_LEVEL_NAV_ITEMS
import com.a602.commonproject.navigation.toEntries
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.feature.album.GalleryNavKey // UPDATED // UPDATED
import com.a602.commonproject.feature.home.navigation.MemoryNavKey // UPDATED
import com.a602.commonproject.feature.home.navigation.memoryEntries // UPDATED
import com.a602.commonproject.feature.mypage.navigation.myPageEntries
import com.a602.commonproject.feature.mypage.navigation.MyPageNavKey
import com.a602.commonproject.feature.baby.navigation.BabyNavKey
import com.a602.commonproject.feature.camera.navigation.CameraNavKey
import com.a602.commonproject.feature.camera.navigation.cameraEntries
import com.a602.commonproject.feature.album.GridNavKey
import com.a602.commonproject.feature.album.navigation.galleryEntries
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.a602.commonproject.designsystem.component.ChangeStatusBarColor

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LMApp() {
    val appState = rememberLMAppState()

    // [Fix] Dynamic Status Bar Icon Color based on Route
    // Dark Background Screens (Home, Gallery, Baby, Camera) -> White Icons (isAppearanceLightStatusBars = false)
    // Light Background Screens (MyPage, Login, etc.) -> Black Icons (isAppearanceLightStatusBars = true)
    val currentKey = appState.navigationState.currentKey
    val useDarkIcons = when (currentKey) {
        MemoryNavKey, GalleryNavKey, GridNavKey, BabyNavKey, CameraNavKey, MyPageNavKey -> false
        else -> true
    }

    ChangeStatusBarColor(
        color = Color.Transparent,
        isAppearanceLightStatusBars = useDarkIcons
    )

    // [Fix] Layout Refactor: Use Box to overlay NavigationBar over content
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. NavigationSuiteScaffold 대신 표준 Scaffold 사용
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
                // .navigationBarsPadding() // [Fix] Removed to allow content behind nav bar
                // .statusBarsPadding(), // [Fix] Removed to allow content behind status bar (Edge-to-Edge)
            containerColor = background,
            floatingActionButtonPosition = FabPosition.Center,
            // bottomBar = { ... } // [Fix] Removed from Scaffold, moved to Box alignment
            floatingActionButton = {
                // ✨ 홈, 앨범, 추억 탭에서만 FAB 표시
                // ... (existing commented out code)
            }
        ) { _ ->

            // 5. 모듈별 EntryProvider 연결
            val provider = entryProvider {
                loginEntries(
                    navigator = appState.navigator,
                    onLoginSuccess = {
                        // 로그인 성공 시 홈(New Home = Memory)으로 이동
                        appState.navigator.replaceRoot(MemoryNavKey)
                    }
                )
                babyEntries(appState.navigator) // 구 Home -> Baby
                // Fallback / Placeholder for unimplemented features

                galleryEntries(appState.navigator) // 구 Gallery -> Album

                memoryEntries(appState.navigator) // 구 Memory -> Home

                myPageEntries(appState.navigator)

                cameraEntries(appState.navigator)
            }

            val combinedEntryProvider: (NavKey) -> NavEntry<NavKey> = { key ->
                provider.invoke(key)
            }

            val entries = appState.navigationState.toEntries(combinedEntryProvider)

            // 4. 화면 표시 영역 (하단 바 높이만큼 padding 적용)
            NavDisplay(
                entries = entries,
                modifier = Modifier
                    .fillMaxSize(),

                    // 💡 [Fix] Global padding removed. Padding is applied via wrapper above.
                onBack = { appState.navigator.goBack() },
            )
        }

        // [Fix] Navigation Bar overlaid at bottom
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            // 애니메이션: 스플래시 화면이 사라질 때까지 100ms 기다렸다가 300ms 동안 서서히 나타남 (겹침 방지)
            AnimatedVisibility(
                visible = appState.shouldShowBottomBar,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 300, delayMillis = 200)
                ),
                exit = fadeOut()
            ) {
                LMNavigationBar {
                    TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                        // [Fix] Grid 화면이어도 갤러리 탭이 선택된 것으로 표시
                        val isSelected = appState.navigationState.currentTopLevelKey == navKey ||
                                (navKey == GalleryNavKey && appState.navigationState.currentTopLevelKey == GridNavKey)

                        // 3. ✨ 사용자님이 만든 LMNavigationBarItem 적용
                        LMNavigationBarItem(
                            selected = isSelected,
                            onClick = { appState.navigator.navigate(navKey) },
                            painter = painterResource(id = if (isSelected) navItem.selectedIcon else navItem.unselectedIcon),
                            label = stringResource(navItem.iconTextId),
                        )
                    }
                }
            }
        }
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
