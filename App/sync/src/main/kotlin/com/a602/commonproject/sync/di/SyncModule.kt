package com.a602.commonproject.sync.di

import com.a602.commonproject.sync.status.FirebaseSyncSubscriber
import com.a602.commonproject.sync.status.StubSyncSubscriber
import com.a602.commonproject.sync.status.SyncManager
import com.a602.commonproject.sync.status.SyncSubscriber
import com.a602.commonproject.sync.status.WorkManagerSyncManager
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SyncModule {
    // 1. SyncManager 요청 시 -> WorkManagerSyncManager 주입
    @Binds
    fun bindSyncManager(
        syncManager: WorkManagerSyncManager
    ): SyncManager

    // 2. SyncSubscriber 요청 시 -> StubSyncSubscriber (가짜 구독자) 주입
    @Binds
    fun bindsSyncSubscriber(
        syncSubscriber: FirebaseSyncSubscriber,
    ): SyncSubscriber

    // [추가] FirebaseMessaging 객체 제공
    // (interface 안에서는 companion object를 써서 @Provides를 만듭니다)
    companion object {
        @Provides
        @Singleton
        fun provideFirebaseMessaging(): FirebaseMessaging = Firebase.messaging
    }
}
