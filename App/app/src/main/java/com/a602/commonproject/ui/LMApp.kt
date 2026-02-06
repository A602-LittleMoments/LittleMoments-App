package com.a602.commonproject.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
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
import com.a602.commonproject.feature.login.navigation.LoginNavKey
import com.a602.commonproject.feature.login.navigation.SplashNavKey
import com.a602.commonproject.feature.login.navigation.SignUpNavKey
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.a602.commonproject.designsystem.component.ChangeStatusBarColor
import com.a602.commonproject.feature.album.HighlightResultNavKey

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LMApp(
    deepLinkUri: android.net.Uri? = null,
    onDeepLinkHandled: () -> Unit = {}
) {
    val appState = rememberLMAppState()
    val context = androidx.compose.ui.platform.LocalContext.current
    var backPressedTime by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0L) }

    // [Fix] Dynamic Status Bar Icon Color based on Route
    // Dark Background Screens (Home, Gallery, Baby, Camera) -> White Icons (isAppearanceLightStatusBars = false)
    // Light Background Screens (MyPage, Login, etc.) -> Black Icons (isAppearanceLightStatusBars = true)
    val currentKey = appState.navigationState.currentKey
    // [Fix] Added HighlightResultNavKey to use White Icons (Dark Theme)
    val useDarkIcons = when (currentKey) {
        MemoryNavKey, GalleryNavKey, GridNavKey, BabyNavKey, CameraNavKey, MyPageNavKey -> false
        else -> true
    }

    ChangeStatusBarColor(
        color = Color.Transparent,
        isAppearanceLightStatusBars = useDarkIcons
    )

    // 딥링크 처리: littlemoments://slideshow/{slideshowId}
    LaunchedEffect(deepLinkUri) {
        if (deepLinkUri != null) {
            val host = deepLinkUri.host
            val pathSegments = deepLinkUri.pathSegments

            when (host) {
                "slideshow" -> {
                    // littlemoments://slideshow/{slideshowId}
                    val slideshowId = pathSegments.firstOrNull()
                    if (!slideshowId.isNullOrBlank()) {
                        // 먼저 홈을 루트로 설정 (뒤로 가기 시 홈으로 돌아가도록)
                        appState.navigator.replaceRoot(MemoryNavKey)
                        // 그 후 상세 화면으로 이동
                        appState.navigator.navigate(HighlightResultNavKey(slideshowId))
                    }
                }
                // 다른 딥링크 호스트 처리 가능
            }
            onDeepLinkHandled()
        }
    }

    // [Fix] BackHandler로 백버튼 제어 (NavDisplay.onBack이 제대로 동작하지 않음)
    BackHandler {
        val currentKey = appState.navigationState.currentKey
        val isAuthScreen = currentKey == LoginNavKey || currentKey == SplashNavKey || currentKey == SignUpNavKey

        // 로그인/스플래시/회원가입 화면에서는 goBack 호출 없이 바로 앱 종료
        if (isAuthScreen) {
            (context as? android.app.Activity)?.finish()
            return@BackHandler
        }

        if (!appState.navigator.goBack()) {
            // Root에 도달했을 때 (더 이상 뒤로 갈 곳이 없음)
            val isMainTab = currentKey in TOP_LEVEL_NAV_ITEMS.keys || currentKey == MemoryNavKey

            if (isMainTab) {
                // 메인 탭에서는 두 번 눌러서 종료
                val currentTime = System.currentTimeMillis()
                if (currentTime - backPressedTime > 2000) {
                    backPressedTime = currentTime
                    android.widget.Toast.makeText(context, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    (context as? android.app.Activity)?.finish()
                }
            } else {
                // 다른 Root에서는 바로 종료
                (context as? android.app.Activity)?.finish()
            }
        }
    }

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
                onBack = {
                    val currentKey = appState.navigationState.currentKey
                    val isAuthScreen = currentKey == LoginNavKey || currentKey == SplashNavKey || currentKey == SignUpNavKey

                    // 로그인/스플래시/회원가입 화면에서는 goBack 호출 없이 바로 앱 종료
                    if (isAuthScreen) {
                        (context as? android.app.Activity)?.finish()
                        return@NavDisplay
                    }

                    if (!appState.navigator.goBack()) {
                        // Root에 도달했을 때 (더 이상 뒤로 갈 곳이 없음)
                        val isMainTab = currentKey in TOP_LEVEL_NAV_ITEMS.keys || currentKey == MemoryNavKey

                        if (isMainTab) {
                            // 메인 탭에서는 두 번 눌러서 종료
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - backPressedTime > 2000) {
                                backPressedTime = currentTime
                                android.widget.Toast.makeText(context, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                (context as? android.app.Activity)?.finish()
                            }
                        } else {
                            // 다른 Root에서는 바로 종료
                            (context as? android.app.Activity)?.finish()
                        }
                    }
                },
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
