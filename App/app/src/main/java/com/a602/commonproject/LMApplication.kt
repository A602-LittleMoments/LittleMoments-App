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

@HiltAndroidApp
class LMApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    // ✅ 추천: 필요한 매니저를 여기서 바로 주입받습니다.
    @Inject lateinit var syncManager: SyncManager
    @Inject lateinit var syncSubscriber: SyncSubscriber

    @Inject lateinit var userPreferencesDataSource: UserPreferencesDataSource

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

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
