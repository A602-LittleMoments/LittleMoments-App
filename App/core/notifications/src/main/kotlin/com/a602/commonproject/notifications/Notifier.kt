package com.a602.commonproject.notifications


/**
 * 앱 내 알림 생성 인터페이스
 */
interface Notifier {
    fun postNotification(
        id : Int,
        title: String,
        content : String,
        deepLinkUri : String? = null
        // 필요하면 딥 링크나 아이콘 등을 인자로 추가가능
    )
}
