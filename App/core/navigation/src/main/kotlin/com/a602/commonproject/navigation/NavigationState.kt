package com.a602.commonproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

/**
 * 설정 변경(예: 화면 회전) 및 프로세스 종료 후에도 유지되는 내비게이션 상태를 생성합니다.
 */
@Composable
fun rememberNavigationState(
    startKey: NavKey,
    topLevelKeys: Set<NavKey>,
): NavigationState {
    val topLevelStack = rememberNavBackStack(startKey)
    val subStacks = topLevelKeys.associateWith { key -> rememberNavBackStack(key) }

    return remember(startKey, topLevelKeys) {
        NavigationState(
            startKey = startKey,
            topLevelStack = topLevelStack,
            subStacks = subStacks,
        )
    }
}

/**
 * "내비게이션 상태를 보유하는 클래스(State holder)입니다."
 * • startKey: 시작 내비게이션 키입니다. 사용자는 이 키(화면)를 통해 앱을 종료하게 됩니다.
 * • topLevelStack: 최상위 백스택입니다. 최상위 키들만 보유합니다.
 * • subStacks: 각 최상위 키(탭)에 대한 개별 백스택들입니다.
 *  현재 앱이 어디에 있는지, 각 탭의 뒤로가기 기록은 어떠한지를 저장하는 데이터 바구니입니다.
 */
class NavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    // 현재 선택 된 탭 (예 : 홈탭, 설정 탭)
    val currentTopLevelKey: NavKey by derivedStateOf { topLevelStack.last() }

    val topLevelKeys get() = subStacks.keys

    // 현재 탭 안에서의 화면 스택 (예시 : 홈 탭 안에서의 상세화면)
    val currentSubstack: NavBackStack<NavKey>
        get() = subStacks[currentTopLevelKey]
            ?: error("$currentTopLevelKey 에 대한 하위 스택이 없습니다.")

    // 지금 보고 있는 화면
    val currentKey: NavKey by derivedStateOf { currentSubstack.last() }

}

/**
 * "NavigationState를 NavEntries로 변환합니다."
 *  State를 실제 Compose가 그릴 수 있는 Entry로 변환하는 함수
 *  (ViewModel과 SaveableState를 자동으로 붙여줍니다)
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = subStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            // 화면 상태 (스크롤 등) 저장 기능
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            // ViewModel 수명 관리 기능
            rememberViewModelStoreNavEntryDecorator<NavKey>(),
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        )
    }

    return topLevelStack
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
