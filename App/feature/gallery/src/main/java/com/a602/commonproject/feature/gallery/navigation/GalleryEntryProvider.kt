package com.a602.commonproject.feature.gallery.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.navigation.Navigator
import com.a602.commonproject.feature.gallery.CalendarRoute
import com.a602.commonproject.feature.gallery.CommentEditNavKey
import com.a602.commonproject.feature.gallery.CommentEditRoute
import com.a602.commonproject.feature.gallery.DayGridNavKey
import com.a602.commonproject.feature.gallery.GalleryNavKey
import com.a602.commonproject.feature.gallery.GridNavKey
import com.a602.commonproject.feature.gallery.GridRoute
import com.a602.commonproject.feature.gallery.HighlightCalendarNavKey
import com.a602.commonproject.feature.gallery.HighlightCalendarRoute
import com.a602.commonproject.feature.gallery.HighlightLoadingNavKey
import com.a602.commonproject.feature.gallery.HighlightLoadingRoute
import com.a602.commonproject.feature.gallery.HighlightResultNavKey
import com.a602.commonproject.feature.gallery.HighlightResultRoute
//import com.a602.commonproject.feature.gallery.HighlightLoadingRoute
//import com.a602.commonproject.feature.gallery.HighlightResult
//import com.a602.commonproject.feature.gallery.HighlightResultNavKey
//import com.a602.commonproject.feature.gallery.HighlightResultScreen
import com.a602.commonproject.feature.gallery.MediaDetailNavKey
import com.a602.commonproject.feature.gallery.MediaDetailRoute
import com.a602.commonproject.feature.gallery.MultiPhotoUploadNavKey
import com.a602.commonproject.feature.gallery.MultiPhotoUploadScreen
import com.a602.commonproject.feature.gallery.TempAlbumNavKey
import com.a602.commonproject.feature.gallery.TempGridGalleryRoute

fun EntryProviderScope<NavKey>.galleryEntries(
    navigator: Navigator,
) {
    // 1. 메인화면 - 캘린더 뷰
    entry<GalleryNavKey> {
        CalendarRoute(
            onDateClick = { date -> navigator.navigate(DayGridNavKey(date)) },
            onGridClick = { navigator.navigate(GridNavKey) },
            onTempAlbumClick = { navigator.navigate(TempAlbumNavKey) },
            onHighLightClick = { navigator.navigate(HighlightCalendarNavKey) },
            onMediaClick = { media -> navigator.navigate(MediaDetailNavKey(mediaId = media.id)) }
        )
    }
    // 2. 그리드 보기
    entry<GridNavKey> {
        GridRoute(
            onBackClick = navigator::goBack,
            onCalendarClick = { navigator.navigate(GalleryNavKey) },
            onMediaClick = { media ->
                navigator.navigate(MediaDetailNavKey(mediaId = media.id))
            }
        )
    }
    //2-1. 그리드 날짜 필터링
    entry<DayGridNavKey> { key ->
        GridRoute(
            date = key.date,
            onBackClick = navigator::goBack,
            onCalendarClick = { navigator.navigate(GalleryNavKey) },
            onMediaClick = { media -> navigator.navigate(MediaDetailNavKey(media.id)) } // 예시
        )
    }


    // 3. 사진 상세보기
    entry<MediaDetailNavKey> { key ->
        MediaDetailRoute(
            mediaId = key.mediaId,
            onBack = navigator::goBack,
            onEdit = { navigator.navigate(CommentEditNavKey(key.mediaId)) },
            onDeleted = navigator::goBack
        )
    }

    // 4. 코멘트 수정
    entry<CommentEditNavKey> { key ->
        CommentEditRoute(
            mediaId = key.mediaId,
            onBack = navigator::goBack,
            onDone = navigator::goBack
        )
    }
// 5. 임시 앨범
    entry<TempAlbumNavKey> {
        TempGridGalleryRoute(
            onMediaClick = { media ->
                // TODO: 임시 앨범의 상세보기 화면 정의 필요
                // 현재는 PhotoDetailNavKey 재사용
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

    // 7. 하이라이트 캘린더
    entry<HighlightCalendarNavKey> {
        HighlightCalendarRoute(
            onDateRangeSelected = { start, end ->
                navigator.navigate(HighlightLoadingNavKey(start, end))
            },
            onBack = navigator::goBack
        )
    }

    // 7. 하이라이트 로딩
    entry<HighlightLoadingNavKey> { key ->
        HighlightLoadingRoute(
            startMillis = key.startMillis,
            endMillis = key.endMillis,
            onSuccess = { slideshowId ->
                navigator.navigate(HighlightResultNavKey(slideshowId))
            },
            onFailure = { error ->
                // TODO: 에러 토스트 또는 스낵바
                navigator.goBack()
            }
        )
    }

    // 8. 하이라이트 결과
    entry<HighlightResultNavKey> { key ->
        HighlightResultRoute(
            slideshowId = key.slideshowId,
            onBack = navigator::goBack
        )
    }

}
