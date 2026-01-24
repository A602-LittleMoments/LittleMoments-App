package com.a602.commonproject.sync.services

import android.util.Log
import com.a602.commonproject.notifications.Notifier
import com.a602.commonproject.notifications.SystemTrayNotifier
import com.a602.commonproject.sync.status.SyncManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val SYNC_TOPIC_SENDOR = "/topics/sync"

@AndroidEntryPoint
class SyncNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var syncManager :  SyncManager
    @Inject
    lateinit var notifier : Notifier

    // 메시지가 어디서 왔는지 확인 (우리가 구독한 'sync' 토픽인지)
    // 또는 data payload 특정 키가 있는지 확인해도 됩니다.
    override fun onMessageReceived(message: RemoteMessage) {
        // 데이터 동기화 하는 부분
        if (SYNC_TOPIC_SENDOR == message.from || message.data["type"] == "isSync"){
            // 동기화 하라는 명령 내리기
            // (이러면 즉시 Worker가 돌면서 서버와 데이터를 맞춥니다)
            syncManager.requestSync()
        }
        // 새 앨범이 생겼을 떄 로직
        else if (message.data["type"] == "NEW_ALBUM") {
            val albumTitle = message.data["title"] ?: "새 앨범"
            val msgBody = message.data["message"] ?: "새로운 앨범이 도착했습니다."
            val albumId = message.data["albumId"]

            // 사용자가 알림 누르면 바로 그 앨범으로 이동하게 DeepLink 생성 (선택사항)
            // 예: "myapp://album/502"
            val deepLink = if (albumId != null) "myapp://album/$albumId" else null

            notifier.postNotification(
                id = albumId?.hashCode() ?: System.currentTimeMillis().toInt(), // ID 별로 알림 따로 쌓이게
                title = albumTitle,
                content = msgBody,
                deepLinkUri = deepLink
            )

            // (선택) 새 앨범이 왔으니 데이터도 갱신해야겠죠?
            syncManager.requestSync()
        }

        // 알림 처리
        message.notification?.let{
            notifier.postNotification(
                id = message.messageId?.hashCode() ?: System.currentTimeMillis().toInt(),
                title = it.title ?: "새 알림",
                content = it.body ?: "새로운 사진이 공유되었습니다."
            )
        }
    }

    override fun onNewToken(token: String) {
        // 1. 로그 찍어보기 (개발용)
        Log.d("FCM", "새로운 기기 주소 발급됨: $token")
        // TODO: 토큰을 서버에 보내야 됨
    }

}
