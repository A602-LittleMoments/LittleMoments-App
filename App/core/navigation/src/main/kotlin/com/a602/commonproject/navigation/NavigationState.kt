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
 * • [startKey]: 앱 실행 시 표시되는 최초의 화면이자, 뒤로가기 탐색을 통해 앱을 종료하기 전 마지막으로 도달하게 되는 기준점입니다.
 * • [topLevelStack]: 최상위 목적지(하단 탭 등) 간의 이동 이력을 관리하여, 서로 다른 탭 사이에서 발생하는 뒤로가기 탐색을 지원하는 백스택입니다.
 * • [subStacks]:  각 최상위 목적지별로 독립적인 화면 적재 기록을 맵(Map) 형태로 유지하여, 탭 전환 시에도 각 탭 내부의 탐색 상태를 보존하는 데이터 구조입니다.
 *  현재 앱이 어디에 있는지, 각 탭의 뒤로가기 기록은 어떠한지를 저장하는 데이터 바구니입니다.
 */
class NavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    // 현재 선택 된 탭 (예 : 홈탭, 설정 탭)
    // topLevelStack에서 가장 최근에 추가된(마지막) 요소를 반환합니다.
    // 사용자가 현재 활성화하여 보고 있는 최상위 카테고리(탭)가 무엇인지 나타내며, derivedStateOf를 통해 스택이 변경될 때마다 상태를 동적으로 갱신합니다.
    val currentTopLevelKey: NavKey by derivedStateOf { topLevelStack.last() }

    //  subStacks 맵의 모든 키를 반환합니다.
    //  앱에 정의된 모든 최상위 목적지들의 전체 집합을 의미하며, 주로 내비게이션 UI(하단 바 등)를 구성하는 기준 데이터로 사용됩니다.
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
