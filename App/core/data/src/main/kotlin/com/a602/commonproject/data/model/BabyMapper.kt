package com.a602.commonproject.data.model

import com.a602.commonproject.database.model.BabyEntity
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.network.model.BabyRequest
import com.a602.commonproject.network.model.BabyResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * [UI -> DB] 아기 정보 수정 저장 시
 */
fun Baby.toEntity(): BabyEntity {
    return BabyEntity(
        babyId = babyId,
        babyName = babyName,
        birthDate = parseDateToLong(birthDate),
        profileUrl = imageUrl ?: "",
        gender = gender.toDbValue() // 아래 확장 함수 사용
    )
}

/**
 * [DB -> UI]
 */
fun BabyEntity.asExternalModel(): Baby {
    // Timestamp -> "yyyy-MM-dd"
    val formattedDate = try {
        SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date(birthDate))
    } catch (e: Exception) {
        ""
    }

    return Baby(
        babyId = babyId,
        babyName = babyName, // DB 컬럼명 name -> Model babyName
        birthDate = formattedDate,
        imageUrl = profileUrl,
        gender = when (gender) {
            "M" -> Baby.Gender.MALE
            "F" -> Baby.Gender.FEMALE
            else -> Baby.Gender.UNKNOWN
        }
    )
}

// =================================================================
// 3. ✨ [Network -> DB] 서버 동기화용 (추가됨!)
// 서버에서 받아온 최신 정보를 내 폰 DB에 덮어쓸 때 사용합니다.
// =================================================================
fun BabyResponse.toEntity(): BabyEntity {
    return BabyEntity(
        babyId = babyId,
        babyName = babyName,    // 서버 필드명(babyName) -> DB 필드명(name)

        // 서버 날짜("yyyy-MM-dd") -> DB Timestamp(Long)
        birthDate = parseDateToLong(birthDate),

        profileUrl = pictureUrl ?: "", // 서버 필드명(pictureUrl) -> DB 필드명(profileUrl)

        // 서버에서 성별을 안 주거나 형식이 다를 경우 안전하게 처리
        gender = when (gender) { // 서버가 "MALE"/"FEMALE"로 준다고 가정
            "MALE", "M" -> "M"
            "FEMALE", "F" -> "F"
            else -> "U"
        }
    )
}

// =================================================================
// 4. ✨ [UI -> Network] 정보 수정 요청용 (추가됨!)
// 아기 정보를 수정해서 서버로 보낼 때 사용합니다.
// =================================================================
fun Baby.toNetworkModel(): BabyRequest {
    return BabyRequest(
        babyId = babyId,
        babyName = babyName,
        birthDate = birthDate, // "yyyy-MM-dd" 그대로 전송
        gender = when (gender) {
            Baby.Gender.MALE -> "M"
            Baby.Gender.FEMALE -> "F"
            else -> "U"
        },
        pictureUrl = imageUrl // (이미지 업로드는 별도 로직일 수 있음)
    )
}

// =================================================================
// 5. ✨ [DB -> Network] 동기화(Worker)용 (필수!)
// DB에 저장된 내용을 꺼내서 서버로 보낼 때 사용합니다.
// =================================================================
fun BabyEntity.toNetworkModel(): BabyRequest {
    // DB의 Long 날짜 -> 서버의 "yyyy-MM-dd" 변환
    val dateString = try {
        SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date(birthDate))
    } catch (e: Exception) {
        ""
    }


    return BabyRequest(
        babyId = babyId,
        babyName = babyName,
        birthDate = dateString,
        gender = gender,
        pictureUrl = profileUrl
    )
}
private fun parseDateToLong(dateString: String): Long {
    return try {
        SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).parse(dateString)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

// 성별 Enum -> DB String 변환
private fun Baby.Gender.toDbValue(): String {
    return when (this) {
        Baby.Gender.MALE -> "M"
        Baby.Gender.FEMALE -> "F"
        else -> "U"
    }
}
