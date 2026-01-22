package com.a602.commonproject.sync.di

import com.a602.commonproject.sync.status.StubSyncSubscriber
import com.a602.commonproject.sync.status.SyncManager
import com.a602.commonproject.sync.status.SyncSubscriber
import com.a602.commonproject.sync.status.WorkManagerSyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
        syncSubscriber: StubSyncSubscriber,
    ): SyncSubscriber
}
