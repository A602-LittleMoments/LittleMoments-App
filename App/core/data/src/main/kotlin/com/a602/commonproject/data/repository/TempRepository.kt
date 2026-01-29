package com.a602.commonproject.data.repository

import com.a602.commonproject.model.data.TempMedia
import java.io.File
import kotlinx.coroutines.flow.Flow

/**
 * 📸 TempMediaRepository
 * 촬영 직후 '임시 저장소'에 있는 미디어를 관리합니다.
 * (자동 삭제 로직 및 공유 앨범으로의 이동 포함)
 */
interface TempMediaRepository {

    // 1. 임시 미디어 목록 실시간 조회 (촬영일 내림차순)
    fun getTempMediaStream(): Flow<List<TempMedia>>

    // 2. 특정 미디어 상세 조회
    fun getTempMedia(tempId: String): Flow<TempMedia?>

    // 3. 촬영한 사진 저장 (DB + 만료시간 설정)
    suspend fun saveTempMedia(
        tempId: String,       // UUID
        file: File,           // 저장된 파일
        takenAt: Long,        // 촬영 시간
        orientation: Int,     // 회전 정보
        cameraFacing: String  // 전/후면 정보
    ): Result<Unit>

    // 4. 선택한 미디어 삭제 (파일 삭제 + DB 삭제)
    suspend fun deleteTempMedia(ids: List<String>): Result<Unit>

    // 5. 만료된 미디어 정리 (자동 삭제 Worker용)
    // - 반환값: 삭제된 파일 개수
    suspend fun cleanupExpiredMedia(): Result<Int>

    // 6. 공유 앨범으로 이동 (선택한 사진들을 업로드 대기 상태로 전환)
    suspend fun moveToShared(
        tempIds: List<String>,
    ): Result<Unit>
}
