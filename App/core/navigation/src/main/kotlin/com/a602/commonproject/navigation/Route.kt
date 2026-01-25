package com.a602.commonproject.navigation

import kotlinx.serialization.Serializable

/**
 * [Route]
 * 앱의 모든 이동 경로(화면 주소)를 정의하는 파일
 * 화면이 추가될 때마다 여기에 @Serializable 클래스/객체를 추가하세요.
 */
sealed interface Route {
    @Serializable
    data object Home : Route
}
