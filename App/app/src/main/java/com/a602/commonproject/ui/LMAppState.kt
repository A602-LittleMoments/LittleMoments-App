package com.a602.commonproject.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.navigation.HomeNavKey
import com.a602.commonproject.navigation.NavigationState
import com.a602.commonproject.navigation.Navigator
import com.a602.commonproject.navigation.TOP_LEVEL_NAV_ITEMS
import com.a602.commonproject.navigation.rememberNavigationState
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberLMAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    // 1. [변경] Route.Home 대신 TopLevelNavItem.kt에 정의된 HomeNavKey 사용
    startDestination: NavKey = HomeNavKey,
    // 2. [변경] 맵에서 키 목록(Set<NavKey>)을 가져와서 설정
    topLevelDestinations: Set<NavKey> = TOP_LEVEL_NAV_ITEMS.keys
): MyAppState {

    // NavigationState 생성 (NavKey 기반)
    val navigationState = rememberNavigationState(
        startKey = startDestination,
        topLevelKeys = topLevelDestinations
    )

    // Navigator 생성 (로직 위임)
    val navigator = remember(navigationState) {
        Navigator(navigationState)
    }

    return remember(navigationState, navigator, coroutineScope) {
        MyAppState(
            navigationState = navigationState,
            navigator = navigator,
            coroutineScope = coroutineScope
        )
    }
}

@Stable
class MyAppState(
    val navigationState: NavigationState,
    val navigator: Navigator,
    val coroutineScope: CoroutineScope,
) {
    // 1. 바텀 바 표시 여부
    // 현재 보고 있는 화면(currentKey)이 TOP_LEVEL_NAV_ITEMS의 키들 중 하나라면 true
    val shouldShowBottomBar: Boolean
        @Composable get() = navigationState.currentKey in navigationState.topLevelKeys

    // 2. 최상위 탭 이동 (Navigator 위임)
    // 인자로 NavKey를 받습니다 (예: HomeNavKey, AlbumNavKey 등)
    fun navigateToTopLevel(key: NavKey) {
        navigator.navigate(key) //
    }

    // 3. 뒤로 가기 (Navigator 위임)
    fun onBackClick() {
        navigator.goBack() //
    }
}
