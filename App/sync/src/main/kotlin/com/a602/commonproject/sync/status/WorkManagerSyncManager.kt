package com.a602.commonproject.sync.status

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.a602.commonproject.sync.initializers.SyncConstraints
import com.a602.commonproject.sync.workers.FetchWorker
import com.a602.commonproject.sync.workers.MediaCleanupWorker
import com.a602.commonproject.sync.workers.UploadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map

/**
 * 실제로 Woker들이 동작하는 부분
 */
internal class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncManager {

    private val workManager get() = WorkManager.getInstance(context)

    // 실제로 동작하고 있는지를 확인
    // "SYNC_WORK_NAME" 이라는 이름표 달린 작업들의 상태를 계속 달라고 함
    override val isSyncing: Flow<Boolean> =
        workManager.getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
            .map(List<WorkInfo>::anyRunning)
            .conflate() // 상태가 너무 빨리 변함녀 중간 거 건너뛰고 최신만 받기


    override fun requestSync() {
        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(SyncConstraints)
//            .setInputData(inputData)
            .build()

        val fetchWorkRequest = OneTimeWorkRequestBuilder<FetchWorker>()
            .setConstraints(SyncConstraints)
//            .setInputData(inputData)
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


    override fun initializePeriodicCleanup() {
        // 제약 조건: 저장소 공간이 부족하지 않을 때만 실행 (배터리 절약 등 추가 가능)
        val constraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build()

        // 24시간마다 반복
        val cleanupRequest = PeriodicWorkRequestBuilder<MediaCleanupWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        // 큐에 등록 (KEEP: 이미 예약돼 있으면 덮어쓰지 않고 유지함)
        workManager.enqueueUniquePeriodicWork(
            CLEANUP_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE, // ✨ 중복 실행 방지 핵심
            cleanupRequest
        )
    }

    companion object {
        const val SYNC_WORK_NAME = "SyncWorkName"
        const val CLEANUP_WORK_NAME = "MediaCleanupWork"
    }
}

// ✨ [NiA 확장 함수]
// 리스트 중에 하나라도 'RUNNING(실행 중)' 상태인 게 있으면 true 반환
private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED }
