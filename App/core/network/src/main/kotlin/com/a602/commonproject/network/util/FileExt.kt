package com.a602.commonproject.network.util

import java.io.File
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * File을 Retrofit 전송용 MultipartBody.Part로 변환
 */
fun File.toMultipartPart(partName: String) : MultipartBody.Part{
    // 파일을 읽을 수 있는 RequestBody로 변환 (타입: image/*)
    val requestFile = this.asRequestBody("image/*".toMediaTypeOrNull())
    // 서버가 요구한 파라미터 이름(partName)과 파일명, 데이터를 합쳐서 파트 생성
    return MultipartBody.Part.createFormData(partName, this.name, requestFile)
}

/**
 * 객체를 JSON RequestBody로 변환 (Multipart 내 데이터 파트용)
 */
inline fun <reified T> T.toJsonRequestBody(json: Json): RequestBody{
    // 1. 요청 정보를 JSON 문자열로 반환 해줌
    val jsonString = json.encodeToString(this)
    // 2. JSON 문자열을 RequestBody로 포장
    return jsonString.toRequestBody("application/json".toMediaTypeOrNull())
}
