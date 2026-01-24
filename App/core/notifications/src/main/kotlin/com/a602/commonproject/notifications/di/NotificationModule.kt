package com.a602.commonproject.notifications.di

import com.a602.commonproject.notifications.Notifier
import com.a602.commonproject.notifications.SystemTrayNotifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationModule {
    @Binds
    @Singleton
    abstract fun bindNotifier(
        notifier: SystemTrayNotifier,
    ): Notifier
}
