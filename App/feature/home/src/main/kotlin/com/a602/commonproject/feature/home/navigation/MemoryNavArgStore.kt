package com.a602.commonproject.feature.home.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object MemoryNavArgsStore {
    private val _keywordId = MutableStateFlow<String?>(null)
    val keywordId: StateFlow<String?> = _keywordId

    private val _mediaId = MutableStateFlow<String?>(null)
    val mediaId: StateFlow<String?> = _mediaId

    private val _keywordValue = MutableStateFlow<String?>(null)
    val keywordValue: StateFlow<String?> = _keywordValue

    fun setKeywordId(id: String) {
        _keywordId.value = id
    }

    fun setKeywordValue(value: String) {
        _keywordValue.value = value
    }

    fun setMediaId(id: String) {
        _mediaId.value = id
    }

    fun clearMediaId() {
        _mediaId.value = null
    }
}
