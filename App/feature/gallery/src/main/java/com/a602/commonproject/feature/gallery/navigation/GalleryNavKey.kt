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
data class PhotoDetailNavKey(
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


// 하이라이트 생성
@Serializable
data object HighlightCalendarNavKey : NavKey
//하이라이트 로딩
@Serializable
data class HighlightLoadingNavKey(
    val startMillis: Long,
    val endMillis: Long
) : NavKey

// 하이라이트 완료
@Serializable
data class HighlightResultNavKey(
    val highlightId: String
) : NavKey
