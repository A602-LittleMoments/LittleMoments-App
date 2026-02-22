 package com.a602.commonproject.network.datasource

import com.a602.commonproject.network.api.RetrofitMediaApi
import com.a602.commonproject.network.model.BatchUploadResponse
import com.a602.commonproject.network.model.MediaDetailResponse
import com.a602.commonproject.network.model.MediaListResponse
import com.a602.commonproject.network.model.MediaUploadMetadataWrapper
import com.a602.commonproject.network.model.UpdateCaptionRequest
import com.a602.commonproject.network.util.toJsonRequestBody
import com.a602.commonproject.network.util.toMultipartPart
import java.io.File
import javax.inject.Inject
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


/**
 * 📸 MediaNetworkDataSource 인터페이스
 *
 * 앱의 핵심 기능인 '사진/영상'의 업로드와 조회를 담당합니다.
 * 다중 파일 업로드와 복잡한 필터링(날짜, 페이징) 로직을 추상화하여
 * Repository가 편하게 사용할 수 있도록 합니다.
 */
interface MediaNetworkDataSource {
    // 4.1 다중 업로드 (배치)
    // - files: 사용자가 선택한 실제 사진 파일 리스트
    // - metadata: 각 사진의 촬영 정보(날짜, 위치 등)가 담긴 매칭 데이터
    // -> 반환값: 성공/실패 개수와 상세 결과
    suspend fun uploadMediaBatch(
        groupId: String,
        files: List<File>,
        metadata: MediaUploadMetadataWrapper,
    ): BatchUploadResponse

    // 4.3 앨범 조회 (만능 조회 함수 ✨)
    // - limit: 한 번에 가져올 개수 (기본 1000)
    // - cursor: 무한 스크롤용 다음 페이지 키 (없으면 첫 페이지)
    // - startDate, endDate: 특정 날짜 범위 조회 (없으면 전체 최신순)
    suspend fun getAlbums(
        groupId: String,
        limit: Int = 1000,
        cursor: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        babyId: String? = null,
        filterByUserId: String? = null,
    ): MediaListResponse

    // 4.4 미디어 상세 조회
    suspend fun getMediaDetail(groupId: String, mediaId: String): MediaDetailResponse

    // 4.5 캡션(설명) 수정
    suspend fun updateCaption(groupId: String, mediaId: String, request: UpdateCaptionRequest)

    // 4.6 미디어 삭제
    suspend fun deleteMedia(groupId: String, mediaId: String)
}

/**
 * 🏗️ RetrofitMediaNetwork (구현체)
 * * internal 키워드로 모듈 내부 전용으로 설정했습니다.
 */
internal class RetrofitMediaNetwork @Inject constructor(
    private val networkJson: Json,       // JSON 변환기
    private val mediaApi: RetrofitMediaApi, // Retrofit 전화기
) : MediaNetworkDataSource {

    /**
     * ✨ 다중 업로드 구현 (가장 복잡한 부분!)
     * List<File>을 MultipartBody.Part의 리스트로 변환하고,
     * 메타데이터 객체를 JSON RequestBody로 변환하여 함께 보냅니다.
     */
    override suspend fun uploadMediaBatch(
        groupId: String,
        files: List<File>,
        metadata: MediaUploadMetadataWrapper,
    ): BatchUploadResponse {
        // [Step 1] 파일 리스트 변환 (List<File> -> List<MultipartBody.Part>)
        val fileParts = files.map { file ->  file.toMultipartPart("files") }
        // [Step 2] 메타데이터 변환 (Object -> JSON RequestBody)
        // val itemsPart = metadata.toJsonRequestBody(networkJson) -> NetworkModule에서 한번에 처리
        // [Step 3] 파일 뭉치와 설명서를 서버로 발송
        return mediaApi.uploadMediaBatch(groupId, fileParts, metadata)
    }

    /**
     * ✨ 앨범 조회 구현
     * 파라미터가 많지만, Retrofit이 알아서 URL 쿼리(?key=value)로 만들어줍니다.
     * 값이 null인 파라미터는 자동으로 URL에서 생략됩니다.
     */
    override suspend fun getAlbums(
        groupId: String,
        limit: Int,
        cursor: String?,
        startDate: String?,
        endDate: String?,
        babyId: String?,
        filterByUserId: String?,
    ): MediaListResponse {
        return mediaApi.getAlbums(
            groupId = groupId,
            limit = limit,
            cursor = cursor,
            startDate = startDate,
            endDate = endDate,
            babyId = babyId,
            filterByUserId = filterByUserId,
        )
    }

    override suspend fun getMediaDetail(groupId: String, mediaId: String): MediaDetailResponse =
        mediaApi.getMediaDetail(groupId, mediaId)

    override suspend fun updateCaption(
        groupId: String,
        mediaId: String,
        request: UpdateCaptionRequest,
    ) = mediaApi.updateCaption(groupId, mediaId, request)

    override suspend fun deleteMedia(groupId: String, mediaId: String) = mediaApi.deleteMedia(groupId, mediaId)

}
