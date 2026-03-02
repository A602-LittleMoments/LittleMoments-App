package com.a602.commonproject.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.a602.commonproject.database.model.CollectionEntity
import com.a602.commonproject.database.model.MediaCollectionCrossRefEntity
import com.a602.commonproject.database.model.ShareMediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {
    /**
     * 특정 카테고리(예: TAG, DATE)의 컬렉션 목록을 가져옵니다.
     */
    @Query("SELECT * FROM collections WHERE categoryValue = :categoryValue")
    fun getCollectionsByCategory(categoryValue: String): Flow<List<CollectionEntity>>

    /**
     * 모든 컬렉션 목록을 가져옵니다.
     */
    @Query("SELECT * FROM collections")
    fun getCollections(): Flow<List<CollectionEntity>>

    /**
     * 컬렉션 목록을 삽입하거나 업데이트합니다.
     */
    @Upsert
    suspend fun upsertCollections(collections: List<CollectionEntity>)

    /**
     * 특정 카테고리의 컬렉션 데이터를 모두 삭제합니다.
     */
    @Query("DELETE FROM collections WHERE categoryValue = :categoryValue")
    suspend fun deleteCollectionsByCategory(categoryValue: String)

    // ------------------------ 태그와 사진을 연결하는 엔티티 관한 코드

    /**
     *  사진-컬렉션 관계 정보 삽입 (Batch)
     */
    @Upsert
    suspend fun upsertMediaCollectionCrossRefs(crossRefs: List<MediaCollectionCrossRefEntity>)

    /**
     * 특정 컬렉션(태그)에 속한 모든 사진 목록 조회 (JOIN)
     * UI는 이 Flow를 구독하여 오프라인에서도 즉각적으로 사진들을 볼 수 있습니다.
     */
    @Transaction
    @Query(
        """
        SELECT * FROM shared_media
        INNER JOIN media_collection_cross_ref ON shared_media.mediaId = media_collection_cross_ref.mediaId
        WHERE media_collection_cross_ref.collectionId = :collectionId
        ORDER BY takenAt DESC
    """,
    )
    fun getMediaByCollectionStream(collectionId: String): Flow<List<ShareMediaEntity>>


    /**
     *  특정 컬렉션과 연결된 모든 관계 정보를 삭제합니다. (재동기화 시 필요)
     */
    @Query("DELETE FROM media_collection_cross_ref WHERE collectionId = :collectionId")
    suspend fun deleteMediaCollectionCrossRefsByCollectionId(collectionId: String)


}
