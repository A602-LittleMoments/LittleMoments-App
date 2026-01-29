package com.a602.commonproject.feature.gallery.navigation


import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.gallery.HighlightCalendarNavKey
import com.a602.commonproject.feature.gallery.HighlightCalendarScreen
import com.a602.commonproject.feature.gallery.HighlightLoadingNavKey
import com.a602.commonproject.navigation.Navigator



//HighlightCalendarScreen 날짜 선택 캘린더

fun EntryProviderScope<NavKey>.highlightCalendarEntry(
    navigator: Navigator
) {
    entry<HighlightCalendarNavKey> {
        HighlightCalendarScreen(
            onNavigateToLoading = {
                navigator.navigate(
                    HighlightLoadingNavKey
                )
            },
            onBack = navigator::goBack
        )
    }
}
