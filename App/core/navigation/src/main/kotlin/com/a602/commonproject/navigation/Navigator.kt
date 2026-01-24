package com.a602.commonproject.navigation

/**
 * [Navigator]
 * 실제 화면 이동 로직을 수행하는 클래스입니다.
 * NavigationState를 조작하여 스택을 쌓거나 뺍니다.
 * "네비게이션 이벤트(앞으로 가기, 뒤로 가기)를 처리하여 네비게이션 상태를 업데이트합니다."
 *  @param state: 네비게이션 이벤트에 응답하여 업데이트될 네비게이션 상태입니다.
 */

class Navigator(val state: NavigationState) {
}
