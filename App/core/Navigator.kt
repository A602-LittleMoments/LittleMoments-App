package com.a602.commonproject.navigation

import androidx.navigation3.runtime.NavKey

/**
 * [Navigator]
 * 실제 화면 이동 로직을 수행하는 클래스입니다.
 * NavigationState를 조작하여 스택을 쌓거나 뺍니다.
 * "네비게이션 이벤트(앞으로 가기, 뒤로 가기)를 처리하여 네비게이션 상태를 업데이트합니다."
 *  @param state: 네비게이션 이벤트에 응답하여 업데이트될 네비게이션 상태입니다.
 */

class Navigator(val state: NavigationState) {
    /**
     * "특정 네비게이션 키(화면)로 이동합니다."
     * @param key: 이동할 네비게이션 키입니다.
     */
    fun navigate(key: NavKey) {
        when (key) {
            // 현재 보고 있는 탭을 또 누르면 -> 스택 초기화 (Refresh 느낌)
            state.currentTopLevelKey -> clearSubStack()
            // 다른 탭 (예: 설정)을 누르면 -> 탭 전환
            in state.topLevelKeys -> goToTopLevel(key)
            // 일반 화면(예: 상세 화면) 으로 이동 -> 스택 쌓기
            else -> goToKey(key)
        }
    }

    /**
     * 이전 화면으로 돌아가는 함수 (뒤로 가기)
     */
    fun goBack() {
        when (state.currentKey) {
            // 시작 화면에서는 더 이상 뒤로 갈 수 없음 (Activity에서 앱 종료 처리)
            state.startKey -> error("시작 루트에서는 돌아갈 수 없습니다.")
            // 현재 탭의 최상위 화면일때 (예: 설정 탭의 메인 화면)
            state.currentTopLevelKey -> {
                // goToTopLevel 로직 덕분에, 여기서 제거하면 무조건 Home(StartKey)만 남습니다.
                state.topLevelStack.removeLastOrNull()
            }

            else -> state.currentSubstack.removeLastOrNull() // 일반화면이면 뒤로 가기
        }
    }

    /**
     * 일반 화면 이동 (상세 화면 등)
     * 현재 탭의 스택 위에 화면을 쌓습니다.
     */
    private fun goToKey(key: NavKey) {
        state.currentSubstack.apply {
            remove(key) // 이미 스택에 있다면 제거 -> 중복이면 안되니까
            add(key)              // 맨 위로 돌림
        }
    }

    /**
     * 탭 이동
     * 어떤 탭을 가든 백그라운드에는 "홈" 하나만 남겨두기.
     * 뒤로 가기시 무조건 홈으로 복귀
     */
    private fun goToTopLevel(key: NavKey) {
        state.topLevelStack.apply {
            // 1. 기존의 쌓여 있던 탭 기록을 모두 지움
            clear()
            // 2. 무조건 바닥에 '홈'을 깔기
            add(state.startKey)
            // 3. 이동하려는 곳이 '홈'이 라니라면 그 위에 얹기
            if (key != state.startKey)
                add(key)
        }
    }

    /**
     *  현재 탭의 상세 기록 초기화
     *  (예: 홈 탭에서 깊게 들어갔다가 홈 탭 버튼을 다시 누르면 초기화되는 기능)
     */
    private fun clearSubStack() {
        state.currentSubstack.run {
            if (size > 1) subList(1, size).clear()
        }
    }
}
