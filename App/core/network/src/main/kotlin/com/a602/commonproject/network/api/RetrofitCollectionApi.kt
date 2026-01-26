package com.a602.commonproject.network.api

import com.a602.commonproject.network.model.CollectionListResponse
import com.a602.commonproject.network.model.MediaListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface RetrofitCollectionApi {

    // 6.1.1 모음(태그) 목록
    @GET("groups/{groupId}/collections")
    suspend fun getCollections(
        @Path("groupId") groupId: String,
        @Query("type") type: String = "TAG",
        @Query("limit") limit: Int = 20
    ): CollectionListResponse


    // 6.1.2 클러스터 상세 (미디어 그리드)
    @GET("groups/{groupId}/collections/{keywordId}")
    suspend fun getCollectionDetail(
        @Path("groupId") groupId: String,
        @Path("keywordId") keywordId: String,
        @Query("cursor") cursor: String? = null
    ): MediaListResponse
}
