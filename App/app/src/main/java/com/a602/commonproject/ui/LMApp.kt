package com.a602.commonproject.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold // 표준 Scaffold 사용
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.a602.commonproject.designsystem.component.CameraButton
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.feature.gallery.GalleryNavKey
import com.a602.commonproject.feature.gallery.navigation.galleryEntries
import com.a602.commonproject.feature.memory.navigation.MemoryNavKey
import com.a602.commonproject.feature.memory.navigation.memoryEntries
import com.a602.commonproject.feature.mypage.navigation.myPageEntries
import com.a602.commonproject.feature.camera.navigation.CameraNavKey
import com.a602.commonproject.feature.camera.navigation.cameraEntries

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LMApp() {
    val appState = rememberLMAppState()
    // 1. NavigationSuiteScaffold 대신 표준 Scaffold 사용
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = background,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            // 2. ✨ 사용자님이 만든 LMNavigationBar 적용

            /*
            // [Backup] 애니메이션 없이 즉시 표시 (문제 발생 시 주석 해제하여 복구)
            if (appState.shouldShowBottomBar) {
                LMNavigationBar {
                    TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                        val isSelected = appState.navigationState.currentTopLevelKey == navKey
                        LMNavigationBarItem(
                            selected = isSelected,
                            onClick = { appState.navigator.navigate(navKey) },
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
            */

            // 애니메이션: 스플래시 화면이 사라질 때까지 100ms 기다렸다가 300ms 동안 서서히 나타남 (겹침 방지)
            androidx.compose.animation.AnimatedVisibility(
                visible = appState.shouldShowBottomBar,
                enter = androidx.compose.animation.fadeIn(
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300, delayMillis = 200)
                ),
                exit = androidx.compose.animation.fadeOut()
            ) {
                LMNavigationBar {
                    TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                        val isSelected = appState.navigationState.currentTopLevelKey == navKey

                        // 3. ✨ 사용자님이 만든 LMNavigationBarItem 적용
                        LMNavigationBarItem(
                            selected = isSelected,
                            onClick = { appState.navigator.navigate(navKey) },
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

            /*
            // [Backup] 애니메이션 없이 즉시 표시
            if (isTopLevelTab) {
                CameraButton(
                    onClick = { appState.navigator.navigate(CameraNavKey) },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            */

            androidx.compose.animation.AnimatedVisibility(
                visible = isTopLevelTab,
                enter = androidx.compose.animation.fadeIn(
                    animationSpec = androidx.compose.animation.core.tween(durationMillis = 300, delayMillis = 200)
                ) + androidx.compose.animation.scaleIn(initialScale = 0.8f),
                exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.scaleOut()
            ) {
                CameraButton(
                    onClick = { appState.navigator.navigate(CameraNavKey) },
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }
    ) { _ ->

        // 5. 모듈별 EntryProvider 연결
        val provider = entryProvider {
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
