package com.a602.commonproject.feature.gallery.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.navigation.Navigator
import com.a602.commonproject.feature.gallery.CalendarRoute
import com.a602.commonproject.feature.gallery.GalleryNavKey
import com.a602.commonproject.feature.gallery.GridNavKey
import com.a602.commonproject.feature.gallery.GridRoute
import com.a602.commonproject.feature.gallery.HighlightCalendarNavKey
import com.a602.commonproject.feature.gallery.HighlightCalendarRoute
import com.a602.commonproject.feature.gallery.HighlightLoadingNavKey
import com.a602.commonproject.feature.gallery.HighlightLoadingRoute
import com.a602.commonproject.feature.gallery.PhotoDetailNavKey
import com.a602.commonproject.feature.gallery.MediaDetailRoute
import com.a602.commonproject.feature.gallery.TempAlbumNavKey
import com.a602.commonproject.feature.gallery.TempGridGalleryRoute

/**
 * 메인 화면: 캘린더
 */
fun EntryProviderScope<NavKey>.galleryEntries(
    navigator: Navigator,
) {

    entry<GalleryNavKey> {
        CalendarRoute(
//            onBackClick = { navigator.goBack() },
            onDateClick = {navigator.navigate(GridNavKey)},
            onGridClick= {navigator.navigate(GridNavKey)},
            onTempAlbumClick={navigator.navigate(TempAlbumNavKey)},
            onHighLightClick= {navigator.navigate(HighlightCalendarNavKey)}
        )
    }

    entry<GridNavKey> {
        GridRoute(
            onCalendarClick = { navigator.navigate(GalleryNavKey) },
            onMediaClick = { media ->
                navigator.navigate(PhotoDetailNavKey(mediaId = ""))
            }
        )
    }

    entry<PhotoDetailNavKey> { key ->
    /*    val media = remember(key.mediaId) {
            mediasProvider().firstOrNull { it.id == key.mediaId }
        }
        if (media == null) {
            LaunchedEffect(key.mediaId) { navigator.goBack() }
            return@entry
        }*/

        MediaDetailRoute(
            onBack = navigator::goBack,
            onDelete = navigator::goBack,
            onDownload = navigator::goBack,
            onEdit = navigator::goBack,
        )
    }

    entry<TempAlbumNavKey> {
        TempGridGalleryRoute(
           // Todo
        )
    }

    entry<HighlightCalendarNavKey> {
        HighlightCalendarRoute(

        )
    }

    entry<HighlightLoadingNavKey> { key ->
        HighlightLoadingRoute(

        )
    }

}
