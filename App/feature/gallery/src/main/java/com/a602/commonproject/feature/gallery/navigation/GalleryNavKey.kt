package com.a602.commonproject.feature.gallery

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
object GalleryNavKey : NavKey

// 메인 -> 캘린더
/*@Serializable
data object CalendarNavKey : NavKey*/


// 그리드 보기
// 캘린더 -> 날짜 클릭 -> 이동 -> 날짜 필터 안한다 하여 이렇게 둡니다
@Serializable
data object GridNavKey : NavKey


// 사진 상세보기

@Serializable
data class MediaDetailNavKey(
    val mediaId: String
) : NavKey


// 코멘트 수정
@Serializable
data class CommentEditNavKey(
    val mediaId: String
) : NavKey


// 임시 앨범
@Serializable
data object TempAlbumNavKey : NavKey

@Serializable
object HighlightCalendarNavKey : NavKey
@Serializable
data class HighlightLoadingNavKey(
    val startMillis: Long,
    val endMillis: Long
) : NavKey
//@Serializable
//data class HighlightResultNavKey(
//    val result: HighlightResult
//) : NavKey
//
//
//@Serializable
//data class HighlightResult(
//    val id: String,
//    val title: String,
//    val thumbnailUrl: String?,
//    val remoteVideoUrl: String?,
//    val localVideoPath: String?,
//    val status: MakeStatus,
//    val mediaCount: Int,
//    val durationSec: Int,
//    val createdAt: Long
//) : NavKey {
//
//    enum class MakeStatus {
//        QUEUED, PROCESSING, COMPLETED, FAILED, DOWNLOADED, UNKNOWN
//    }
//}
