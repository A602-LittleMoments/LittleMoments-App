package com.a602.commonproject.data.repository

import com.a602.commonproject.model.data.Collection
import com.a602.commonproject.model.data.SharedMedia
import kotlinx.coroutines.flow.Flow

/**
 * 🏷️ CollectionRepository
 * AI가 분류한 사진 모음(태그, 클러스터) 데이터를 관리합니다.
 */
interface CollectionRepository {
    /**
     * 모음(태그) 목록 조회
     * - 예: "웃는 얼굴", "해변", "강아지" 등 키워드 리스트 반환
     */
    suspend fun getCollections(
        type: String = "TAG",
        limit: Int = 20
    ): Flow<List<Collection>>

    /**
     * 특정 모음의 상세 사진 목록 조회
     * - keywordId: 선택한 태그의 ID
     * - cursor: 무한 스크롤용 커서 (Optional)
     * - 반환값: 해당 태그에 속한 사진(Media) 리스트
     */
    suspend fun getCollectionDetail(
        keywordId: String,
        cursor: String? = null
    ): Flow<List<SharedMedia>> // (Media 도메인 모델 사용)
}
