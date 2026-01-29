package com.a602.commonproject.data.repository

import com.a602.commonproject.model.data.Baby
import java.io.File
import kotlinx.coroutines.flow.Flow

interface BabyRepository {
    /**
     * 아기 목록 조회 (DB 관찰)
     */
    fun getBabyStream(): Flow<List<Baby>>

    /**
    * 아기 등록 (API 호출 -> DB 저장)
    * - 반환값: Result<Unit>
    */
    suspend fun addBaby(
        name: String,
        birthDate: String, // "yyyy-MM-dd"
        gender: Baby.Gender,
        imageFile: File?
    ): Result<Unit>

    /**
     * 아기 정보 수정 (API 호출 -> DB 저장)
     * - 현재 API 구조상 수정 시에는 사진 업로드를 지원하지 않아 정보만 수정합니다.
     */
    suspend fun updateBaby(
        babyId: String,
        name: String,
        birthDate: String,
        gender: Baby.Gender,
        imageFile: File?
    ): Result<Unit>

    /**
     * 아기 삭제
     */
    suspend fun deleteBaby(groupId: String, babyId: String): Result<Unit>

    /**
     * 서버 동기화 (Worker용)
     * - 서버 데이터를 가져와 로컬 DB를 최신 상태로 갱신합니다.
     */
    suspend fun syncWithServer(groupId: String): Boolean

}
