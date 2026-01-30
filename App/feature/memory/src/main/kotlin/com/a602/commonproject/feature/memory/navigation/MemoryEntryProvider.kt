package com.a602.commonproject.feature.memory.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.memory.MediaDetailScreen
import com.a602.commonproject.feature.memory.MediaGridScreen
import com.a602.commonproject.feature.memory.MemoryDetailViewModel
import com.a602.commonproject.feature.memory.MemoryGridViewModel
import com.a602.commonproject.feature.memory.MemoryMainContainer
import com.a602.commonproject.feature.memory.MemoryMainViewModel
import com.a602.commonproject.navigation.Navigator

fun EntryProviderScope<NavKey>.memoryEntryProvider(
    navigator: Navigator
) {
    // 1) Main (데이터 있을때, 없을때 분기 포함)
    entry<MemoryMainKey> { key ->
        val viewModel = hiltViewModel<MemoryMainViewModel, MemoryMainViewModel.Factory> {
            it.create(key)
        }

        MemoryMainContainer(
            viewModel = viewModel,
            onOpenGrid = { keywordId ->
                MemoryNavArgsStore.setKeywordId(keywordId)
                MemoryNavArgsStore.clearMediaId()
                navigator.navigate(MemoryGridKey)
            }
        )
    }

    // 2) GRID (행성 선택 후 사진 목록)
    entry<MemoryGridKey> { key ->
        val viewModel = hiltViewModel<MemoryGridViewModel, MemoryGridViewModel.Factory> {
            it.create(key)
        }

        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

        MediaGridScreen(
            title = "키워드 앨범",
            medias = uiState.medias,
            onBackClick = {
                MemoryNavArgsStore.clearMediaId()
                navigator.goBack()
            },
            onMediaClick = { mediaId ->
                MemoryNavArgsStore.setMediaId(mediaId)
                navigator.navigate(MemoryDetailKey)
            }
        )
    }

    // 3) DETAIL (사진 상세)
    entry<MemoryDetailKey> { key ->
        val viewModel = hiltViewModel<MemoryDetailViewModel, MemoryDetailViewModel.Factory> {
            it.create(key)
        }

        val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
        val media = uiState.media ?: return@entry

        MediaDetailScreen(
            title = "자세히 보기",
            media = media,
            onBack = {
                MemoryNavArgsStore.clearMediaId()
                navigator.goBack()
            },
            onDelete = viewModel::onDelete,
            onDownload = viewModel::onDownload,
            onEdit = viewModel::onEdit
        )
    }
}
