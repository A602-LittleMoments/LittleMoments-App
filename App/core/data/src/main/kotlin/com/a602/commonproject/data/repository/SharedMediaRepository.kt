package com.a602.commonproject.data.repository

import com.a602.commonproject.model.data.SharedMedia
import java.io.File
import kotlinx.coroutines.flow.Flow

interface SharedMediaRepository {
    /**
     * 🖼️ 공유 앨범 목록 관찰 (Offline-First)
     * - DB의 변경 사항을 실시간으로 UI에 반영
     */
    fun getSharedAlbumStream(groupId: String): Flow<List<SharedMedia>>

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
     * ☁️ 서버 동기화 (Download)
     * - 서버의 최신 목록을 받아와 로컬 DB를 갱신
     */
    suspend fun syncWithServer(groupId: String): Boolean
    /**
     * 🚀 미전송 미디어 업로드 (Upload)
     * - DB에서 'NOT_UPLOADED' 상태인 항목들을 찾아 서버로 전송
     */
    suspend fun uploadUnsyncedMedia(groupId: String): Boolean

    /**
     * 🗑️ 미디어 삭제
     * - 로컬 DB 삭제 + 서버 삭제 요청
     */
    suspend fun deleteMedia(mediaId: String): Result<Unit>


    suspend fun syncWithServer() : Boolean

    suspend fun uploadUnsyncedMedia() :Boolean
    fun syncDeletedMedia(groupId: String) : Boolean
}
