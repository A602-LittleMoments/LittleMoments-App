package com.a602.commonproject.data.model

import com.a602.commonproject.database.model.TempMediaEntity
import com.a602.commonproject.model.data.TempMedia

/**
 * [DB -> UI]
 * 일시 사진을 위해서 바꾸는 거
 */
fun TempMediaEntity.asExternalModel(): TempMedia {
    return TempMedia(
        id = tempId,
        localUri = localUri ?: "", // 로컬 경로가 없으면 빈 문자열 처리
        subLocalUri = subLocalUri,
        takenAt = takenAt,
        expirationDate = expirationDate,
    )
}
