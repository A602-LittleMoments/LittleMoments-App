package com.a602.commonproject.network.di

import com.a602.commonproject.network.BuildConfig
import com.a602.commonproject.network.api.RetrofitAuthApi
import com.a602.commonproject.network.api.RetrofitBabyApi
import com.a602.commonproject.network.api.RetrofitCollectionApi
import com.a602.commonproject.network.api.RetrofitGroupApi
import com.a602.commonproject.network.api.RetrofitMediaApi
import com.a602.commonproject.network.api.RetrofitQuestionApi
import com.a602.commonproject.network.api.RetrofitSlideshowApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // =================================================================
    // 1. 기본 설정 (JSON, OkHttp, Retrofit)
    // =================================================================

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true // DTO에 없는 필드가 와도 에러 안 남 (안전성 ↑)
        coerceInputValues = true // null이 들어오면 기본값(Default Value) 사용
        encodeDefaults = true    // 요청 보낼 때 기본값도 포함해서 전송
        prettyPrint = true       // 로그 찍을 때 예쁘게
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        // 로깅 인터셉터 (Debug 모드일 때만 Body 출력)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // 🔑 헤더 관리 (토큰 자동 추가/제거)
            .addInterceptor(loggingInterceptor) // 📜 로그 출력
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃
            .readTimeout(30, TimeUnit.SECONDS) // 읽기 타임 아웃
            .writeTimeout(30, TimeUnit.SECONDS) // 쓰기(업로드) 타임 아웃
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        networkJson: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // =================================================================
    // 4️⃣ [API 생성] 7개의 API 서비스 제공 (Retrofit에서 뽑아내기)
    // =================================================================

    @Provides
    @Singleton
    internal fun provideAuthApi(retrofit: Retrofit): RetrofitAuthApi =
        retrofit.create(RetrofitAuthApi::class.java)

    @Provides
    @Singleton
    internal fun provideGroupApi(retrofit: Retrofit): RetrofitGroupApi =
        retrofit.create(RetrofitGroupApi::class.java)

    @Provides
    @Singleton
    internal fun provideBabyApi(retrofit: Retrofit): RetrofitBabyApi =
        retrofit.create(RetrofitBabyApi::class.java)

    @Provides
    @Singleton
    internal fun provideMediaApi(retrofit: Retrofit): RetrofitMediaApi =
        retrofit.create(RetrofitMediaApi::class.java)

    @Provides
    @Singleton
    internal fun provideCollectionApi(retrofit: Retrofit): RetrofitCollectionApi =
        retrofit.create(RetrofitCollectionApi::class.java)

    @Provides
    @Singleton
    internal fun provideSlideshowApi(retrofit: Retrofit): RetrofitSlideshowApi =
        retrofit.create(RetrofitSlideshowApi::class.java)

    @Provides
    @Singleton
    internal fun provideQuestionApi(retrofit: Retrofit): RetrofitQuestionApi =
        retrofit.create(RetrofitQuestionApi::class.java)


}
