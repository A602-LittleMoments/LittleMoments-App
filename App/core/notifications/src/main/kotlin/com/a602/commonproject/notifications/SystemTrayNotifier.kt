package com.a602.commonproject.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


private const val MAX_NUM_NOTIFICATIONS = 5
private const val CHANNEL_ID = "general_notification_channel"
private const val CHANNEL_NAME = "General Notifications"
private const val CHANNEL_DESCRIPTION = "Shows general app notifications"

@Singleton
class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {
    override fun postNotification(id: Int, title: String, content: String) {
        // 권한 체크 (안드로이드 13 이상)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없으면 알림을 못 보냅니다. (로그를 남기거나 리턴)
            return
        }
        // 알림 채널 만들기 (최초 1회만 동작함)
        ensureNotificationChannelExists()

        // 알림 클릭시 실행할 동작 (앱 렬기)

    }
}
