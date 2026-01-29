package com.a602.commonproject.data.di

import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.data.repository.DailyQuestionRepository
import com.a602.commonproject.data.repository.GroupRepository
import com.a602.commonproject.data.repository.impl.NetworkDailyQuestionRepository
import com.a602.commonproject.data.repository.impl.OfflineFirstBabyRepository
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.data.repository.SlideshowRepository
import com.a602.commonproject.data.repository.impl.OfflineFirstSharedMediaRepository
import com.a602.commonproject.data.repository.impl.OfflineFirstTempMediaRepository
import com.a602.commonproject.data.repository.impl.OfflineFirstUserRepository
import com.a602.commonproject.data.repository.TempMediaRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.data.repository.impl.NetworkCollectionRepository
import com.a602.commonproject.data.repository.impl.NetworkGroupRepository
import com.a602.commonproject.data.repository.impl.OfflineFirstSlideshowRepository
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


    @Binds
    @Singleton
    abstract fun bindTempMediaRepository(
        impl: OfflineFirstTempMediaRepository,
    ): TempMediaRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(
        impl: NetworkGroupRepository,
    ): GroupRepository

    @Binds
    @Singleton
    abstract fun bindBabyRepository(
        impl: OfflineFirstBabyRepository,
    ): BabyRepository

    @Binds
    @Singleton
    abstract fun bindCollectionRepository(
        impl: NetworkCollectionRepository,
    ): CollectionRepository

    @Binds
    @Singleton
    abstract fun bindSlideShowRepository(
        impl: OfflineFirstSlideshowRepository,
    ): SlideshowRepository

    @Binds
    @Singleton
    abstract fun bindDailyQuestionRepository(
        impl: NetworkDailyQuestionRepository,
    ): DailyQuestionRepository
}
