package com.a602.commonproject.sync.status

/**
 * [SyncSubscriber]
 * 동기화 로직과 관련된 변경사항을 구독하는 인터페이스
 * (예: 앱이 켜질 때 FCM 토픽을 구독하거나 해제하는 등)
 */
interface SyncSubscriber {
    suspend fun subscribe()
}
