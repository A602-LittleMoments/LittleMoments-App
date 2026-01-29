package com.a602.commonproject.network.di

// ... 기존 import 유지
import com.a602.commonproject.network.datasource.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule { // 추상 클래스로 선언

    @Binds
    @Singleton
    internal abstract fun bindAuthNetworkDataSource(
        impl: RetrofitAuthNetwork
    ): AuthNetworkDataSource

    @Binds
    @Singleton
    internal abstract fun bindBabyNetworkDataSource(
        impl: RetrofitBabyNetwork
    ): BabyNetworkDataSource

    @Binds
    @Singleton
    internal abstract fun bindCollectionNetworkDataSource(
        impl: RetrofitCollectionNetwork
    ): CollectionNetworkDataSource

    @Binds
    @Singleton
    internal abstract fun bindGroupNetworkDataSource(
        impl: RetrofitGroupNetwork
    ): GroupNetworkDataSource

    @Binds
    @Singleton
    internal abstract fun bindMediaNetworkDataSource(
        impl: RetrofitMediaNetwork
    ): MediaNetworkDataSource

    @Binds
    @Singleton
    internal abstract fun bindQuestionNetworkDataSource(
        impl: RetrofitQuestionNetwork
    ): QuestionNetworkDataSource

    @Binds
    @Singleton
    internal abstract fun bindSlideshowNetworkDataSource(
        impl: RetrofitSlideshowNetwork
    ): SlideshowNetworkDataSource
}
