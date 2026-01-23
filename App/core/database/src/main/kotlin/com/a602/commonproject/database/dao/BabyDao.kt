package com.a602.commonproject.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.a602.commonproject.database.model.BabyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BabyDao {
    @Query("SELECT * FROM babies ORDER BY birthDate ASC")
    fun getAllBabies() : Flow<List<BabyEntity>>

    @Upsert
    suspend fun insertBaby(baby : BabyEntity)

    @Query("DELETE FROM babies WHERE babyId = :babyId")
    suspend fun deleteBaby(babyId:String)
}
