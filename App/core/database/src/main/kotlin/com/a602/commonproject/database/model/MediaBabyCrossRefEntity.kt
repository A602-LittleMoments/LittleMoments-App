package com.a602.commonproject.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "media_baby_cross_ref",
    primaryKeys = ["mediaId", "babyId"],
    foreignKeys = [
        ForeignKey(
            entity = ShareMediaEntity::class,
            parentColumns = ["mediaId"],
            childColumns = ["mediaId"],
            onDelete = ForeignKey.CASCADE // 📸 사진이 삭제되면 관계 정보도 자동 삭제
        ),
        ForeignKey(
            entity = BabyEntity::class,
            parentColumns = ["babyId"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE // 👶 아기 정보가 삭제되면 관계 정보도 자동 삭제
        )
    ]
)
data class MediaBabyCrossRefEntity(
    val mediaId: String,
    @ColumnInfo(index = true)
    val babyId: String
)
