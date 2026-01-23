package com.a602.commonproject.sync.services

import android.util.Log
import com.a602.commonproject.sync.status.SyncManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val SYNC_TOPIC_SENSOR = "/topics/sync"

@AndroidEntryPoint
class SyncNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var syncManager :  SyncManager

    // 메시지가 어디서 왔는지 확인 (우리가 구독한 'sync' 토픽인지)
    // 또는 data payload 특정 키가 있는지 확인해도 됩니다.
    override fun onMessageReceived(message: RemoteMessage) {
        if (SYNC_TOPIC_SENSOR == message.from || message.data.contains("isSync")){
            // 동기화 하라는 명령 내리기
            // (이러면 즉시 Worker가 돌면서 서버와 데이터를 맞춥니다)
            syncManager.requestSync()
        }
    }

    override fun onNewToken(token: String) {
        // 1. 로그 찍어보기 (개발용)
        Log.d("FCM", "새로운 기기 주소 발급됨: $token")
    }

}
