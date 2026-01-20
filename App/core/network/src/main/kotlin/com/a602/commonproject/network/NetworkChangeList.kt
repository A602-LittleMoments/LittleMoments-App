package com.a602.commonproject.network

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * 모델의 변경 목록을 네트워크에서 표현한 것입니다.
 * 변경 목록은 모델 ID와 해당 모델에 대한 메타데이터를 매핑하는 서버 측 데이터 구조를 나타냅니다.
 * 하나의 변경 목록에서 특정 모델 ID는 한 번만 나타날 수 있습니다.
 */

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
@Serializable
data class NetworkChangeList(

    // 변경된 모델의 ID
    val id : String,
    // 컬렉션 내에서 고유하고 연속적이며 단조롭게 증가하는 버전 번호로, 컬렉션 내 모델 간의 상대적인 변경 시점을 나타냅니다.
    val changeListVersion: Int,
    // 모델에 대한 업데이트 내용을 요약합니다. 삭제되었는지 또는 업데이트되었는지 여부를 나타냅니다.
    // 업데이트에는 새 모델 생성도 포함됩니다.
    val isDelete : Boolean
)
