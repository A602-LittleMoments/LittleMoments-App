package com.a602.commonproject.data.repository.impl

import com.a602.commonproject.data.model.asExternalModel
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.datastore.datastore.UserPreferencesDataStore
import com.a602.commonproject.model.data.Collection
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.network.datasource.CollectionNetworkDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.first


class NetworkCollectionRepository @Inject constructor(
    private val networkDataSource: CollectionNetworkDataSource,
    private val userPreferences: UserPreferencesDataStore // ✨ groupId 조회용
) : CollectionRepository {
    override suspend fun getCollections(
        type: String,
        limit: Int,
    ): Result<List<Collection>> {

        // =================================================================
        // 🏷️ 1. 모음(태그) 목록 조회
        // =================================================================
        return try {
            val groupId = getGroupIdOrThrow()

            // 1. 서버 요청
            val response = networkDataSource.getCollections(groupId, type, limit)

            // 2. 도메인 변환 (List<Keyword> -> List<Collection>)
            // keywords 리스트를 순회하며 변환
            // asExternalModel() 사용
            val collections = response.keywords.map { it.asExternalModel() }
            Result.success(collections)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================
    // 🖼️ 2. 모음 상세(사진 그리드) 조회
    // =================================================================
    override suspend fun getCollectionDetail(
        keywordId: String,
        cursor: String?,
    ): Result<List<SharedMedia>> {
        return try {
            val groupId = getGroupIdOrThrow()

            // 1. 서버 요청 (MediaListResponse 반환)
            val response = networkDataSource.getCollectionDetail(groupId, keywordId, cursor)

            // 2. 도메인 변환 (MediaListResponse -> List<Media>)
            val mediaList = response.medias.map { it.asExternalModel() }
            Result.success(mediaList)

            // 임시 반환 (Media 관련 코드가 없어서 주석 처리함, 실제 구현 시 위 주석 해제)
            Result.success(mediaList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =================================================================
    // 🔒 내부 헬퍼 함수
    // =================================================================

    // DataStore에서 현재 내 그룹 ID를 가져옵니다. (없으면 에러)
    private suspend fun getGroupIdOrThrow(): String {
        return userPreferences.userGroupId.first()
            ?: throw IllegalStateException("로그인된 그룹 정보가 없습니다.")
    }
}
