package com.a602.commonproject.data.model

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
