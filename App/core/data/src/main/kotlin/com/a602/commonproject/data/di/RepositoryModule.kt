package com.a602.commonproject.data.di

import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.data.repository.MediaRepositoryImpl
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
    abstract fun bindMediaRepository(
        mediaRepositoryImpl: MediaRepositoryImpl // 실제 구현체
    ): SharedMediaRepository
}
