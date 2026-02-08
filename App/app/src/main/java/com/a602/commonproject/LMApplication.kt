package com.a602.commonproject

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.a602.commonproject.datastore.datastore.UserPreferencesDataSource
import com.a602.commonproject.sync.status.SyncManager
import com.a602.commonproject.sync.status.SyncSubscriber
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy

@HiltAndroidApp
class LMApplication : Application(), Configuration.Provider, ImageLoaderFactory {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    // ✅ 추천: 필요한 매니저를 여기서 바로 주입받습니다.
    @Inject lateinit var syncManager: SyncManager
    @Inject lateinit var syncSubscriber: SyncSubscriber

    @Inject lateinit var userPreferencesDataSource: UserPreferencesDataSource

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    /**
     * 🚀 [Coil 최적화] 전역 이미지 로더 캐시 설정
     * - 메모리 캐시: 가용 메모리의 25% 할당
     * - 디스크 캐시: 앱 전용 캐시 디렉토리에 최대 250MB 할당
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(250L * 1024 * 1024) // 250MB
                    .build()
            }
            .allowHardware(true)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    override fun onCreate() {
        super.onCreate() // 👈 여기서 Hilt 주입이 완료됩니다. (폭탄 제거 완료)

        FirebaseApp.initializeApp(this)

        // ✅ 추천: 주입된 객체를 바로 사용합니다.
        syncManager.requestSync()

        CoroutineScope(Dispatchers.IO).launch {
            userPreferencesDataSource.getOrCreateDeviceId()
            syncSubscriber.subscribe()
        }
    }
}
