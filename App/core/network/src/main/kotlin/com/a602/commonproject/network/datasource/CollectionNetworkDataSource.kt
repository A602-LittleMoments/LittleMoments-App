package com.a602.commonproject.network.datasource

import com.a602.commonproject.network.api.RetrofitCollectionApi
import com.a602.commonproject.network.model.CollectionListResponse
import com.a602.commonproject.network.model.MediaListResponse
import javax.inject.Inject

/**
 * 🤖 CollectionNetworkDataSource 인터페이스
 *
 * AI가 자동으로 분류해준 '모음집(Cluster/Tag)' 기능을 담당합니다.
 * "웃는 얼굴", "자는 모습" 같은 태그 목록을 불러오거나,
 * 특정 태그를 눌렀을 때 해당 사진들을 앨범처럼 보여줍니다.
 */
interface CollectionNetworkDataSource {
    // 6.1.1 모음(태그) 목록 조회
    // - type: "TAG" (기본값), "FACE" 등 확장 가능
    // - limit: 상위 몇 개의 태그를 가져올지
    suspend fun getCollections(
        groupId: String,
        type: String = "TAG",
        limit: Int = 20
    ): CollectionListResponse

    // 6.1.2 클러스터 상세 조회 (사진 그리드)
    // - keywordId: 태그 ID (예: "smile_123")
    // - 반환값: MediaListResponse (앨범 조회와 동일한 모델 재사용 ✨)
    suspend fun getCollectionDetail(
        groupId: String,
        keywordId: String,
        cursor: String? = null
    ): MediaListResponse
}
/**
 * 🏗️ RetrofitCollectionNetwork (구현체)
 * internal visibility: 모듈 내부 전용
 */
internal class RetrofitCollectionNetwork @Inject constructor(
    private val collectionApi: RetrofitCollectionApi
) : CollectionNetworkDataSource {

    override suspend fun getCollections(groupId: String, type: String, limit: Int) =
        collectionApi.getCollections(groupId, type, limit)

    override suspend fun getCollectionDetail(groupId: String, keywordId: String, cursor: String?) =
        collectionApi.getCollectionDetail(groupId, keywordId, cursor)
}
