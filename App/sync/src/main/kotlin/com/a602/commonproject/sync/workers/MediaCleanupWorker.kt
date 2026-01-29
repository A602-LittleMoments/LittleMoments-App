package com.a602.commonproject.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.a602.commonproject.common.network.Dispatcher
import com.a602.commonproject.common.network.LMDispatchers
import com.a602.commonproject.data.repository.TempMediaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * 🧹 하루에 한 번씩 만료된(3일 지난) 임시 사진을 정리하는 Worker
 */
@HiltWorker
class MediaCleanupWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val tempMediaRepository: TempMediaRepository, // ✨ Repository 주입
    @Dispatcher(LMDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            // [Repository 호출] 만료된 미디어 정리
            val result = tempMediaRepository.cleanupExpiredMedia()

            // 성공/실패 여부에 따라 결과 반환
            // (cleanupExpiredMedia가 Result<Int>를 반환한다고 가정)
            if (result.isSuccess) {
                Result.success()
            } else {
                Result.retry() // 실패 시 나중에 재시도
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

}
