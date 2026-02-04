package com.a602.commonproject.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.a602.commonproject.database.model.MediaBabyCrossRefEntity
import com.a602.commonproject.database.model.ShareMediaEntity
import com.a602.commonproject.database.model.TempMediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    // 임시 앨번을 내림차순으로 조회
    @Query("SELECT * FROM temp_media ORDER BY takenAt DESC")
    fun getTempMediaFlow() : Flow<List<TempMediaEntity>>


    // 공유 앨범을 내림차순으로 조회 (Legacy List)
    @Query("SELECT * FROM shared_media WHERE syncStatus !='TO_BE_DELETE' ORDER BY takenAt DESC")
    fun getSharedMediaFlow(): Flow<List<ShareMediaEntity>>

    // 공유 앨범을 내림차순으로 조회 (Paging 3)
    @Query("SELECT * FROM shared_media WHERE syncStatus !='TO_BE_DELETE' ORDER BY takenAt DESC")
    fun getSharedMediaPagingSource(): PagingSource<Int, ShareMediaEntity>

    // 상세 조회용
    @Query("SELECT * FROM shared_media WHERE mediaId = :mediaId")
    fun getSharedMediaById(mediaId: String): Flow<ShareMediaEntity?>

    // 상세 조회용
    @Query("SELECT * FROM temp_media WHERE tempId = :tempId")
    fun getTempMediaById(tempId: String): Flow<TempMediaEntity?>

    // 현재 공유 앨범에서의 Sync 상태를 불러오는 부분 (삭제, 올라감, 아직 안올라감)
    @Query("SELECT syncStatus FROM shared_media WHERE mediaId = :mediaId")
    suspend fun getSyncStatus(mediaId: String): String?

    // 캡션 업데이트 (로컬 DB 즉시 반영용)
    @Query("UPDATE shared_media SET caption = :caption WHERE mediaId = :mediaId")
    suspend fun updateCaption(mediaId: String, caption: String)


    // 임시 앨범 Upsert 하는 부분
    @Upsert
    suspend fun upsertTempList(tempList : List<TempMediaEntity>)

    // 공유 앨범 Upsert 하는 부분
    @Upsert
    suspend fun upsertSharedList(sharedList : List<ShareMediaEntity>)


    // 업로드해야 할 파일 조회 -> WorkManager 용
    @Query("SELECT * FROM shared_media WHERE syncStatus = 'NOT_UPLOADED'")
    suspend fun getUnsyncedMedia () : List<ShareMediaEntity>

    // 지워야 할 목록 가져오기  -> WorkManager 용
    @Query("SELECT * FROM shared_media WHERE syncStatus = 'TO_BE_DELETE'")
    suspend fun getDeletedMedia(): List<ShareMediaEntity>

    // Dirty Checking용 헬퍼 쿼리 (로컬 작업 중인 ID 목록 한 번에 가져오기) -> 서버에서 데이터를 가져 올때 사용 ㅎ
    //@Query("SELECT mediaId FROM shared_media WHERE syncStatus IN ('TO_BE_DELETE', 'NOT_UPLOADED')")
    @Query("""
        SELECT mediaId
        FROM shared_media
        WHERE mediaId IN (:targetIds)
        AND syncStatus IN ('TO_BE_DELETE', 'NOT_UPLOADED')
    """)
    suspend fun getDirtyMediaIds(targetIds: List<String>): List<String>

    // 업로드 성공 처리 (로컬 경로는 삭제)
    @Query("UPDATE shared_media SET syncStatus = 'SYNCED', remoteUrl = :remoteUrl, localUri = NULL, subRemoteUrl = :subRemoteUrl, subLocalUri = NULL WHERE mediaId = :mediaId")
    suspend fun markAsSync(mediaId: String, remoteUrl : String, subRemoteUrl: String)


    // --- 서버와 동기화 될 부분에서의 삭제 로직 ---
    // Soft Delete -> 상태 변경
    @Query("UPDATE shared_media SET syncStatus = 'TO_BE_DELETE' WHERE mediaId = :mediaId")
    suspend fun markAsDeleted(mediaId: String)

    // Hard Delete -> 실제로 DB에서 삭제
    @Query("DELETE FROM shared_media WHERE mediaId = :mediaId")
    suspend fun hardDelete(mediaId: String)

    // ✨ [추가] 배치 삭제 (중복 제거용)
    @Query("DELETE FROM shared_media WHERE mediaId IN (:ids)")
    suspend fun hardDeleteByIds(ids: List<String>)

    // ✨ [추가] 배치 조회 (중복 확인용)
    @Query("SELECT * FROM shared_media WHERE mediaId IN (:ids)")
    suspend fun getSharedMediaListByIds(ids: List<String>): List<ShareMediaEntity>


    // --- 날짜가 지나고 삭제할 때 사용할 것들 ---
    // 1. 만료된 목록 가져오기 (Repository에서 파일을 먼저 지우기 위함)
    @Query("SELECT * FROM temp_media WHERE expirationDate < :currentTimeMillis")
    suspend fun getExpiredTempMedia(currentTimeMillis: Long): List<TempMediaEntity>

    // ✨ [추가] 삭제 전 파일 경로 확보를 위한 일괄 조회
    @Query("SELECT * FROM temp_media WHERE tempId IN (:ids)")
    suspend fun getTempMediaListByIds(ids: List<String>): List<TempMediaEntity>

    // 2. 파일 삭제 확인 후 DB에서 제거 (기존 쿼리 유지 또는 ID 리스트로 삭제) -> repo에서 호출
    @Query("DELETE FROM temp_media WHERE tempId IN (:ids)")
    suspend fun deleteTempMediaByIds(ids: List<String>)

    // [안정성 확보] 삭제 직전에 데이터가 여전히 임시 테이블에 있는지 확인 (Double Check)
    // EXISTS는 데이터 내용을 다 읽지 않고 존재 여부만 1/0으로 리턴하므로 매우 빠릅니다.
    @Query("SELECT EXISTS(SELECT 1 FROM temp_media WHERE tempId = :tempId)")
    suspend fun checkIfTempExists(tempId: String): Boolean



    // 임시에서 공유로 이동하는 트랜잭션
    @Transaction
    suspend fun moveToShared(tempIds: List<String>, newShareEntityList: List<ShareMediaEntity>){
        upsertSharedList(newShareEntityList)
        deleteTempMediaByIds(tempIds)
    }

    // 오프라인에서 지운 사진이 온라인에서 살아 날 거이기 때문에
    // 설명: 서버 데이터를 로컬에 덮어쓰기 전에, 로컬에서 '작업 중인(Dirty)' 데이터는 보호함.
    // [Sync] 서버 데이터 반영
    // N+1 문제를 해결하고, 로컬 작업(삭제/업로드중)을 보호합니다.
    @Transaction
    suspend fun syncSharedList(serverList: List<ShareMediaEntity>) {
        // 데이터가 없으면 바로 종료 (불필요한 연산 방지)
        if (serverList.isEmpty()) return

        // 1. [핵심] 리스트를 900개씩 쪼갭니다.
        // 이유: SQLite의 변수 제한(999개)을 넘지 않기 위해 안전하게 900으로 설정
        val chunks = serverList.chunked(900)

        // 2. 쪼개진 덩어리(chunk) 단위로 반복문을 돕니다.
        // (예: 2000개면 루프 3번 돔)
        chunks.forEach { chunkedList ->

            // --- 여기서부터는 '전체 리스트'가 아니라 '쪼개진 리스트(chunkedList)'만 씁니다 ---

            // A. ID 추출
            val targetIds = chunkedList.map { it.mediaId }

            // B. Dirty Checking (이 900개 안에서만 검사)
            val dirtyIds = getDirtyMediaIds(targetIds).toSet()

            // C. 필터링 (로컬 작업 중인 데이터 제외)
            val validData = chunkedList.filterNot { serverItem ->
                dirtyIds.contains(serverItem.mediaId)
            }

            // D. 저장 (900개 이하이므로 안전하게 저장됨)
            if (validData.isNotEmpty()) {
                upsertSharedList(validData)
            }
        }
    }

    // =================================================================
    // 👶 아기별 필터링을 위한 추가 쿼리 (Join)
    // =================================================================

    // 매핑 테이블 저장
    @Upsert
    suspend fun upsertMediaBabyCrossRefs(crossRefs: List<MediaBabyCrossRefEntity>)

    // 특정 아기의 사진만 조회 (Paging 3)
    @Transaction
    @Query("""
        SELECT * FROM shared_media
        INNER JOIN media_baby_cross_ref ON shared_media.mediaId = media_baby_cross_ref.mediaId
        WHERE media_baby_cross_ref.babyId = :babyId
        AND syncStatus != 'TO_BE_DELETE'
        ORDER BY takenAt DESC
    """)
    fun getSharedMediaPagingSourceByBaby(babyId: String): PagingSource<Int, ShareMediaEntity>

    // 특정 아기 + 날짜 범위 조회 (년도별 필터링용)
    @Transaction
    @Query("""
        SELECT * FROM shared_media
        INNER JOIN media_baby_cross_ref ON shared_media.mediaId = media_baby_cross_ref.mediaId
        WHERE media_baby_cross_ref.babyId = :babyId
        AND syncStatus != 'TO_BE_DELETE'
        AND takenAt >= :startMillis AND takenAt <= :endMillis
        ORDER BY takenAt DESC
    """)
    fun getSharedMediaPagingSourceByBabyAndDateRange(babyId: String, startMillis: Long, endMillis: Long): PagingSource<Int, ShareMediaEntity>


    // =================================================================
    // 👶 UI List Filtering Methods (Non-Paging)
    // =================================================================

    // 특정 아기의 사진만 조회 (Flow List)
    @Transaction
    @Query("""
        SELECT * FROM shared_media
        INNER JOIN media_baby_cross_ref ON shared_media.mediaId = media_baby_cross_ref.mediaId
        WHERE media_baby_cross_ref.babyId = :babyId
        AND syncStatus != 'TO_BE_DELETE'
        ORDER BY takenAt DESC
    """)
    fun getSharedMediaFlowByBaby(babyId: String): Flow<List<ShareMediaEntity>>

    // 특정 아기 + 날짜 범위 조회 (Flow List)
    @Transaction
    @Query("""
        SELECT * FROM shared_media
        INNER JOIN media_baby_cross_ref ON shared_media.mediaId = media_baby_cross_ref.mediaId
        WHERE media_baby_cross_ref.babyId = :babyId
        AND syncStatus != 'TO_BE_DELETE'
        AND takenAt >= :startMillis AND takenAt <= :endMillis
        ORDER BY takenAt DESC
    """)
    fun getSharedMediaFlowByBabyAndDateRange(babyId: String, startMillis: Long, endMillis: Long): Flow<List<ShareMediaEntity>>
}
