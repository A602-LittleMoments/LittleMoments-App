package com.a602.commonproject.network.datasource

import com.a602.commonproject.network.api.RetrofitBabyApi
import com.a602.commonproject.network.model.BabyResponse
import com.a602.commonproject.network.model.BabyListResponse
import com.a602.commonproject.network.model.BabyRequest
import java.io.File
import javax.inject.Inject
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * 👶 BabyNetworkDataSource 인터페이스
 *
 * 우리 가족의 주인공인 '아기' 정보를 관리합니다.
 * 아기를 등록할 때는 사진이 필수일 수 있으므로 파일 처리 로직이 포함됩니다.
 */
interface BabyNetworkDataSource {
    // 3.1 아기 등록 (Multipart)
    // - babyRequest: 이름, 생년월일 등의 텍스트 정보
    // - imageFile: 아기 프로필 사진 파일 (nullable)
    // - 반환값: 등록된 아기 정보 (NetworkBaby) -> 화면 갱신용
    suspend fun addBaby(groupId: String, babyRequest: BabyRequest, imageFile: File?): BabyResponse

    // 3.2 아기 목록 조회: 그룹에 등록된 모든 아기를 가져옵니다.
    suspend fun getBabies(groupId: String): BabyListResponse

    // 3.2 아기 정보 수정: 사진 수정은 별도 API가 없다면 정보만 수정합니다.
    suspend fun updateBaby(groupId: String, babyId: String, request: BabyRequest)

    // 3.2 아기 삭제: 목록에서 제거합니다.
    suspend fun deleteBaby(groupId: String, babyId: String)
}

/**
 * 🏗️ RetrofitBabyNetwork (구현체)
 * * internal 키워드로 RetrofitBabyApi 노출 문제를 방지합니다.
 */
internal class RetrofitBabyNetwork @Inject constructor(
    private val networkJson: Json,     // JSON 변환기
    private val babyApi: RetrofitBabyApi,
) : BabyNetworkDataSource {
    /**
     * ✨ 아기 등록 구현 (가장 중요한 부분!)
     * Multipart/form-data 요청을 조립하는 과정입니다.
     */
    override suspend fun addBaby(
        groupId: String,
        babyRequest: BabyRequest,
        imageFile: File?,
    ): BabyResponse {

        // [Step 1] 아기 정보(DTO)를 JSON 문자열로 변환합니다.
        // 예: BabyRequest(name="하린") -> '{"name":"하린", ...}'
        val jsonString = networkJson.encodeToString(babyRequest)

        // [Step 2] JSON 문자열을 RequestBody로 포장합니다.
        // "이건 단순 텍스트가 아니라 JSON 데이터야"라고 알려줍니다 (application/json).
        // API 명세서의 @Part("data")에 해당합니다.
        val dataPart = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        // [Step 3] 이미지 파일이 있다면 MultipartBody.Part로 변환합니다.
        val imagePart = imageFile?.let { file ->
            // 3-1. 파일을 읽기 위한 RequestBody 생성 (이미지 타입)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())

            // 3-2. 서버가 약속한 키 값("baby_picture")으로 파트 생성
            MultipartBody.Part.createFormData("baby_picture", file.name, requestFile)
        }

        // [Step 4] 조립된 부품(데이터 + 이미지)을 서버로 전송합니다.
        // 서버는 등록을 완료하고, 생성된 아기 객체(ID 포함)를 돌려줍니다.
        return babyApi.addBaby(groupId, dataPart, imagePart)
    }

    override suspend fun getBabies(groupId: String): BabyListResponse {
        return babyApi.getBabies(groupId)
    }

    override suspend fun updateBaby(groupId: String, babyId: String, request: BabyRequest) {
        // 수정은 JSON Body로만 보내므로 간단하게 전달합니다.
        babyApi.updateBaby(groupId, babyId, request)
    }

    override suspend fun deleteBaby(groupId: String, babyId: String) {
        babyApi.deleteBaby(groupId, babyId)
    }

}
