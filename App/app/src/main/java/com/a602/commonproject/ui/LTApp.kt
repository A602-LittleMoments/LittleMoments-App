package com.a602.commonproject.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold // 표준 Scaffold 사용
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.NavEntry
// ✨ 디자인 시스템 컴포넌트 임포트
import com.a602.commonproject.designsystem.component.LMNavigationBar
import com.a602.commonproject.designsystem.component.LMNavigationBarItem
import com.a602.commonproject.feature.gallery.CalendarJanuary2026Preview
import com.a602.commonproject.feature.gallery.GridGallery
import com.a602.commonproject.feature.mypage.MyPageScreen
import com.a602.commonproject.navigation.*

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
                    is AlbumNavKey -> GridGallery(polaroids = emptyList())
                    is MemoryNavKey -> Text("추억 화면 (준비 중)")
                    is MyPageNavKey -> MyPageScreen()
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
