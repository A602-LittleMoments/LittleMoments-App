package com.a602.commonproject.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "media_collection_cross_ref",
    primaryKeys = ["mediaId", "collectionId"],
    foreignKeys = [
        ForeignKey(
            entity = ShareMediaEntity::class,
            parentColumns = ["mediaId"],
            childColumns = ["mediaId"],
            onDelete = ForeignKey.CASCADE // 원본 사진이 삭제되면 관계 정보도 자동 삭제
        ),
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["keywordId"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE // 컬렉션 자체가 삭제되면 관계 정보도 자동 삭제
        )
    ]
)
data class MediaCollectionCrossRefEntity(
    val mediaId: String,
    @ColumnInfo(index = true)
    val collectionId: String
)
