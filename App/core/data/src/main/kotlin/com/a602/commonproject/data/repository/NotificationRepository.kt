package com.a602.commonproject.data.repository

import com.a602.commonproject.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(): Flow<List<NotificationEntity>>
    suspend fun saveNotification(notification: NotificationEntity)
    suspend fun deleteNotification(id: String)
}
