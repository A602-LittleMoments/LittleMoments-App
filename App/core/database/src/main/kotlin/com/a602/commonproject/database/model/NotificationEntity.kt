package com.a602.commonproject.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String, // FCM MessageId or HashCode
    val title: String,
    val body: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val deepLink: String? = null,
    val type: String? = null // e.g., "NEW_ALBUM", "SLIDESHOW", "GENERAL"
)
