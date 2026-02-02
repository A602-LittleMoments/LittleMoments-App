package com.a602.commonproject.data.repository.impl

import com.a602.commonproject.data.model.asExternalModel
import com.a602.commonproject.data.repository.TempMediaRepository
import com.a602.commonproject.database.dao.MediaDao
import com.a602.commonproject.database.model.ShareMediaEntity
import com.a602.commonproject.database.model.TempMediaEntity
import com.a602.commonproject.datastore.datastore.UserPreferencesDataSource
import com.a602.commonproject.model.data.TempMedia
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class OfflineFirstTempMediaRepository @Inject constructor(
    private val mediaDao: MediaDao,
    private val userPreferences: UserPreferencesDataSource
) : TempMediaRepository {

    // ⏳ 임시 보관 기간 (예: 3일)
    private val EXPIRATION_DAYS = 30L

    // =================================================================
    // 1. 목록 조회
    // =================================================================
    override fun getTempMediaStream(): Flow<List<TempMedia>> =
        mediaDao.getTempMediaFlow().map { list -> list.map { it.asExternalModel() } } //


    override fun getTempMedia(tempId: String): Flow<TempMedia?> =
        mediaDao.getTempMediaById(tempId).map { it?.asExternalModel() }

    override suspend fun saveTempMedia(
        tempId: String,
        file: File,
        subFile: File?,
        takenAt: Long,
        orientation: Int,
        cameraFacing: String,
    ): Result<Unit> {
        return try {
            // 만료일 계산 (현재 + 3일)
            val expirationDate = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(EXPIRATION_DAYS)

            val entity = TempMediaEntity(
                tempId = tempId,
                localUri = file.absolutePath, // 파일 절대 경로 저장
                subLocalUri = subFile?.absolutePath, // 썸네일/전면카메라 경로 저장
                takenAt = takenAt,
                cameraFacing = cameraFacing,
                orientation = orientation,
                expirationDate = expirationDate //
            )

            // DB 저장
            mediaDao.upsertTempList(listOf(entity)) //

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTempMedia(ids: List<String>): Result<Unit> {
        return try {
            // 1. ✨ DB에서 삭제할 파일 정보들 한 번에 가져오기 (Batch Query)
            // (반복문으로 DB를 계속 찌르지 않아 훨씬 빠릅니다)
            val entitiesToDelete = mediaDao.getTempMediaListByIds(ids) //

            // 2. 실제 파일 삭제 (로컬 저장소 정리)
            entitiesToDelete.forEach { entity ->
                entity.localUri?.let { path ->
                    deleteFile(path)
                }
            }

            // 3. DB 데이터 삭제
            mediaDao.deleteTempMediaByIds(ids) //

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================
    // 4. 만료된 미디어 자동 정리 (Worker용)
    // =================================================================
    override suspend fun cleanupExpiredMedia(): Result<Int> {
        return try {
            val now = System.currentTimeMillis()

            // 1. 만료된 목록 가져오기
            val expiredList = mediaDao.getExpiredTempMedia(now)
            if (expiredList.isEmpty()) return Result.success(0)

            // 2. 실제 파일 삭제
            var deletedCount = 0
            val idsToDelete = expiredList.map { it.tempId }

            expiredList.forEach { entity ->
                entity.localUri?.let { path ->
                    if (deleteFile(path)) deletedCount++
                }
            }

            // 3. DB 삭제
            mediaDao.deleteTempMediaByIds(idsToDelete)

            Result.success(deletedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun moveToShared(
        tempIds: List<String>,
        captions: Map<String, String>?
    ): Result<Unit> {
        return try {
            // ✨ 1. 업로더 이름(내 닉네임) 가져오기
            val myNickname = userPreferences.userNickname.first() ?: "알 수 없음"

            val entitiesToMove = mutableListOf<ShareMediaEntity>()

            // 1. Temp 정보를 바탕으로 Shared Entity 생성
            // (여기서는 개별 조회를 해도 사용자가 선택한 소수 파일이라 괜찮음)
            tempIds.forEach { tempId ->
                val tempEntity = mediaDao.getTempMediaById(tempId).firstOrNull()

                if (tempEntity != null && tempEntity.localUri != null) {
                    // ✨ Temp -> Shared 변환 로직
                    // ✨ ShareMediaEntity 필드에 맞춰 매핑
                    val shareEntity = ShareMediaEntity(
                        mediaId = tempEntity.tempId, // ID 유지

                        // 파일 경로 (업로드 전이므로 로컬만 있음)
                        localUri = tempEntity.localUri,
                        remoteUrl = null,
                        thumbnailUrl = null,

                        // 서브 경로 (썸네일 등) - Temp에 있는 값 승계
                        subLocalUri = tempEntity.subLocalUri,
                        subRemoteUrl = null,
                        subThumbnailUrl = null,

                        // 메타데이터 복사
                        cameraFacing = tempEntity.cameraFacing,
                        orientation = tempEntity.orientation,
                        takenAt = tempEntity.takenAt,

                        // ✨ 추가된 필드들 처리
                        caption = captions?.get(tempId),       // 캡션 저장
                        type = "PHOTO",       // 기본값은 사진 (필요 시 로직 추가)
                        uploaderName = myNickname, // ✨ 아까 가져온 닉네임 사용

                        // 상태 설정 (WorkManager가 감지하여 업로드함)
                        syncStatus = "NOT_UPLOADED"
                    )
                    entitiesToMove.add(shareEntity)
                }
            }

            if (entitiesToMove.isNotEmpty()) {
                // 2. 트랜잭션 실행 (Temp 삭제 + Shared 추가)
                mediaDao.moveToShared(tempIds, entitiesToMove)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun deleteFile(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) {
                file.delete() // 파일 삭제 시도
            } else {
                false // 파일이 없으면 false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false // 에러 나면 false
        }
    }
}
