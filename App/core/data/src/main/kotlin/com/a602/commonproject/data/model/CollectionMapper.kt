package com.a602.commonproject.data.model

import com.a602.commonproject.database.model.CollectionEntity
import com.a602.commonproject.network.model.CollectionKeyword
import com.a602.commonproject.model.data.Collection

/**
 * [Network -> UI] 서버에서 받은 키워드 목록을 UI 모델로 변환
 * DB 저장 없이 바로 화면에 뿌려줄 때 사용합니다.
 */
fun CollectionKeyword.asExternalModel(): Collection {
    return Collection(
        categoryId = categoryId,
        categoryValue = categoryValue,
        keywordId = keywordId,
        keywordValue = keywordValue,
        collectionSize = collectionSize
    )
}

/**
 * [Network -> DB] 서버에서 받은 데이터를 DB에 저장하기 위해서
 */
fun CollectionKeyword.toEntity() = CollectionEntity(
    keywordId = keywordId,
    keywordValue = keywordValue,
    categoryId = categoryId,
    categoryValue = categoryValue,
    collectionSize = collectionSize
)

/**
 * [DB -> UI] DB에 데이터를 UI로 보여주기 위해서
 */
fun CollectionEntity.asExternalModel(): Collection {
    return Collection(
        categoryId = categoryId,
        categoryValue = categoryValue,
        keywordId = keywordId,
        keywordValue = keywordValue,
        collectionSize = collectionSize
    )
}
