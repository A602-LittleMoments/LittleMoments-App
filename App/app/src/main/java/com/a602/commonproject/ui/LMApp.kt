package com.a602.commonproject.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold // 표준 Scaffold 사용
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.a602.commonproject.feature.login.navigation.loginEntries
import com.a602.commonproject.designsystem.component.LMNavigationBar
import com.a602.commonproject.designsystem.component.LMNavigationBarItem
import com.a602.commonproject.feature.home.navigation.HomeNavKey
import com.a602.commonproject.feature.home.navigation.homeEntries
import com.a602.commonproject.navigation.TOP_LEVEL_NAV_ITEMS
import com.a602.commonproject.navigation.toEntries
import com.a602.commonproject.ui.rememberLMAppState
import com.a602.commonproject.designsystem.component.CameraButton
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.feature.gallery.GalleryNavKey
import com.a602.commonproject.feature.gallery.navigation.galleryEntries
import com.a602.commonproject.feature.memory.navigation.MemoryNavKey
import com.a602.commonproject.feature.memory.navigation.memoryEntries
import com.a602.commonproject.feature.mypage.navigation.myPageEntries
import com.a602.commonproject.feature.camera.navigation.CameraNavKey
import com.a602.commonproject.feature.camera.navigation.cameraEntries

@Composable
fun LMApp() {
    val appState = rememberLMAppState()
    // 1. NavigationSuiteScaffold 대신 표준 Scaffold 사용
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            // 2. ✨ 사용자님이 만든 LMNavigationBar 적용
            if (appState.shouldShowBottomBar) {
                LMNavigationBar {
                    TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                        val isSelected = appState.navigationState.currentTopLevelKey == navKey

                        // 3. ✨ 사용자님이 만든 LMNavigationBarItem 적용
                        LMNavigationBarItem(
                            selected = isSelected,
                            onClick = { appState.navigator.navigate(navKey as NavKey) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) navItem.selectedIcon else navItem.unselectedIcon,
                                    contentDescription = stringResource(navItem.iconTextId),
                                )
                            },
                            label = stringResource(navItem.iconTextId),
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // ✨ 홈, 앨범, 추억 탭에서만 FAB 표시
            // [Fix] currentTopLevelKey 대신 currentKey를 사용하여, 현재 '화면'이 탑 레벨일 때만 버튼이 나오도록 수정
            val currentKey = appState.navigationState.currentKey
            val isTopLevelTab = currentKey == HomeNavKey || currentKey == GalleryNavKey || currentKey == MemoryNavKey

            if (isTopLevelTab) {
                CameraButton(
                    onClick = { appState.navigator.navigate(CameraNavKey) },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }
    ) { innerPadding ->
        // 5. 모듈별 EntryProvider 연결
        val provider = entryProvider<NavKey> {
            loginEntries(
                navigator = appState.navigator,
                onLoginSuccess = {
                    // 로그인 성공 시 홈으로 이동하고, 백스택을 정리합니다 (뒤로가기 시 로그인 화면 안 나오게)
                    // replaceRoot를 사용하여 스택을 초기화하고 홈을 새로운 루트로 설정합니다.
                    appState.navigator.replaceRoot(HomeNavKey)
                }
            )
            homeEntries(appState.navigator)
            // Fallback / Placeholder for unimplemented features

            galleryEntries(appState.navigator)

            memoryEntries(appState.navigator)

            myPageEntries(appState.navigator)

            cameraEntries(appState.navigator)
        }

        val combinedEntryProvider: (NavKey) -> NavEntry<NavKey> = { key ->
            provider.invoke(key) ?: error("Unknown key: $key")
        }

        val entries = appState.navigationState.toEntries(combinedEntryProvider)

        // 4. 화면 표시 영역 (하단 바 높이만큼 padding 적용)
        NavDisplay(
            entries = entries,
            modifier = Modifier
                .fillMaxSize()
                // 💡 innerPadding 전체를 적용하지 않고, '하단(Bottom)' 패딩만 적용합니다.
                // 이렇게 하면 TopBar 영역(원래라면 비어있을 상단)까지 NavDisplay가 꽉 차게 됩니다.
                .padding(bottom = innerPadding.calculateBottomPadding()),
            onBack = { appState.navigator.goBack() },
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
