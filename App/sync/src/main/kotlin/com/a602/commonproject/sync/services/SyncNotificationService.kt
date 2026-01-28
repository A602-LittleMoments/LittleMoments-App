package com.a602.commonproject.sync.services

import android.util.Log
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.datastore.datastore.UserPreferencesDataSource
import com.a602.commonproject.notifications.Notifier
import com.a602.commonproject.sync.status.SyncManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


private const val SYNC_TOPIC_SENDER = "/topics/sync"

@AndroidEntryPoint
class SyncNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var syncManager: SyncManager

    @Inject
    lateinit var notifier: Notifier

    @Inject
    lateinit var userRepository: UserRepository // 토큰 갱신용 (아래 onNewToken 설명 참고)

    // ✨ [추가] 내 로컬 그룹 ID를 찾기 위해 필요
    @Inject
    lateinit var userPreferences: UserPreferencesDataSource

    // 서비스는 생명주기가 짧지만, 비동기 작업을 위해 Scope가 필요할 수 있음
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // 메시지가 어디서 왔는지 확인 (우리가 구독한 'sync' 토픽인지)
    // 또는 data payload 특정 키가 있는지 확인해도 됩니다.
    override fun onMessageReceived(message: RemoteMessage) {

        // ==========================================
        // CASE A: 데이터 동기화 요청 (isSync)
        // ==========================================
        if (SYNC_TOPIC_SENDER == message.from || message.data["type"] == "isSync") {
            Log.d("FCM", "동기화 요청 수신 -> 작업 예약")
            syncManager.requestSync()
        }

        // ==========================================
        // CASE B: 새 앨범 알림 (NEW_ALBUM)
        // ==========================================
        else if (message.data["type"] == "NEW_ALBUM") {
            val albumTitle = message.data["title"] ?: "새 앨범"
            val msgBody = message.data["message"] ?: "새로운 앨범이 도착했습니다."
            val albumId = message.data["albumId"]
            val deepLink = if (albumId != null) "myapp://album/$albumId" else null

            // 1. 알림 띄우기
            notifier.postNotification(
                id = albumId?.hashCode() ?: System.currentTimeMillis().toInt(),
                title = albumTitle,
                content = msgBody,
                deepLinkUri = deepLink,
            )
            // 2. ✨ 내 그룹 데이터 갱신
            syncManager.requestSync()
        }


        // ==========================================
        // CASE C: 일반 알림
        // ==========================================
        message.notification?.let {
            notifier.postNotification(
                id = message.messageId?.hashCode() ?: System.currentTimeMillis().toInt(),
                title = it.title ?: "새 알림",
                content = it.body ?: "새로운 사진이 공유되었습니다.",
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 1. 로그 찍어보기 (개발용)
        Log.d("FCM", "새로운 기기 주소 발급됨: $token")
        // 토큰이 갱신되면 서버에 알려줘야, 서버가 이 기기로 푸시를 보낼 수 있음
        // Service는 비동기 호출을 위해 CoroutineScope 사용
        serviceScope.launch {
            try {
                // UserRepository나 AuthDataSource에 updateFcmToken 함수가 필요함
                // 예시: userRepository.updateFcmToken(token)
                // 혹은 UserPreferences에 일단 저장해두고 나중에 보낼 수도 있음
            } catch (e: Exception) {
                Log.e("FCM", "토큰 서버 전송 실패", e)
            }
        }
    }

}
