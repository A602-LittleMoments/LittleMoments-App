package com.a602.commonproject.feature.home.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import com.a602.commonproject.feature.home.viewmodel.SlideshowRequest

// 💡 Copied/Placeholder Keys for external features

@Serializable
data object MemoryNavKey : NavKey



@Serializable
data object MemoryDetailKey : NavKey


@Serializable
data class SlideshowEntryKey(val request: SlideshowRequest) : NavKey


@Serializable
data class HighlightLoadingNavKey(
    val startMillis: Long,
    val endMillis: Long
) : NavKey
