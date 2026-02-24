package com.a602.commonproject.database.di

import androidx.room.Database
import com.a602.commonproject.database.LMDatabase
import com.a602.commonproject.database.dao.BabyDao
import com.a602.commonproject.database.dao.CollectionDao
import com.a602.commonproject.database.dao.MediaDao
import com.a602.commonproject.database.dao.NotificationDao
import com.a602.commonproject.database.dao.SlideshowDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    fun provideMediaDao(
        database: LMDatabase,
    ) : MediaDao = database.mediaDao()

    @Provides
    fun provideBabyDao(
        database: LMDatabase,
    ) : BabyDao = database.babyDao()

    @Provides
    fun provideSlideshowDao(
        database: LMDatabase,
    ) : SlideshowDao = database.slideshowDao()

    @Provides
    fun provideNotificationDao(
        database: LMDatabase,
    ) : NotificationDao = database.notificationDao()

    @Provides
    fun provideCollectionDao(
        database: LMDatabase,
    ) : CollectionDao = database.collectionDao()

}
