package com.a602.commonproject.data.repository.impl

import com.a602.commonproject.data.model.asExternalModel
import com.a602.commonproject.data.model.toEntity
import com.a602.commonproject.data.repository.BabyRepository
import com.a602.commonproject.database.dao.BabyDao
import com.a602.commonproject.datastore.datastore.UserPreferencesDataStore
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.network.datasource.BabyNetworkDataSource
import com.a602.commonproject.network.model.BabyRequest
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class OfflineFirstBabyRepository @Inject constructor(
    private val babyDao: BabyDao,
    private val networkDataSource: BabyNetworkDataSource,
    private val userPreferences: UserPreferencesDataStore
) : BabyRepository {

    private suspend fun getGroupIdOrThrow(): String {
        // userPreferences.userGroupId는 Flow이므로 first()로 현재 값을 스냅샷처럼 가져옵니다.
        return userPreferences.userGroupId.first()
            ?: throw IllegalStateException("로그인된 그룹 정보가 없습니다.")
    }

    // =================================================================
    // 📱 1. 목록 조회
    // =================================================================
    override fun getBabyStream(): Flow<List<Baby>> {
        return babyDao.getAllBabies().map { entities -> entities.map { it.asExternalModel() } }
    }
    // =================================================================
    // ➕ 2. 아기 등록
    // =================================================================
    override suspend fun addBaby(
        name: String,
        birthDate: String,
        gender: Baby.Gender,
        imageFile: File?
    ): Result<Unit> {
        return try {
            val groupId = getGroupIdOrThrow() // ✨ DataStore에서 꺼냄

            // 1. 요청 객체 생성
            val request = BabyRequest(
                babyName = name,
                birthDate = birthDate,
                gender = when (gender) {
                    Baby.Gender.MALE -> "M"
                    Baby.Gender.FEMALE -> "F"
                    else -> "U"
                },
            )

            // 2. 서버 API 호출
            val response = networkDataSource.addBaby(
                groupId = groupId,
                babyRequest = request,
                imageFile = imageFile
            )

            // 3. 응답(ID 포함)을 로컬 DB 저장
            babyDao.insertBaby(response.toEntity())

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    // =================================================================
    // ✏️ 3. 아기 정보 수정 (Multipart + Sync)
    // =================================================================
    override suspend fun updateBaby(
        babyId: String,
        name: String,
        birthDate: String,
        gender: Baby.Gender,
        imageFile: File?
    ): Result<Unit> {
        return try {
            val groupId = getGroupIdOrThrow() // ✨ DataStore에서 꺼냄

            // 1. 요청 객체 생성
            val request = BabyRequest(
                babyId = babyId,
                babyName = name,
                birthDate = birthDate,
                gender = when (gender) {
                    Baby.Gender.MALE -> "M"
                    Baby.Gender.FEMALE -> "F"
                    else -> "U"
                }
            )

            // 2. 서버 API 호출
            val response = networkDataSource.updateBaby(
                babyId = babyId,
                groupId = groupId,
                babyRequest = request,
                imageFile = imageFile,
            )

            // 3. 응답(ID 포함)을 로컬 DB 저장
            babyDao.insertBaby(response.toEntity())

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // =================================================================
    // 🗑️ 4. 아기 삭제
    // =================================================================
    override suspend fun deleteBaby(groupId: String, babyId: String): Result<Unit> {
        return try {
            val groupId = getGroupIdOrThrow()

            networkDataSource.deleteBaby(groupId, babyId)
            babyDao.deleteBaby(babyId)

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun syncWithServer(groupId: String): Boolean {
        return try {
            val groupId = getGroupIdOrThrow()

            // 1. 서버 목록 가져오기
            val remoteResponse = networkDataSource.getBabies(groupId)
            val remoteList = remoteResponse.babies

            // 2. 로컬 DB 갱신 (덮어쓰기)
            remoteList.forEach { remote ->
                babyDao.insertBaby(remote.toEntity())
            }

            // 3. 로컬에만 있는 좀비 데이터 정리
            val remoteIds = remoteList.map { it.babyId }.toSet()
            val localList = babyDao.getAllBabies().first()
            localList.forEach { local ->
                if (local.babyId !in remoteIds) {
                    babyDao.deleteBaby(local.babyId)
                }
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
