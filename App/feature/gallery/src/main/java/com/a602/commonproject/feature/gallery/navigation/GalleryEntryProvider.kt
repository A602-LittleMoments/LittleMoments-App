//package com.a602.commonproject.feature.gallery.navigation
//
//import androidx.compose.runtime.LaunchedEffect
//
//import androidx.navigation3.runtime.EntryProviderScope
//import androidx.navigation3.runtime.NavKey
//import com.a602.commonproject.feature.gallery.CalendarNavKey
//import com.a602.commonproject.feature.gallery.CalendarScreen
//import com.a602.commonproject.navigation.Navigator
//import androidx.compose.runtime.remember
//import com.a602.commonproject.feature.gallery.GridNavKey
//import com.a602.commonproject.feature.gallery.GridGallery
//import com.a602.commonproject.feature.gallery.HighlightCalendarNavKey
//import com.a602.commonproject.feature.gallery.HighlightCalendarScreen
//import com.a602.commonproject.feature.gallery.HighlightLoadingNavKey
//import com.a602.commonproject.feature.gallery.PhotoDetail
//import com.a602.commonproject.feature.gallery.PhotoDetailNavKey
//import com.a602.commonproject.model.data.SharedMedia
//import com.a602.commonproject.feature.gallery.HighlightResultNavKey
//import com.a602.commonproject.feature.gallery.HighlightLoadingScreen
///**
// * ✅ 메인 화면: 캘린더
// */
//fun EntryProviderScope<NavKey>.calendarEntry(
//    navigator: Navigator,
//    mediasProvider: () -> List<SharedMedia>,
//) {
//    entry<CalendarNavKey> {
//        CalendarScreen(
//            medias = mediasProvider(),
////            onBackClick = { navigator.goBack() },
//            onDateClick = {
//                navigator.navigate(GridNavKey)
//            }
//        )
//    }
//}
//
///**
// * ✅ 그리드 보기
// * - 캘린더로 가는 버튼
// * - 폴라로이드 클릭 -> 상세보기
// */
//
//fun EntryProviderScope<NavKey>.gridEntry(
//    navigator: Navigator,
//    mediasProvider: () -> List<SharedMedia>,
//) {
//    entry<GridNavKey> {
//        GridGallery(
//            medias = mediasProvider(),
//            onCalendarClick = { navigator.navigate(CalendarNavKey) },
//            onMediaClick = { media ->
//                navigator.navigate(PhotoDetailNavKey(mediaId = media.id))
//            }
//        )
//    }
//}
//fun EntryProviderScope<NavKey>.photoDetailEntry(
//    navigator: Navigator,
//    mediasProvider: () -> List<SharedMedia>,
//) {
//    entry<PhotoDetailNavKey> { key ->
//        val media = remember(key.mediaId) {
//            mediasProvider().firstOrNull { it.id == key.mediaId }
//        }
//        if (media == null) {
//            LaunchedEffect(key.mediaId) { navigator.goBack() }
//            return@entry
//        }
//
//        PhotoDetail(
//            media = media
//        )
//    }
//}
//
//
//
///**
// * ✅ 하이라이트 캘린더 (추가 예정 플로우)
// */
//fun EntryProviderScope<NavKey>.highlightCalendarEntry(
//    navigator: Navigator
//) {
//    entry<HighlightCalendarNavKey> {
//        HighlightCalendarScreen(
//            onDateRangeSelected = { startMillis, endMillis ->
//                navigator.navigate(
//                    HighlightLoadingNavKey(
//                        startMillis = startMillis,
//                        endMillis = endMillis
//                    )
//                )
//            },
//            onBack = { navigator.goBack() }
//        )
//    }
//}
//
///**
// *  하이라이트 생성 로딩
// * - 들어오자마자 POST 요청(시작/끝 전달)
// * - 성공하면 결과 화면으로 이동
// * - 실패하면 뒤로
// */
//fun EntryProviderScope<NavKey>.highlightLoadingEntry(
//    navigator: Navigator,
//    requestCreateSlideshow: suspend (Long, Long) -> String,
//) {
//    entry<HighlightLoadingNavKey> { key ->
//        HighlightLoadingScreen(
//            startMillis = key.startMillis,
//            endMillis = key.endMillis,
//            requestCreateSlideshow = requestCreateSlideshow,
//            onSuccess = { slideshowId ->
//                navigator.navigate(HighlightResultNavKey(highlightId = slideshowId))
//            },
//            onFailure = {
//                navigator.goBack()
//            }
//        )
//    }
//}
