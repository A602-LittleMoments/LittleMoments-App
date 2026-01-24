package com.a602.commonproject.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri


private const val MAX_NUM_NOTIFICATIONS = 5
private const val CHANNEL_ID = "general_notification_channel"
private const val CHANNEL_NAME = "General Notifications"
private const val CHANNEL_DESCRIPTION = "Shows general app notifications"

@Singleton
class SystemTrayNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {
    override fun postNotification(id: Int, title: String, content: String, deepLinkUri : String?) {
        // 1. 권한 체크 (안드로이드 13 이상)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없으면 알림을 못 보냅니다. (로그를 남기거나 리턴)
            return
        }
        // 2. 알림 채널 만들기 (최초 1회만 동작함)
        ensureNotificationChannelExists()

        // 3. 알림 클릭시 실행할 동작 (앱 렬기)
        // 패키지 매니저에게 "이 앱을 실행할 수 있는 인텐트" 요청
        val intent = if (deepLinkUri != null) {
            Intent(Intent.ACTION_VIEW, deepLinkUri.toUri()).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            context.packageManager.getLaunchIntentForPackage(context.packageName)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        // 4. 알림 생성
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.core_notifications_ic_nia_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // 클릭하면 삭제
            .build()

        with(NotificationManagerCompat.from(context)){
            notify(id, notification)
        }

    }
    // 안드로이드 8.0 이상은 채널이 필수
    private fun ensureNotificationChannelExists(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            // 시스템에 채널 등록
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }

    }
}
