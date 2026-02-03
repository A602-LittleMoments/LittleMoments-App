package com.a602.commonproject.data.repository

import androidx.paging.PagingData
import com.a602.commonproject.model.data.SharedMedia
import java.io.File
import kotlinx.coroutines.flow.Flow

interface SharedMediaRepository {

    /**
     * 🖼️ 공유 앨범 목록 관찰 (Offline-First)
     * - DB의 변경 사항을 실시간으로 UI에 반영 (기존 유지)
     */
    fun getSharedAlbumStream(): Flow<List<SharedMedia>>

    /**
     * 🖼️ 공유 앨범 목록 관찰 (Paging 3)
     * - 최적화된 Paging 데이터 스트림
     */
    fun getSharedAlbumPagingStream(babyId: String? = null): Flow<PagingData<SharedMedia>>

    /**
     * ➕ 새로운 미디어 추가 (업로드 대기 상태로 저장)
     * - TempMedia에서 이동해오거나, 바로 촬영한 사진을 저장할 때 사용
     * - 저장 직후 uploadUnsyncedMedia를 호출하면 즉시 업로드 효과
     */
    suspend fun saveNewMedia(
        groupId: String,
        mainFile: File, // 뒷면
        subFile: File?, // 듀얼 카메라일 경우 (없으면 null) 앞면
        caption: String?,
        cameraFacing: String = "REAR"
    ): Result<Unit>

    /**
     * 사진 삭제 요청
     * - DB에서 바로 지우지 않고, 상태만 '삭제 예정(TO_BE_DELETED)'으로 변경합니다. (Soft Delete)
     * - 화면에서는 안 보이게 되고, 이후 WorkManager가 서버 삭제를 수행합니다.
     * - 로컬 DB 삭제 + 서버 삭제 요청
     */
    suspend fun deleteMedia(mediaId: String): Result<Unit>
    // 캡션 업데이트용
    suspend fun updateCaption(mediaId: String, caption: String): Result<Unit>

    // =================================================================
    // ⚙️ Worker 용 (백그라운드 동기화)
    // =================================================================

    /**
     * [UploadWorker 사용] 삭제 동기화
     * - '삭제 예정'인 항목들을 찾아 서버에 삭제 요청을 보냅니다.
     * - 서버 삭제 성공 시, 로컬 DB에서도 완전히 지웁니다(Hard Delete).
     */
    suspend fun syncDeletedMedia(groupId: String): Boolean

    /**
     * ☁️ 서버 동기화 (Download)
     * - 서버의 최신 목록을 받아와 로컬 DB를 갱신
     */
    suspend fun syncWithServer(groupId: String, filterByUserId: String? = null): Boolean
    /**
     * 🚀 미전송 미디어 업로드 (Upload)
     * - DB에서 'NOT_UPLOADED' 상태인 항목들을 찾아 서버로 전송
     */
    suspend fun uploadUnsyncedMedia(groupId: String): Boolean




}
