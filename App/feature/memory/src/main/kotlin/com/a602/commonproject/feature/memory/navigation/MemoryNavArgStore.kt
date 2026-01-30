package com.a602.commonproject.feature.memory.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object MemoryNavArgsStore {
    private val _keywordId = MutableStateFlow<String?>(null)
    val keywordId: StateFlow<String?> = _keywordId

    private val _mediaId = MutableStateFlow<String?>(null)
    val mediaId: StateFlow<String?> = _mediaId

    fun setKeywordId(id: String) {
        _keywordId.value = id
    }

    fun setMediaId(id: String) {
        _mediaId.value = id
    }

    fun clearMediaId() {
        _mediaId.value = null
    }
}
