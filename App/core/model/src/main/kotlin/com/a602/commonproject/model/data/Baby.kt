package com.a602.commonproject.model.data


/**
 * 아이 관련 data class
 */
data class Baby(
    val babyId: String,           // DB: babyId, Net: babyId
    val babyName: String,         // DB: name, Net: babyName (변환 필요)
    val birthDate: String,        // "YYYY-MM-DD"
    val gender: Gender,           // "M", "F", "U" 처리
    val imageUrl: String?,        // DB: profileUrl, Net: pictureUrl (변환 필요)
) {
    enum class Gender {
        MALE, FEMALE, UNKNOWN
    }
}
