package com.a602.commonproject.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.a602.commonproject.database.model.SlideshowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SlideshowDao {
    @Query("SELECT * FROM slideshows ORDER BY createAt DESC")
    fun getSlideShows(): Flow<List<SlideshowEntity>>

    @Upsert
    suspend fun insertSlideshow(slideShow : SlideshowEntity)

    @Query("DELETE FROM slideshows WHERE slideshowId = :id")
    suspend fun deleteSlideshow(id: String)
}
