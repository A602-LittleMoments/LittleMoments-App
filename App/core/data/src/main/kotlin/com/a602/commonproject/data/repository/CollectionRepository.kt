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
    fun getCollections(
        type: String = "TAG",
        limit: Int = 20
    ): Flow<List<Collection>>

    /**
     * 2컬렉션 목록 동기화
     * 서버에서 최신 태그 목록을 가져와 로컬 DB를 업데이트합니다.
     * 에러 발생 시 예외를 던지며, 이는 ViewModel의 .asResult()에서 처리됩니다.
     */
    suspend fun syncCollections(
        type: String = "TAG",
        limit: Int = 20
    ) : Boolean

    /**
     * 특정 모음의 상세 사진 목록 조회
     * - keywordId: 선택한 태그의 ID
     * - cursor: 무한 스크롤용 커서 (Optional)
     * - 반환값: 해당 태그에 속한 사진(Media) 리스트
     */
    fun getCollectionDetail(
        keywordId: String,
        cursor: String? = null
    ): Flow<List<SharedMedia>> // (Media 도메인 모델 사용)

    /**
     * 특정 컬렉션 상세 데이터 동기화
     * 서버에서 해당 태그에 속한 사진 ID 리스트를 가져와 DB(SharedMedia 및 CrossRef)를 갱신합니다.
     */
    suspend fun syncCollectionDetail(
        keywordId: String,
        cursor: String? = null
    ) : Boolean


}
