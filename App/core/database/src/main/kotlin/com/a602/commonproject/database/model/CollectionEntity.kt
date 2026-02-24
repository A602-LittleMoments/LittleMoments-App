package com.a602.commonproject.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey val keywordId: String, // 고유 키워드 ID
    val keywordValue: String,          // 키워드 이름 (예: "강아지", "2024-03-21")
    val categoryId: String,            // 카테고리 ID
    val categoryValue: String,         // 카테고리 종류 (예: "TAG", "DATE")
    val collectionSize: Int            // 해당 키워드에 포함된 미디어 수
)
