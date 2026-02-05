package com.a602.commonproject.feature.album.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.navigation.Navigator
import com.a602.commonproject.feature.album.CalendarRoute
import com.a602.commonproject.feature.album.CommentEditNavKey
import com.a602.commonproject.feature.album.CommentEditRoute
import com.a602.commonproject.feature.album.DayGridNavKey
import com.a602.commonproject.feature.album.GalleryNavKey
import com.a602.commonproject.feature.album.GridNavKey
import com.a602.commonproject.feature.album.GridRoute
import com.a602.commonproject.feature.album.HighlightCalendarNavKey
import com.a602.commonproject.feature.album.HighlightCalendarRoute
import com.a602.commonproject.feature.album.HighlightResultNavKey
import com.a602.commonproject.feature.album.HighlightResultRoute
//import com.a602.commonproject.feature.gallery.HighlightLoadingRoute
//import com.a602.commonproject.feature.gallery.HighlightResult
//import com.a602.commonproject.feature.gallery.HighlightResultNavKey
//import com.a602.commonproject.feature.gallery.HighlightResultScreen
import com.a602.commonproject.feature.album.MediaDetailNavKey
import com.a602.commonproject.feature.album.MediaDetailRoute
import com.a602.commonproject.feature.album.MultiPhotoUploadNavKey
import com.a602.commonproject.feature.album.MultiPhotoUploadScreen
import com.a602.commonproject.feature.album.TempAlbumNavKey
import com.a602.commonproject.feature.album.TempGridGalleryRoute

fun EntryProviderScope<NavKey>.galleryEntries(
    navigator: Navigator,
) {
    // 1. 메인화면 - 캘린더 뷰
    entry<GalleryNavKey> {
        CalendarRoute(
            onDateClick = { date -> navigator.navigate(DayGridNavKey(date)) },
            onGridClick = { navigator.navigate(GridNavKey()) },
            onTempAlbumClick = { navigator.navigate(TempAlbumNavKey) },
            onHighLightClick = { navigator.navigate(HighlightCalendarNavKey) },
            onMediaClick = { media ->
                navigator.navigate(MediaDetailNavKey(mediaId = media.id))
            }
        )
    }
    // 2. 그리드 보기
    entry<GridNavKey> { key ->
        GridRoute(
            keywordId = key.keywordId,
            title = key.title,
            babyId = key.babyId,
            year = key.year,
            onBackClick = navigator::goBack,
            onCalendarClick = { navigator.navigate(GalleryNavKey) },
            onMediaClick = { media ->
                navigator.navigate(
                    MediaDetailNavKey(
                        mediaId = media.id,
                        keywordId = key.keywordId,
                        babyId = key.babyId,
                        year = key.year
                    )
                )
            }
        )
    }
    //2-1. 그리드 날짜 필터링
    entry<DayGridNavKey> { key ->
        GridRoute(
            date = key.date,
            onBackClick = navigator::goBack,
            onCalendarClick = { navigator.navigate(GalleryNavKey) },
            onMediaClick = { media -> 
                val mediaDate = java.time.Instant.ofEpochMilli(media.dateTaken)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                navigator.navigate(MediaDetailNavKey(mediaId = media.id, date = mediaDate.toString())) 
            }
        )
    }


    // 3. 사진 상세보기
    entry<MediaDetailNavKey> { key ->
        MediaDetailRoute(
            mediaId = key.mediaId,
            date = key.date,
            keywordId = key.keywordId,
            babyId = key.babyId,
            year = key.year,
            isTemp = key.isTemp,
            onBack = navigator::goBack,
            onEdit = { currentMediaId -> navigator.navigate(CommentEditNavKey(currentMediaId, isTemp = key.isTemp)) },
            onDeleted = navigator::goBack
        )
    }

    // 4. 코멘트 수정
    entry<CommentEditNavKey> { key ->
        CommentEditRoute(
            mediaId = key.mediaId,
            isTemp = key.isTemp,
            onBack = navigator::goBack,
            onDone = navigator::goBack
        )
    }
// 5. 임시 앨범 (상세보기 추가)
    entry<TempAlbumNavKey> {
        TempGridGalleryRoute(
            onMediaClick = { media ->
                 navigator.navigate(MediaDetailNavKey(mediaId = media.id, isTemp = true))
            },
            onBackClick = navigator::goBack,
            onNavigateToUpload = { ids ->
                navigator.navigate(MultiPhotoUploadNavKey(mediaIds = ids))
            }
        )
    }



    // 6. 멀티 포토 업로드
    entry<MultiPhotoUploadNavKey> { key ->
        MultiPhotoUploadScreen(
            mediaIds = key.mediaIds,
            onBackClick = { navigator.goBack() },
            onUploadSuccess = {
                // 1. 현재 탭의 스택 정리 (카메라/업로드 화면 제거)
                navigator.navigate(navigator.state.currentTopLevelKey)
                // 2. 갤러리 화면으로 이동
                navigator.navigate(GalleryNavKey)
            }
        )
    }

    // 7. 하이라이트 캘린더 (날짜 선택 + API 호출)
    entry<HighlightCalendarNavKey> {
        HighlightCalendarRoute(
            onDateRangeSelected = { _, _ ->
                // API 호출은 ViewModel에서 처리됨
                // 성공 시 알림이 오면 딥링크로 상세 화면 이동
                navigator.goBack()
            },
            onBack = navigator::goBack
        )
    }

    // 7. 하이라이트 로딩

    // 8. 하이라이트 결과
    entry<HighlightResultNavKey> { key ->
        HighlightResultRoute(
            slideshowId = key.slideshowId,
            onBack = navigator::goBack
        )
    }

}
