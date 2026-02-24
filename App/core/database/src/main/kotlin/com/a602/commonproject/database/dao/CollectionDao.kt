package com.a602.commonproject.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.a602.commonproject.database.model.CollectionEntity
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
}
