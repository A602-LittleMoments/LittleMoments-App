package com.a602.commonproject.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.a602.commonproject.common.network.Dispatcher
import com.a602.commonproject.common.network.LMDispatchers
import com.a602.commonproject.data.reposotory.MediaRepository
import com.a602.commonproject.sync.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * 로컬(내 폰)의 변경 사항을 서버(클라우드)에 반영하는 worker
 */
@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted private val appContext : Context,
    @Assisted workerParams : WorkerParameters,
    private val mediaRepository: MediaRepository,
    @Dispatcher(LMDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, workerParams) {

    // 시스템입장에서 포그라운드에서 도는 것 (화면 기준으로는 백그라우느드 작업)
    // 시스템에게 "작업 알림창은 이걸로 띄워줘" 라고 정보 제공
    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        try {
            // 시스템입장에서 포그라운드에서 도는 것 (화면 기준으로는 백그라우느드 작업)
            setForeground(getForegroundInfo())
            // Repository에 안 보낸 파일을 다 업로드 하로록 합
            // (내부에서 삭제 동기화 + 업로드 동기화 수행)
            val isSuccess = mediaRepository.uploadUnsyncedMedia()

            if(isSuccess) Result.success() else Result.retry()
        }
        catch (e : Exception){
            Result.failure()
        }
    }
}
