package com.a602.commonproject.sync.initializers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import com.a602.commonproject.sync.R

/**
 * Worker 들이 공통응로 사용할 설정과 알림창 도구 모음
 */

private const val SYNC_NOTIFICATION_ID = 1001

private const val SYNC_NOTIFICATION_CHANNEL_ID = "PhotoSyncChannel"

// 공통 제약 조건 : 인터넷이 연결 되어 있어야 함
val SyncConstraints
    get() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()


/**
 * 포그라운드 서비스를 사용하여 동기화 워커가 실행될 때
 * 하위 API 레벨에서의 동기화를 위한 포그라운드 정보입니다.
 */

fun Context.syncForegroundInfo() = ForegroundInfo(
    SYNC_NOTIFICATION_ID,
    syncWorkNotification(),
)

/**
 * 실제 알림창 만들기
 * 상단바에 동기화 중이라는 알림을 띄워 주는 부분
 */
private fun Context.syncWorkNotification(): Notification {
    // 안드로이드 8.0 이상은 알림 채널(channel) 이 필수
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // min이 31이라 이미 크긴 함
        val channel = NotificationChannel(
            SYNC_NOTIFICATION_CHANNEL_ID,
            getString(R.string.sync_work_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT, // 소리나 진동 X
        ).apply {
            description = getString(R.string.sync_work_notification_channel_description)
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(channel)
    }
    return NotificationCompat.Builder(this, SYNC_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(
            com.a602.commonproject.notifications.R.drawable.core_notifications_ic_nia_notification,
        )
        .setContentTitle(getString(R.string.sync_work_notification_title))
        .setContentText(getString(R.string.sync_work_notification_channel_description))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setOngoing(true) // 사용자가 스와이프해서 지울 수 없게 함 (작업 끝나면 자동 삭제)
        .build()
}
