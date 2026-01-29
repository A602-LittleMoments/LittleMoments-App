package com.a602.commonproject.data.repository.impl

import android.content.Context
import com.a602.commonproject.data.model.asExternalModel
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.database.dao.MediaDao
import com.a602.commonproject.database.model.ShareMediaEntity
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.network.datasource.MediaNetworkDataSource
import com.a602.commonproject.network.model.MediaFileKey
import com.a602.commonproject.network.model.MediaUploadMetadata
import com.a602.commonproject.network.model.MediaUploadMetadataWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 앱 전체에서 하나만 쓰려면 추가
class OfflineFirstSharedMediaRepository @Inject constructor(
    private val mediaDao: MediaDao,
    private val networkDataSource: MediaNetworkDataSource,
    @ApplicationContext private val context: Context,
) : SharedMediaRepository {

    // =================================================================
    // 📱 1. UI용: 목록 관찰 (Offline-First)
    // =================================================================
    override fun getSharedAlbumStream(): Flow<List<SharedMedia>> {
        // DAO에서 이미 '삭제 예정(TO_BE_DELETED)'은 제외하고 가져와야 함
        return mediaDao.getSharedMediaFlow().map { entities -> entities.map { it.asExternalModel() } }
    }

    // =================================================================
    // ➕ 2. UI용: 미디어 저장 (로컬 선저장 -> 업로드 대기)
    // =================================================================
    override suspend fun saveNewMedia(
        groupId: String,
        mainFile: File,
        subFile: File?,
        caption: String?,
        cameraFacing: String,
    ): Result<Unit> {

        return try {
            // ---------------------------------------------------------
            // [STEP 1] 영구 저장소(FilesDir)에 안전한 폴더 만들기
            // ---------------------------------------------------------
            // Cache 폴더(임시)에 있는 사진은 OS가 언제든 지울 수 있습니다.
            // 따라서 앱 전용 내부 저장소인 filesDir 아래에 'shared_images' 폴더를 만들고 거기로 이사시킵니다.
            val destDir = File(context.filesDir, "shared_images")
            if (!destDir.exists()) destDir.mkdirs()

            // ---------------------------------------------------------
            // [STEP 2] 메인 사진 파일 복사 (Temp -> Permanent)
            // ---------------------------------------------------------
            // 파일명 충돌 방지를 위해 시간+UUID를 조합합니다.
            val newMainFileName = "IMG_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
            val newMainFile = File(destDir, newMainFileName)

            // copyTo: 원본 내용을 새 위치로 복사합니다.
            mainFile.copyTo(newMainFile, overwrite = true)

            // ---------------------------------------------------------
            // [STEP 3] 서브 사진 파일 복사 (존재할 경우)
            // ---------------------------------------------------------
            var newSubFilePath: String? = null
            if (subFile != null && subFile.exists()) {
                val newSubFileName = "SUB_${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
                val newSubFile = File(destDir, newSubFileName)
                subFile.copyTo(newSubFile, overwrite = true)
                newSubFilePath = newSubFile.absolutePath
            }


            // ---------------------------------------------------------
            // [STEP 4] Entity 생성 (경로는 이동된 영구 파일 경로 사용!)
            // ---------------------------------------------------------
            val entity = ShareMediaEntity(
                mediaId = UUID.randomUUID().toString(), // 로컬용 임시 ID
                localUri = newMainFile.absolutePath,    // ✨ 이동된(안전한) 경로 저장
                remoteUrl = null,
                thumbnailUrl = null, // 로컬 이미지는 Glide/Coil이 알아서 로딩함

                subLocalUri = newSubFilePath,           // ✨ 이동된 서브 경로
                subRemoteUrl = null,
                subThumbnailUrl = null,

                cameraFacing = cameraFacing,
                orientation = 0, // 기본값 (필요 시 Exif에서 읽어오거나 파라미터 추가)
                caption = caption,
                type = "PHOTO",

                takenAt = System.currentTimeMillis(),
                uploaderName = "Me", // 아직 서버에 안 갔으니 '나'라고 표시

                syncStatus = "NOT_UPLOADED", // ✨ 업로드 대기 상태
            )

            // ---------------------------------------------------------
            // [STEP 5] DB 저장
            // ---------------------------------------------------------
            // DAO가 List를 받도록 정의되어 있으므로 리스트로 감싸서 전달
            mediaDao.upsertSharedList(listOf(entity))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    // =================================================================
    // 🗑️ 3. UI용: 삭제 요청 (Soft Delete)
    // =================================================================
    override suspend fun deleteMedia(mediaId: String): Result<Unit> {
        return try {
            // DAO의 markAsDeleted 호출 (syncStatus = 'TO_BE_DELETE'로 변경)
            // 화면 목록에서는 즉시 사라지지만, 데이터는 남아서 나중에 Worker가 서버 삭제 요청을 보냄
            mediaDao.markAsDeleted(mediaId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // =================================================================
    // 🚀 4. Worker용: 업로드 동기화
    // =================================================================
    override suspend fun uploadUnsyncedMedia(groupId: String): Boolean {
        return try {
            // 1. 업로드 대기 중인('NOT_UPLOADED') 목록 조회
            val unsyncedList = mediaDao.getUnsyncedMedia()
            if (unsyncedList.isEmpty()) return true

            var allSuccess = true

            unsyncedList.forEach { media ->
                try {
                    val mainFile = media.localUri?.let { File(it) }

                    // --- [Step 1] 메타데이터 조립 (새 모델 적용) ---
                    // 파일이 실제로 존재할 때만 업로드 진행
                    if (mainFile != null && mainFile.exists()) {
                        val subFile = media.subLocalUri?.let { File(it) }


                        // --- [Step 2] 메타데이터 조립 (새 모델 적용) ---
                        val filesToUpload = mutableListOf<File>()
                        filesToUpload.add(mainFile)
                        if (subFile != null && subFile.exists()) {
                            filesToUpload.add(subFile)
                        }


                        val fileMap = mutableMapOf<String, MediaFileKey>()

                        // "rear" 키에 메인 파일 이름 매핑
                        fileMap["rear"] = MediaFileKey(clientFileKey = mainFile.name)

                        // "front" 키에 서브 파일 이름 매핑 (있다면)
                        if (subFile != null && subFile.exists()) {
                            fileMap["front"] = MediaFileKey(clientFileKey = subFile.name)
                        }

                        val metadataItem = MediaUploadMetadata(
                            clientMediaKey = media.mediaId, // 로컬 ID를 키로 사용
                            mediaType = "PHOTO",
                            takenAt = media.takenAt.toString(), // Long -> String 변환
                            cameraFacing = media.cameraFacing,
                            orientation = media.orientation,
                            caption = media.caption,
                            files = fileMap,
                        )

                        // --- [Step 3] 서버 요청 (Batch API) ---
                        //
                        val response = networkDataSource.uploadMediaBatch(
                            groupId = groupId,
                            files = filesToUpload,
                            metadata = MediaUploadMetadataWrapper(listOf(metadataItem)),
                        )

                        // --- [Step 4] 결과 처리 ---
                        //
                        // 내 mediaId(clientMediaKey)에 해당하는 결과를 찾음
                        val result = response.results.find { it.clientMediaKey == media.mediaId }

                        if (result != null && result.status == "SUCCESS") {
                            // 서버가 준 URL 추출 ("main" 키 사용)
                            val remoteRearUrl = result.files?.get("rear")?.storageUrl
                            val remoteFrontUrl = result.files?.get("front")?.storageUrl
                            // "sub" 키 사용 (없으면 null)
                            // (주의: UploadFileResult 구조 확인 필요, storageUrl 사용)
                            // val remoteSubUrl = result.files?.get("sub")?.storageUrl
                            // 현재 DAO markAsSync에는 subRemoteUrl 파라미터가 없으므로
                            // 필요하다면 DAO 수정 후 여기서 넣어줘야 함.
                            // 일단 mainUrl만 업데이트.

                            if (remoteRearUrl != null) {
                                // ✨ [핵심 수정] DAO의 markAsSync 호출
                                // subRemoteUrl도 함께 전달해야 함 (없으면 빈 문자열 처리)
                                mediaDao.markAsSync(
                                    mediaId = media.mediaId,
                                    remoteUrl = remoteRearUrl,
                                    subRemoteUrl = remoteFrontUrl ?: "",
                                )
                                // 참고: markAsSync가 실행되면 DB의 localUri, subLocalUri는 NULL이 됩니다.
                                // 필요하다면 여기서 실제 파일(mainFile, subFile)을 삭제해도 됩니다.
                            }
                        } else {
                            allSuccess = false
                        }
                    } else {
                        // 파일 유실 -> 삭제
                        mediaDao.hardDelete(media.mediaId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    allSuccess = false // 하나라도 실패하면 Retry를 위해 false 반환
                }
            }
            allSuccess
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // =================================================================
    // ✂️ 5. Worker용: 삭제 동기화
    // =================================================================
    override suspend fun syncDeletedMedia(groupId: String): Boolean {
        return try {
            val toDeleteList = mediaDao.getDeletedMedia()
            if (toDeleteList.isEmpty()) return true

            var allSuccess = true

            toDeleteList.forEach { media ->
                try {
                    // 서버에 올라간 데이터라면 API 호출
                    if (!media.remoteUrl.isNullOrBlank()) { // 데이터가 올라가 있으면 삭제
                        networkDataSource.deleteMedia(groupId, media.mediaId)
                    }
                    // 로컬 완전 삭제
                    mediaDao.hardDelete(media.mediaId)
                } catch (e: Exception) {
                    allSuccess = false
                }
            }

            allSuccess
        } catch (e: Exception) {
            false
        }
    }

    // =================================================================
    // ☁️ 6. Worker용: 다운로드 동기화
    // =================================================================
    override suspend fun syncWithServer(groupId: String): Boolean {
        return try {
            val response = networkDataSource.getAlbums(groupId = groupId, limit = 1000)

            val entities = response.medias.map { remote ->
                ShareMediaEntity(
                    mediaId = remote.mediaId,
                    localUri = null,

                    remoteUrl = remote.storageUrl,
                    thumbnailUrl = remote.thumbUrl,

                    subLocalUri = null,
                    subRemoteUrl = remote.subStorageUrl,
                    subThumbnailUrl = remote.subThumbUrl,

                    cameraFacing = remote.cameraFacing,
                    orientation = remote.orientation,
                    caption = remote.caption,
                    type = remote.mediaType,

                    // 날짜 변환 (String -> Long)
                    takenAt = try {
                        remote.takenAt.toLong()
                    } catch (e: Exception) {
                        System.currentTimeMillis()
                    },

                    uploaderName = remote.uploadedBy.nickname,

                    syncStatus = "SYNCED",
                )
            }

            // DAO의 스마트 동기화 (Chunking + Dirty Checking)
            mediaDao.syncSharedList(entities)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
