package com.a602.commonproject.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.a602.commonproject.data.model.asExternalModel
import com.a602.commonproject.data.model.toEntity
import com.a602.commonproject.data.repository.CollectionRepository
import com.a602.commonproject.database.dao.CollectionDao
import com.a602.commonproject.database.dao.MediaDao
import com.a602.commonproject.database.model.MediaCollectionCrossRefEntity
import com.a602.commonproject.datastore.datastore.UserPreferencesDataStore
import com.a602.commonproject.model.data.Collection
import com.a602.commonproject.model.data.SharedMedia
import com.a602.commonproject.network.datasource.CollectionNetworkDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


class OfflineFirstCollectionRepository @Inject constructor(
    private val networkDataSource: CollectionNetworkDataSource,
    private val collectionDao: CollectionDao,
    private val mediaDao: MediaDao,
    private val userPreferences: UserPreferencesDataStore, // ✨ groupId 조회용
) : CollectionRepository {

    /**
     * 모음(태그)의 목록을 조회 하는 코드
     */
    override fun getCollections(): Flow<List<Collection>> =
        collectionDao.getCollections().map { entities -> entities.map { it.asExternalModel() } }

    /**
     * 전체 키워드 목록 동기화 (FetchWorker에 돌아가며, 백그라운드에서 돌아가게 됨)
     */
    override suspend fun syncCollections(type: String, limit: Int): Boolean = runCatching {
        val groupId = getGroupIdOrThrow()
        val response = networkDataSource.getCollections(groupId, type, limit)

        val entities = response.keywords.map { it.toEntity() }
        collectionDao.upsertCollections(entities)
        true
    }.getOrDefault(false)


    /**
     * 모음의 상세(사진들)을 조회
     */
    override fun getCollectionDetail(
        keywordId: String,
    ): Flow<PagingData<SharedMedia>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { collectionDao.getMediaByCollection(keywordId) },
        )
            .flow.map { pagingData -> pagingData.map { it.asExternalModel() } }
    }

    /**
     * 상세 사진들을 동기화 하는 코드
     * getCollectionDetail를 클릭시에 동기화 하도록 설계
     */
    override suspend fun syncCollectionDetail(keywordId: String, cursor: String?): Boolean = runCatching {
        val groupId = getGroupIdOrThrow()
        val response = networkDataSource.getCollectionDetail(groupId, keywordId, cursor)

        val mediaEntities = response.medias.map { it.toEntity() }
        mediaDao.syncSharedList(mediaEntities)

        val crossRefs = response.medias.map { media ->
            MediaCollectionCrossRefEntity(
                mediaId = media.mediaId,
                collectionId = keywordId,
            )
        }
        collectionDao.upsertMediaCollectionCrossRefs(crossRefs)

        true
    }.getOrDefault(false)

    /**
     * DataStore에서 현재 내 그룹 ID를 가져옵니다. (없으면 에러)
     * 내부 확장 함수
     */
    private suspend fun getGroupIdOrThrow(): String {
        return userPreferences.userGroupId.first()
            ?: throw IllegalStateException("로그인된 그룹 정보가 없습니다.")
    }
}
