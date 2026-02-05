package com.a602.commonproject.feature.album

import androidx.navigation3.runtime.NavKey
import java.time.LocalDate
import kotlinx.serialization.Serializable


@Serializable
object GalleryNavKey : NavKey

// 메인 -> 캘린더
/*@Serializable
data object CalendarNavKey : NavKey*/


// 그리드 보기
// 캘린더 -> 날짜 클릭 -> 이동 -> 날짜 필터 안한다 하여 이렇게 둡니다
@Serializable
data class GridNavKey(
    val keywordId: String? = null,
    val title: String? = null,
    val babyId: String? = null,
    val year: Int? = null
) : NavKey
data class DayGridNavKey(val date: LocalDate) : NavKey


// 사진 상세보기

@Serializable
data class MediaDetailNavKey(
    val mediaId: String,
    val date: String? = null,
    val keywordId: String? = null
) : NavKey


// 코멘트 수정
@Serializable
data class CommentEditNavKey(
    val mediaId: String
) : NavKey


@Serializable
data object TempAlbumNavKey : NavKey

@Serializable
data class MultiPhotoUploadNavKey(
    val mediaIds: List<String>
) : NavKey

@Serializable
object HighlightCalendarNavKey : NavKey

// 하이라이트 결과
@Serializable
data class HighlightResultNavKey(
    val slideshowId: String
) : NavKey
