package com.a602.commonproject.sync.status

import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

/**
 * FCM [SYNC_TOPIC]을 구독하는 [SyncSubscriber] 구현체
 */

// 모든 앱이 공통으로 듣는 "단톡방" 이름
private const val SYNC_TOPIC = "sync"

class FirebaseSyncSubscriber @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
) : SyncSubscriber {
    override suspend fun subscribe() {
        try {
            firebaseMessaging
                .subscribeToTopic(SYNC_TOPIC)
                .await() // 비동기 작업 대기 (성공할 때까지 멈춤)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }
}
