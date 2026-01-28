package com.a602.commonproject.feature.memory

data class MediaItemUi(
    val mediaId: String,
    val thumbUrl: String,
    val subThumbUrl: String?,
    val takenAt: String?,   // 필요하면 화면에서 포맷팅
    val role: String?,      // uploadedBy.nickname
    val caption: String?,   // nullable
)

//data class MediaGridUiState(
//    val isLoading: Boolean = false,
//    val items: List<MediaItemUi> = emptyList(),
//    val nextCursor: String? = null,
//    val errorMessage: String? = null,
//)
