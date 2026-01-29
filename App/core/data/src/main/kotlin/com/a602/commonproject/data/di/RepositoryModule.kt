package com.a602.commonproject.data.di

import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.data.repository.OfflineFirstSharedMediaRepository
import com.a602.commonproject.data.repository.OfflineFirstUserRepository
import com.a602.commonproject.data.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSharedMediaRepository(
        sharedMediaRepository: OfflineFirstSharedMediaRepository, // 실제 구현체
    ): SharedMediaRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepository: OfflineFirstUserRepository,// 실제 구현체
    ): UserRepository

}
