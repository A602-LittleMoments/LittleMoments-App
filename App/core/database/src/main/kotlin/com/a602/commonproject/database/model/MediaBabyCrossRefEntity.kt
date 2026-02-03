package com.a602.commonproject.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "media_baby_cross_ref",
    primaryKeys = ["mediaId", "babyId"]
)
data class MediaBabyCrossRefEntity(
    val mediaId: String,
    @ColumnInfo(index = true)
    val babyId: String
)
