package com.a602.commonproject.sync.status

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.a602.commonproject.sync.initializers.SyncConstraints
import com.a602.commonproject.sync.workers.FetchWorker
import com.a602.commonproject.sync.workers.UploadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private val workManager = WorkManager.getInstance(context)

    // 실제로 동작하고 있는지를 확인
    // "SYNC_WORK_NAME" 이라는 이름표 달린 작업들의 상태를 계속 달라고 함
    override val isSyncing: Flow<Boolean> =
        workManager.getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
            .map(List<WorkInfo>::anyRunning)
            .conflate() // 상태가 너무 빨리 변함녀 중간 거 건너뛰고 최신만 받기


    override fun requestSync(groupId : String) {

        // ✨ 1. Worker에게 보낼 데이터 포장
        // (FetchWorker와 UploadWorker 안에 KEY_GROUP_ID 상수가 있다고 가정)
        val inputData = workDataOf("key_group_id" to groupId)

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(SyncConstraints)
            .setInputData(inputData)
            .build()

        val fetchWorkRequest = OneTimeWorkRequestBuilder<FetchWorker>()
            .setConstraints(SyncConstraints)
            .setInputData(inputData)v
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

    companion object {
        const val SYNC_WORK_NAME = "SyncWorkName"
    }
}

// ✨ [NiA 확장 함수]
// 리스트 중에 하나라도 'RUNNING(실행 중)' 상태인 게 있으면 true 반환
private fun List<WorkInfo>.anyRunning() = any { it.state == WorkInfo.State.RUNNING }
