package com.a602.commonproject.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.a602.commonproject.common.network.Dispatcher
import com.a602.commonproject.common.network.LMDispatchers
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.data.repository.CollectionRepository

import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.data.repository.SlideshowRepository
import com.a602.commonproject.data.repository.UserRepository
import com.a602.commonproject.sync.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * 서버에서 최신 사진 목록을 가져와서(pull), 로컬 DB와 동기화하는 worker
 */
@HiltWorker
class FetchWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val mediaRepository: SharedMediaRepository,
    private val slideshowRepository: SlideshowRepository, // ✨ 주입 추가
    private val collectionRepository: CollectionRepository,
    private val userRepository: UserRepository,
    private val babyRepository: BabyRepository,
    @param:Dispatcher(LMDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, workerParams) {

    // 시스템입장에서 포그라운드에서 도는 것 (화면 기준으로는 백그라운드 작업)
    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        runCatching {
            // 1. 전달받은 groupId 꺼내기
            val groupId = userRepository.getCurrentGroupId() ?: return@runCatching Result.failure()

            // 2. 동기화 작업을 병렬로 실행
            val success = listOf(
                async { mediaRepository.syncWithServer(groupId) }, // 사진 갱신 (Boolean)
                async { slideshowRepository.refreshSlideshows().isSuccess },
                async { babyRepository.syncWithServer(groupId) },
                async { collectionRepository.syncCollections() },
            ).awaitAll().all { it }

            if (success) Result.success() else Result.retry()
        }.getOrElse { Result.failure() }
    }
}
