package com.a602.commonproject.data.repository.impl

import android.content.Context
import com.a602.commonproject.data.model.asExternalModel
import com.a602.commonproject.data.repository.SharedMediaRepository
import com.a602.commonproject.database.dao.MediaDao
import com.a602.commonproject.database.model.ShareMediaEntity
import com.a602.commonproject.datastore.datastore.UserPreferencesDataSource
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.network.datasource.MediaNetworkDataSource
import com.a602.commonproject.network.model.MediaFileKey
import com.a602.commonproject.network.model.MediaUploadMetadata
import com.a602.commonproject.network.model.MediaUploadMetadataWrapper
import com.a602.commonproject.network.model.UpdateCaptionRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.a602.commonproject.database.dao.BabyDao


class OfflineFirstSharedMediaRepository @Inject constructor(
    private val mediaDao: MediaDao,
    private val babyDao: BabyDao, // Direct Access (Repository might be better but circular dependency concerns)
    private val networkDataSource: MediaNetworkDataSource,
    private val userPreferences: UserPreferencesDataSource,
    @ApplicationContext private val context: Context,
) : SharedMediaRepository {

    // =================================================================

    // =================================================================
    // 📱 1. UI용: 목록 관찰 (Offline-First) - Legacy List
    // =================================================================
    override fun getSharedAlbumStream(babyId: String?, year: Int?): Flow<List<SharedMedia>> {
        return if (babyId != null) {
            if (year != null) {
                 val zoneId = java.time.ZoneId.systemDefault()
                 val start = java.time.LocalDate.of(year, 1, 1).atStartOfDay(zoneId).toInstant().toEpochMilli()
                 val end = java.time.LocalDate.of(year, 12, 31).atTime(23, 59, 59).atZone(zoneId).toInstant().toEpochMilli()
                 mediaDao.getSharedMediaFlowByBabyAndDateRange(babyId, start, end)
                     .map { entities -> entities.map { it.asExternalModel() } }
            } else {
                 mediaDao.getSharedMediaFlowByBaby(babyId)
                     .map { entities -> entities.map { it.asExternalModel() } }
            }
        } else {
             mediaDao.getSharedMediaFlow().map { entities -> entities.map { it.asExternalModel() } }
        }
    }

    // =================================================================
    // 📱 1-1. UI용: 목록 관찰 (Paging 3) - Optimized for Home
    // =================================================================
    override fun getSharedAlbumPagingStream(babyId: String?, year: Int?): Flow<PagingData<SharedMedia>> {
        // Paging 3: Pager 구성
        return Pager(
            config = PagingConfig(
                pageSize = 30,              // 한 번에 가져올 페이지 크기
                enablePlaceholders = false,  // null placeholder 사용 안 함
                initialLoadSize = 90        // 처음 로딩 시 3배수 정도 로드
            ),
            pagingSourceFactory = {
                if (babyId != null) {
                    if (year != null) {
                        // 날짜 범위 계산 (해당 연도 1월 1일 ~ 12월 31일)
                        // Local Time 기준 (사용자가 인식하는 날짜)
                        val zoneId = java.time.ZoneId.systemDefault()
                        val start = java.time.LocalDate.of(year, 1, 1).atStartOfDay(zoneId).toInstant().toEpochMilli()
                        val end = java.time.LocalDate.of(year, 12, 31).atTime(23, 59, 59).atZone(zoneId).toInstant().toEpochMilli()
                        
                        mediaDao.getSharedMediaPagingSourceByBabyAndDateRange(babyId, start, end)
                    } else {
                        mediaDao.getSharedMediaPagingSourceByBaby(babyId)
                    }
                } else {
                    // 전체 보기에서는 연도 필터링 미지원 (혹은 필요시 추가)
                    mediaDao.getSharedMediaPagingSource()
                }
            }
        ).flow.map { pagingData ->
            // Entity -> Model 변환
            pagingData.map { it.asExternalModel() }
        }
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

                takenAt = java.time.Instant.now().toEpochMilli(),
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
            // 1. DAO의 markAsDeleted 호출 (UI 즉시 반영 - Optimistic Update)
            mediaDao.markAsDeleted(mediaId)

            try {
                // 2. 서버 즉시 삭제 시도
                val groupId = getGroupIdOrThrow()
                networkDataSource.deleteMedia(groupId, mediaId)

                // 3. 서버 삭제 성공 시 로컬 완전 삭제
                mediaDao.hardDelete(mediaId)
            } catch (e: Exception) {
                // 네트워크 오류 등으로 실패 시, '삭제 예정' 상태로 유지 -> Worker가 처리
                e.printStackTrace()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            // DB 오류 등 심각한 문제
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
                            // 서버가 ISO-8601 포맷을 반환하므로, 업로드 시에도 포맷을 맞춰줌
                            // Long(millis) -> ISO String ("2023-10-27T10:00:00Z")
                            takenAt = java.time.Instant.ofEpochMilli(media.takenAt).toString(),
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
                                // ✨ [핵심 로직 개선] ID 교체 (Local UUID -> Server ID)
                                // 서버가 발급해준 실제 ID(`result.mediaId`)로 로컬 DB를 업데이트합니다.
                                // 이렇게 해야 나중에 `getAlbums`로 목록을 받아올 때 중복이 생기지 않습니다.
                                
                                val newServerId = result.mediaId // 배치 응답에서 Server ID 획득

                                if (!newServerId.isNullOrBlank() && newServerId != media.mediaId) {
                                    // 1. 새로운 ID를 가진 엔티티 생성 (기존 정보 복사 + ID 변경 + 상태 SYNCED)
                                    val newEntity = media.copy(
                                        mediaId = newServerId,
                                        syncStatus = "SYNCED",
                                        remoteUrl = remoteRearUrl,
                                        subRemoteUrl = remoteFrontUrl ?: "",
                                        // localUri 등은 그대로 유지됨
                                    )
                                    
                                    // 2. 새 엔티티 저장 (Insert)
                                    mediaDao.upsertSharedList(listOf(newEntity))
                                    
                                    // 3. 구 엔티티(임시 ID) 삭제 (Delete)
                                    mediaDao.hardDelete(media.mediaId)
                                } else {
                                    // ID가 같거나(그럴리 없지만) Server ID가 없으면 기존 방식대로 업데이트
                                    mediaDao.markAsSync(
                                        mediaId = media.mediaId,
                                        remoteUrl = remoteRearUrl,
                                        subRemoteUrl = remoteFrontUrl ?: "",
                                    )
                                }
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
    // ☁️ 6. Worker용: 다운로드 동기화 (아기 별 순회)
    // =================================================================
    override suspend fun syncWithServer(groupId: String, filterByUserId: String?): Boolean {
        return try {
            // 1. 아기 목록 조회
            val babies = babyDao.getAllBabies().first() // Flow -> List

            // 2. 전체 조회 (기존 로직 - 안전망) + 아기 별 조회
            // 우선, "전체"를 한 번 긁을지 말지 고민.
            // 일단 아기 별로 API가 있으니, 각 아기 순회하며 매핑 테이블 채워넣어야 함.
            // 아기 없으면? 전체라도 긁어야 하나? -> 아기가 없으면 사진도 없을 확률 높음(기획상).

            // 전체 사진 (babyId = null) 조회 -> 전체 사진 리스트 update (기본)
             val globalResponse = networkDataSource.getAlbums(groupId = groupId, limit = 1000, filterByUserId = filterByUserId)
             processAndSave(globalResponse.medias)

            // 3. 각 아기 별로 순회
            babies.forEach { baby ->
                val response = networkDataSource.getAlbums(groupId = groupId, limit = 1000, babyId = baby.babyId, filterByUserId = filterByUserId)

                // 3-1. 미디어 저장 (중복처리는 Dao upsert가 함)
                val entities = processAndSave(response.medias)

                // 3-2. 매핑 테이블 저장
                val crossRefs = entities.map {  media ->
                    com.a602.commonproject.database.model.MediaBabyCrossRefEntity(
                        mediaId = media.mediaId,
                        babyId = baby.babyId
                    )
                }
                if (crossRefs.isNotEmpty()) {
                    mediaDao.upsertMediaBabyCrossRefs(crossRefs)
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 응답 -> 엔티티 변환 및 저장 헬퍼
    // 응답 -> 엔티티 변환 및 저장 헬퍼
    private suspend fun processAndSave(remoteList: List<com.a602.commonproject.network.model.MediaResponse>): List<ShareMediaEntity> {
        // [중복 방지 로직]
        // 1. 서버에서 온 ID 리스트 추출
        val remoteIds = remoteList.map { it.mediaId }

        // 2. 이미 로컬에 저장된 항목이 있는지 확인 (Server ID 기준)
        // (UploadWorker에서 업로드 성공 시 ID를 Server ID로 교체해두었으므로 매칭됩니다.)
        val existingMap = if (remoteIds.isNotEmpty()) {
            mediaDao.getSharedMediaListByIds(remoteIds).associateBy { it.mediaId }
        } else {
            emptyMap()
        }

        val entities = remoteList.map { remote ->
            // 기존에 로컬에 있던 데이터(원본 파일 경로 등)를 가져옵니다.
            val existing = existingMap[remote.mediaId]

            ShareMediaEntity(
                mediaId = remote.mediaId,
                // ✨ [핵심] 기존에 로컬 파일 경로가 있다면 유지합니다. (없으면 null)
                // 이렇게 하면 다시 다운로드할 필요 없이 바로 고화질 원본을 볼 수 있습니다.
                localUri = existing?.localUri, 

                remoteUrl = remote.storageUrl,
                thumbnailUrl = remote.thumbUrl,

                // ✨ [핵심] 서브(전면) 카메라도 동일하게 경로 유지
                subLocalUri = existing?.subLocalUri,
                subRemoteUrl = remote.subStorageUrl,
                subThumbnailUrl = remote.subThumbUrl,

                cameraFacing = remote.cameraFacing,
                orientation = remote.orientation,
                caption = remote.caption,
                type = remote.mediaType,

                takenAt = try {
                    val numeric = remote.takenAt.trim().toDoubleOrNull()
                    numeric?.toLong() ?: java.time.OffsetDateTime.parse(remote.takenAt).toInstant().toEpochMilli()
                } catch (e: Exception) {
                    android.util.Log.e("SharedMediaRepo", "Date parsing failed.", e)
                    System.currentTimeMillis()
                },

                uploaderName = remote.uploadedBy.nickname,
                syncStatus = "SYNCED",
            )
        }
        
        if (entities.isNotEmpty()) {
            mediaDao.syncSharedList(entities)
        }
        
        return entities
    }


//        캡션
    override suspend fun updateCaption(mediaId: String, caption: String): Result<Unit> {
    return try {
        val groupId = getGroupIdOrThrow()

        networkDataSource.updateCaption(groupId, mediaId, UpdateCaptionRequest(caption))

        mediaDao.updateCaption(mediaId, caption)


        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
    private suspend fun getGroupIdOrThrow(): String =
        userPreferences.userGroupId.first()
            ?: throw IllegalStateException("그룹 정보가 없습니다.")
}
