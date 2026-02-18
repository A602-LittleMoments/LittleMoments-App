package com.a602.commonproject.data.repository.impl

import com.a602.commonproject.data.model.asExternalModel
import com.a602.commonproject.data.repository.TempMediaRepository
import com.a602.commonproject.database.dao.MediaDao
import com.a602.commonproject.database.model.ShareMediaEntity
import com.a602.commonproject.database.model.TempMediaEntity
import com.a602.commonproject.datastore.datastore.UserPreferencesDataStore
import com.a602.commonproject.model.data.TempMedia
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import androidx.core.graphics.createBitmap

class OfflineFirstTempMediaRepository @Inject constructor(
    private val mediaDao: MediaDao,
    private val userPreferences: UserPreferencesDataStore,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
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

            // orientation이 0으로 들어온 경우, 실제 파일에서 방향 정보를 읽어오기 시도
            val actualOrientation = if (orientation == 0) {
                getOrientationDegrees(file.absolutePath)
            } else {
                orientation
            }

            val entity = TempMediaEntity(
                tempId = tempId,
                localUri = file.absolutePath, // 파일 절대 경로 저장
                subLocalUri = subFile?.absolutePath, // 썸네일/전면카메라 경로 저장
                takenAt = takenAt,
                cameraFacing = cameraFacing,
                orientation = actualOrientation,
                expirationDate = expirationDate, //
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
        captions: Map<String, String>?,
    ): Result<Unit> {
        return try {
            // ✨ 1. 업로더 이름(내 닉네임) 가져오기
            val myNickname = userPreferences.userNickname.first() ?: "알 수 없음"

            val entitiesToMove = mutableListOf<ShareMediaEntity>()

            // 1. Temp 정보를 바탕으로 Shared Entity 생성
            // (여기서는 개별 조회를 해도 사용자가 선택한 소수 파일이라 괜찮음)
            tempIds.forEach { tempId ->
                val tempEntity = mediaDao.getTempMediaById(tempId).firstOrNull()
                val safeLocalUri = tempEntity?.localUri

                if (tempEntity != null && safeLocalUri != null) {
                    // ✨ Temp -> Shared 변환 로직
                    // ✨ ShareMediaEntity 필드에 맞춰 매핑
                    val shareEntity = ShareMediaEntity(
                        mediaId = tempEntity.tempId, // ID 유지

                        // 파일 경로 (업로드 전이므로 로컬만 있음)
                        localUri = safeLocalUri,
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
                        syncStatus = "NOT_UPLOADED",
                    )
                    entitiesToMove.add(shareEntity)

                    // 📸 갤러리 저장 (합성 포함)
                    saveImageToGallery(safeLocalUri, tempEntity.subLocalUri, tempEntity.orientation)
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


    // =================================================================
    // Gallery Saving & Image Merging
    // =================================================================

    override suspend fun saveImageToGallery(mainUri: String, subUri: String?, orientation: Int): Boolean {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val mainFile = File(mainUri)
                if (!mainFile.exists()) return@withContext false

                val resolver = context.contentResolver
                val contentValues = android.content.ContentValues().apply {
                    // [Fix] Add UUID to filename to prevent collisions when saving multiple images rapidly
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "CommonProject_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(4)}.jpg")
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/아이랑나랑")
                }

                val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@withContext false

                resolver.openOutputStream(uri)?.use { outputStream ->
                    if (subUri != null && File(subUri).exists()) {
                        // 🖼️ Merge Images (PIP)
                        val mainInternalWithOrientation = rotateBitmapIfNeeded(mainFile.absolutePath, android.graphics.BitmapFactory.decodeFile(mainFile.path), orientation)
                        val subInternalWithOrientation = rotateBitmapIfNeeded(subUri, android.graphics.BitmapFactory.decodeFile(subUri), orientation)

                            if (mainInternalWithOrientation != null && subInternalWithOrientation != null) {
                                val mergedBitmap = combineImages(mainInternalWithOrientation, subInternalWithOrientation)
                                mergedBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
                                mainInternalWithOrientation.recycle()
                                subInternalWithOrientation.recycle()
                                mergedBitmap.recycle()
                            } else {
                                // Fail safe: copy main only
                                java.io.FileInputStream(mainFile).copyTo(outputStream)
                            }
                        } else {
                            // 📄 Single Image: Direct Copy (Exif 보존을 위해 그냥 복사)
                            // 단, 사용자가 "모양이 이상하다"고 했으므로, 여기서도 Rotation을 적용해서 다시 저장하는게 안전할 수 있음.
                            // 하지만 원본 복사가 품질 저하가 없음.
                            // 일단 Single은 원본 복사 + Exif가 갤러리에서 처리되길 기대하지만,
                            // 만약 갤러리 앱이 Exif를 무시하는 커스텀 뷰라면 회전된 비트맵을 저장해야 함.
                            // 안전하게 "비트맵 로드 -> 회전 -> 저장"으로 통일.

                            val original = android.graphics.BitmapFactory.decodeFile(mainFile.path)
                            val rotated = rotateBitmapIfNeeded(mainFile.path, original, orientation)
                            if (rotated != null) {
                                rotated.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
                                rotated.recycle()
                            } else {
                                java.io.FileInputStream(mainFile).copyTo(outputStream)
                            }
                        }
                    }
                    return@withContext true
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@withContext false
                }
            }
        }

    private fun rotateBitmapIfNeeded(path: String, bitmap: android.graphics.Bitmap?, fallbackOrientation: Int = 0): android.graphics.Bitmap? {
        if (bitmap == null) return null
        return try {
            val ei = android.media.ExifInterface(path)
            val orientation = ei.getAttributeInt(
                android.media.ExifInterface.TAG_ORIENTATION,
                android.media.ExifInterface.ORIENTATION_NORMAL
            )

            when (orientation) {
                android.media.ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                android.media.ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                android.media.ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                android.media.ExifInterface.ORIENTATION_NORMAL, android.media.ExifInterface.ORIENTATION_UNDEFINED -> {
                    // Fallback to DB orientation (assuming degrees)
                    if (fallbackOrientation != 0) {
                        rotateImage(bitmap, fallbackOrientation.toFloat())
                    } else {
                        bitmap
                    }
                }
                else -> bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Exception reading EXIF -> Use fallback
            if (fallbackOrientation != 0) {
                 try {
                     rotateImage(bitmap, fallbackOrientation.toFloat())
                 } catch (e2: Exception) {
                     bitmap
                 }
            } else {
                bitmap
            }
        }
    }

    private fun rotateImage(source: android.graphics.Bitmap, angle: Float): android.graphics.Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle)
        return android.graphics.Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun combineImages(mainBitmap: android.graphics.Bitmap, subBitmap: android.graphics.Bitmap): android.graphics.Bitmap {
        val width = mainBitmap.width
        val height = mainBitmap.height
        val result = createBitmap(width, height, mainBitmap.config ?: android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(result)

        // Draw Main
        canvas.drawBitmap(mainBitmap, 0f, 0f, null)

        // Draw Sub (PIP) - Resize to 25% of width
        val subScale = (width * 0.25f) / subBitmap.width
        val subWidth = (subBitmap.width * subScale).toInt()
        val subHeight = (subBitmap.height * subScale).toInt()

        // Position: Bottom-Right with padding
        val padding = 50f
        // [Fix] 우측 하단 좌표 계산: (전체 너비 - 서브 너비 - 패딩, 전체 높이 - 서브 높이 - 패딩)
        val left = width - subWidth - padding
        val top = height - subHeight - padding

        val scaledSub = android.graphics.Bitmap.createScaledBitmap(subBitmap, subWidth, subHeight, true)

        canvas.drawBitmap(scaledSub, left, top, null) // [Fix] Changed from (padding, padding) to (left, top)

        return result
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

    private fun getOrientationDegrees(path: String): Int {
        return try {
            val exifInterface = android.media.ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                android.media.ExifInterface.TAG_ORIENTATION,
                android.media.ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                android.media.ExifInterface.ORIENTATION_ROTATE_90 -> 90
                android.media.ExifInterface.ORIENTATION_ROTATE_180 -> 180
                android.media.ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }
}
