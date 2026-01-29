//package com.a602.commonproject.feature.gallery.navigation
//
//
//import androidx.compose.runtime.remember
//import androidx.navigation3.runtime.EntryProviderScope
//import androidx.navigation3.runtime.NavKey
//import com.a602.commonproject.feature.gallery.CalendarNavKey
//import com.a602.commonproject.feature.gallery.CalendarScreen
//import com.a602.commonproject.feature.gallery.DetailNavKey
//import com.a602.commonproject.feature.gallery.GalleryNavKey
//import com.a602.commonproject.feature.gallery.GridGallery
//import com.a602.commonproject.feature.gallery.HighlightCalendarNavKey
//import com.a602.commonproject.feature.gallery.HighlightCalendarScreen
//import com.a602.commonproject.feature.gallery.HighlightLoadingNavKey
//import com.a602.commonproject.feature.gallery.PhotoDetail
//import com.a602.commonproject.navigation.Navigator
//import com.a602.coommonproject.ui.MediaDetailAction
//
//// 캘린더 보기
//fun EntryProviderScope<NavKey>.calendarEntry(
//    navigator: Navigator
//) {
//    entry<CalendarNavKey> {
//        CalendarScreen(
//            medias = sampleMedias,
//            onBackClick = { navigator.goBack() },
//            onDateClick = { date ->
//                navigator.navigate(
//                    DetailNavKey(date =date.toString())
//                )
//            }
//        )
//    }
//}
//
//
//// 그리드 보기
//fun EntryProviderScope<NavKey>.galleryEntry(
//    navigator: Navigator
//) {
//    entry<GalleryNavKey> {
//
//        GridGallery(
//            polaroids = polaroids,
//            onCalendarClick = {
//                navigator.navigate(CalendarNavKey)
//            },
//            onPolaroidClick = { polaroid ->
//                navigator.navigate(
//                    DetailNavKey(mediaId)
//                )
//            }
//        )
//    }
//}
//
//// DetailPhoto
//fun EntryProviderScope<NavKey>.detailEntry(
//    navigator: Navigator
//) {
//    entry<DetailNavKey> { key ->
//
//        val uiState = remember(key.mediaId) {
//            // TODO: repository에서 mediaId로 데이터 로드
//            sampleDetailUiState
//        }
//
//        PhotoDetail(
//            uiState = uiState,
//            onAction = { action ->
//                when (action) {
//
//                    MediaDetailAction.Back ->
//                        navigator.goBack()
//
//                    MediaDetailAction.Edit ->
//                        navigator.navigate(
//                            CommentEditNavKey(key.mediaId)
//                        )
//
//                    MediaDetailAction.Delete ->
//                        navigator.goBack()
//
//                    MediaDetailAction.PlayPause ->
//                    { /* 상태 변경 */ }
//
//                    else -> {}
//                }
//            }
//        )
//    }
//}
//
//
////HighlightCalendarScreen 날짜 선택 캘린더
//
//fun EntryProviderScope<NavKey>.highlightCalendarEntry(
//    navigator: Navigator
//) {
//    entry<HighlightCalendarNavKey> {
//        HighlightCalendarScreen(
//            onNavigateToLoading = {
//                navigator.navigate(
//                    HighlightLoadingNavKey
//                )
//            },
//            onBack = navigator::goBack
//        )
//    }
//}
//
//
////CommentEditScreen 코멘트 작성
//fun EntryProviderScope<NavKey>.commentEditNavKey(navigator: Navigator){
//    entry<CommentEditNavKey> { key ->
//}
