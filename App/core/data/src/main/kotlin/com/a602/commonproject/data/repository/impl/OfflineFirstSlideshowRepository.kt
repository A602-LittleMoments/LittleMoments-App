package com.a602.commonproject.data.repository.impl

import android.content.Context
import com.a602.commonproject.data.model.asExternalModel
import com.a602.commonproject.data.model.toEntity
import com.a602.commonproject.data.repository.SlideshowRepository
import com.a602.commonproject.database.dao.SlideshowDao
import com.a602.commonproject.database.model.SlideshowEntity
import com.a602.commonproject.datastore.datastore.UserPreferencesDataStore
import com.a602.commonproject.model.data.Slideshow
import com.a602.commonproject.network.datasource.SlideshowNetworkDataSource
import com.a602.commonproject.network.model.CreateSlideshowRequest
import com.a602.commonproject.network.model.ExportSlideshowRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class OfflineFirstSlideshowRepository @Inject constructor(
    private val slideshowDao: SlideshowDao,
    private val networkDataSource: SlideshowNetworkDataSource,
    private val userPreferences: UserPreferencesDataStore,
    @ApplicationContext private val context: Context // 파일 저장을 위해 Context 필요
) : SlideshowRepository {

    // =================================================================
    // 1. 목록 조회 (DB -> UI)
    // =================================================================
    override fun getSlideshowsStream(): Flow<List<Slideshow>> =
        slideshowDao.getSlideShows().map { list -> list.map { it.asExternalModel() } }


    // =================================================================
    // 2. 목록 새로고침 (Network -> DB)
    // =================================================================
    override suspend fun refreshSlideshows(): Result<Unit> {
        return try {
            val groupId = getGroupIdOrThrow()

            // 1. 서버에서 목록 가져오기
            val serverList = networkDataSource.getSlideshowList(groupId)

            // 2. DB 업데이트 (주의: 기존에 다운로드된 파일 경로는 유지해야 함!)
            // 현재 DB 상태를 한 번 가져와서 비교합니다.
            val currentDbList = slideshowDao.getSlideShows().first()
            val dbMap = currentDbList.associateBy { it.slideshowId }

            serverList.forEach { summary ->
                val cached = dbMap[summary.slideshowId]

                // Mapper 사용
                var entity = summary.toEntity()

                // ✨ [보완 1] 파일이 실제로 존재하는지 확인 (Ghost File 방지)
                val isFileExists = cached?.localVideoPath?.let { File(it).exists() } == true

                // ✨ [핵심] 이미 다운로드된 상태라면, 로컬 경로와 상태를 유지합니다.
                if (cached != null) {
                    // [Fix] 기존 타이틀(Source Info) 유지
                    entity = entity.copy(title = cached.title)

                    if (cached.status == "DOWNLOADED" && cached.localVideoPath != null) {
                         entity = entity.copy(
                            localVideoPath = cached.localVideoPath,
                            status = "DOWNLOADED"
                        )
                    }
                }

                slideshowDao.insertSlideshow(entity) //
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // =================================================================
    // 3. 생성 요청
    // =================================================================
    override suspend fun createSlideshow(request: CreateSlideshowRequest, title: String): Result<Unit> {
        return try {
            val groupId = getGroupIdOrThrow()

            // 1. 서버에 생성 요청
            val response = networkDataSource.createSlideshow(groupId, request)

            // 2. DB에 "PROCESSING" 상태로 임시 저장 (즉각적인 UI 반응)
            // [Fix] 전달받은 title (키워드/날짜) 사용
            val initialEntity = SlideshowEntity(
                slideshowId = response.slideshowId,
                title = title,
                createAt = System.currentTimeMillis(),
                status = response.status // "QUEUED" or "PROCESSING"
            )
            slideshowDao.insertSlideshow(initialEntity)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 7.1-2 [Overload] 기존 코드 호환용
    override suspend fun createSlideshow(request: CreateSlideshowRequest): Result<Unit> {
        return createSlideshow(request, "추억 영상")
    }

    // =================================================================
    // 4. 상세 정보 동기화
    // =================================================================
    override suspend fun syncSlideshowDetail(slideshowId: String): Result<Unit> {
        return try {
            val groupId = getGroupIdOrThrow()
            val detail = networkDataSource.getSlideshowDetail(groupId, slideshowId)

            // 기존 다운로드 정보 보존 로직
            val current = slideshowDao.getSlideShows().first().find { it.slideshowId == slideshowId }
            var entity = detail.toEntity(slideshowId)

            // 파일이 살아있다면 경로 유지
            // [Fix] Title 유지 로직 추가
            if (current != null) {
                entity = entity.copy(title = current.title)

                if (current.localVideoPath != null && File(current.localVideoPath).exists()) {
                     entity = entity.copy(
                        localVideoPath = current.localVideoPath,
                        status = "DOWNLOADED"
                    )
                }
            }

            slideshowDao.insertSlideshow(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================
    // 5. 영상 다운로드 (안전한 다운로드: tmp -> mp4)
    // =================================================================
    override suspend fun downloadSlideshow(slideshowId: String): Result<File> {
        return withContext(Dispatchers.IO) {
            var tempFile: File? = null
            try {
                val groupId = getGroupIdOrThrow()

                // 1. URL 발급
                val exportResponse = networkDataSource.exportSlideshow(
                    groupId,
                    slideshowId,
                    ExportSlideshowRequest()
                )
                val downloadUrl = exportResponse.data.downloadUrl
                val fileName = "${slideshowId}.mp4"

                // 2. 임시 파일(.tmp) 준비
                val finalFile = File(context.filesDir, fileName)
                tempFile = File(context.filesDir, "${fileName}.tmp")

                // 3. 스트림 다운로드
                URL(downloadUrl).openStream().use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                // 4. 이름 변경 (Atomic Move) -> 성공 시에만 DB 업데이트
                if (tempFile.renameTo(finalFile)) {
                    // [Fix] Title Overwrite 방지: toEntity 안쓰고 수동 업데이트
                    // 기존 정보 가져오기
                    val currentDao = slideshowDao.getSlideShows().first()
                    val current = currentDao.find { it.slideshowId == slideshowId }

                    if (current != null) {
                        val updated = current.copy(
                            localVideoPath = finalFile.absolutePath,
                            status = "DOWNLOADED"
                        )
                        slideshowDao.insertSlideshow(updated)
                    } else {
                        // 만약 DB에 없다면? (거의 없겠지만) -> 그냥 exportResponse 사용하되 title은 파일명
                        val entity = exportResponse.toEntity(finalFile.absolutePath)
                        slideshowDao.insertSlideshow(entity)
                    }

                    Result.success(finalFile)
                } else {
                    throw Exception("파일 저장 중 오류 발생 (Rename Failed)")
                }

            } catch (e: Exception) {
                // 실패 시 임시 파일 정리
                tempFile?.delete()
                Result.failure(e)
            }
        }
    }

    // =================================================================
    // 5-1. 영상 다운로드 (진행률 추적)
    // =================================================================
    override suspend fun downloadSlideshowWithProgress(
        slideshowId: String,
        onProgress: (Float) -> Unit
    ): Result<File> {
        return withContext(Dispatchers.IO) {
            var tempFile: File? = null
            try {
                val groupId = getGroupIdOrThrow()

                // 1. URL 발급
                val exportResponse = networkDataSource.exportSlideshow(
                    groupId,
                    slideshowId,
                    ExportSlideshowRequest()
                )
                val downloadUrl = exportResponse.data.downloadUrl
                val fileName = "${slideshowId}.mp4"

                // 2. 임시 파일(.tmp) 준비
                val finalFile = File(context.filesDir, fileName)
                tempFile = File(context.filesDir, "${fileName}.tmp")

                // 3. 스트림 다운로드 (진행률 추적)
                val connection = URL(downloadUrl).openConnection()
                connection.connect()

                val contentLength = connection.contentLength.toLong()
                var bytesDownloaded = 0L

                connection.getInputStream().use { input ->
                    FileOutputStream(tempFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            bytesDownloaded += bytesRead

                            // 진행률 콜백 (0.0f ~ 1.0f)
                            if (contentLength > 0) {
                                val progress = (bytesDownloaded.toFloat() / contentLength).coerceIn(0f, 1f)
                                withContext(Dispatchers.Main) {
                                    onProgress(progress)
                                }
                            }
                        }
                    }
                }

                // 4. 이름 변경 (Atomic Move) -> 성공 시에만 DB 업데이트
                if (tempFile.renameTo(finalFile)) {
                     // [Fix] Title Overwrite 방지: toEntity 안쓰고 수동 업데이트
                    val currentDao = slideshowDao.getSlideShows().first()
                    val current = currentDao.find { it.slideshowId == slideshowId }

                    if (current != null) {
                        val updated = current.copy(
                            localVideoPath = finalFile.absolutePath,
                            status = "DOWNLOADED"
                        )
                        slideshowDao.insertSlideshow(updated)
                    } else {
                         val entity = exportResponse.toEntity(finalFile.absolutePath)
                         slideshowDao.insertSlideshow(entity)
                    }

                    // 완료 표시
                    withContext(Dispatchers.Main) {
                        onProgress(1f)
                    }

                    // 📸 갤러리에도 저장 (Movies/아이랑나랑)
                    saveVideoToGallery(finalFile)

                    Result.success(finalFile)
                } else {
                    throw Exception("파일 저장 중 오류 발생 (Rename Failed)")
                }

            } catch (e: Exception) {
                // 실패 시 임시 파일 정리
                tempFile?.delete()
                Result.failure(e)
            }
        }
    }

    // =================================================================
    // 6. 삭제
    // =================================================================
    override suspend fun deleteSlideshow(slideshowId: String): Result<Unit> {
        return try {
            // 1. 로컬 파일 경로 확인 후 삭제
            val currentList = slideshowDao.getSlideShows().first()
            val target = currentList.find { it.slideshowId == slideshowId }

            target?.localVideoPath?.let { path ->
                val file = File(path)
                if (file.exists()) file.delete()
            }

            // 2. DB 삭제
            slideshowDao.deleteSlideshow(slideshowId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // 🔒 헬퍼: 그룹 ID 조회
    private suspend fun getGroupIdOrThrow(): String =
        userPreferences.userGroupId.first()
            ?: throw IllegalStateException("그룹 정보가 없습니다.")

    // 🔒 헬퍼: 비디오 갤러리 저장
    private suspend fun saveVideoToGallery(videoFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (!videoFile.exists()) return@withContext false

                val resolver = context.contentResolver
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "CommonProject_Highlight_${System.currentTimeMillis()}.mp4")
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/아이랑나랑")
                }

                val uri = resolver.insert(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return@withContext false

                resolver.openOutputStream(uri)?.use { outputStream ->
                    java.io.FileInputStream(videoFile).copyTo(outputStream)
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}

