package com.a602.commonproject.data.repository.impl

import com.a602.commonproject.data.repository.NotificationRepository
import com.a602.commonproject.database.dao.NotificationDao
import com.a602.commonproject.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineFirstNotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun getNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotifications()
    }

    override suspend fun saveNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }

    override suspend fun deleteNotification(id: String) {
        notificationDao.deleteNotification(id)
    }
}
