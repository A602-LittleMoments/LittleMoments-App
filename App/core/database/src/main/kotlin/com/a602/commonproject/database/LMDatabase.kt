package com.a602.commonproject.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.a602.commonproject.database.dao.BabyDao
import com.a602.commonproject.database.dao.MediaDao
import com.a602.commonproject.database.dao.SlideshowDao
import com.a602.commonproject.database.model.BabyEntity
import com.a602.commonproject.database.model.ShareMediaEntity
import com.a602.commonproject.database.model.SlideshowEntity
import com.a602.commonproject.database.model.TempMediaEntity
import com.a602.commonproject.database.dao.NotificationDao
import com.a602.commonproject.database.model.MediaBabyCrossRefEntity
import com.a602.commonproject.database.model.NotificationEntity

@Database(
    entities = [
        TempMediaEntity::class,
        ShareMediaEntity::class,
        SlideshowEntity::class,
        BabyEntity::class,
        NotificationEntity::class,
        MediaBabyCrossRefEntity::class
    ],
    version = 4,
    exportSchema = true
)
internal abstract class LMDatabase : RoomDatabase(){
    abstract fun mediaDao() : MediaDao
    abstract fun babyDao() : BabyDao
    abstract fun slideshowDao() : SlideshowDao
    abstract fun notificationDao() : NotificationDao // [New]
}
