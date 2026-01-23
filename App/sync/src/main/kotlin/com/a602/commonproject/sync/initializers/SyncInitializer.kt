package com.a602.commonproject.sync.initializers

import android.content.Context
import androidx.startup.Initializer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.a602.commonproject.sync.status.WorkManagerSyncManager.Companion.SYNC_WORK_NAME
import com.a602.commonproject.sync.workers.FetchWorker
import com.a602.commonproject.sync.workers.UploadWorker

/**
 * [SyncInitializer]
 * 앱 시작 시 WorkManager를 초기화하고 첫 동기화 작업을 예약합니다.
 */
class SyncInitializer : Initializer<Unit>{
    override fun create(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(SyncConstraints)
            .build()

        val fetchWorkRequest = OneTimeWorkRequestBuilder<FetchWorker>()
            .setConstraints(SyncConstraints)
            .build()

        workManager
            .beginUniqueWork(
                SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP, // 이미 돌고 있으면 무시 (중복 방지)
                uploadWorkRequest, // 업로드 작업
            )
            .then(fetchWorkRequest) // 없로드 끝나면 다운으로
            .enqueue()

    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        // 다른 초기화 작업에 의존성이 있다면 추가
        return emptyList()
    }

}
