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
     * @return true - 성공적으로 뒤로가기 수행됨, false - 더 이상 돌아갈 곳이 없음 (Root)
     */
    fun goBack(): Boolean {
        // 1. 현재 서브 스택(상세 화면)이 있다면 제거
        if (state.currentSubstack.size > 1) {
            state.currentSubstack.removeLastOrNull()
            return true
        }

        // 2. 최상위 탭 스택이 있다면 제거
        if (state.topLevelStack.size > 1) {
            state.topLevelStack.removeLastOrNull()
            return true
        }

        // 3. 더 이상 돌아갈 곳이 없음 (Activity 종료 처리를 위해 false 반환)
        return false
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
            // 무조건 바닥에 현재 루트(Root)를 깔기
            // startKey가 아니라, 현재 스택의 가장 바닥에 있는 키를 유지해야 함.
            // (로그인 후에는 Home이 Root가 되었을 테니까)
            val currentRoot = if (isNotEmpty()) first() else state.startKey

            // 1. 기존의 쌓여 있던 탭 기록을 모두 지움
            clear()

            add(currentRoot)

            // 3. 이동하려는 곳이 '현재 루트'가 아니라면 그 위에 얹기
            if (key != currentRoot)
                add(key)
        }
    }

    /**
     * 루트를 교체합니다. (예: 로그인 완료 후 Splash/Login 스택 제거하고 홈으로 설정)
     * 모든 서브스택도 초기화하여 이전 세션의 네비게이션 상태를 제거합니다.
     */
    fun replaceRoot(key: NavKey) {
        state.topLevelStack.clear()
        state.topLevelStack.add(key)
        // 모든 서브스택 초기화 (루트 키만 남김)
        state.subStacks.forEach { (stackKey, stack) ->
            val rootKey = stack.firstOrNull() ?: stackKey
            stack.clear()
            stack.add(rootKey)
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
