package com.a602.commonproject.feature.home.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.home.MediaDetailScreen
import com.a602.commonproject.feature.home.MediaGridScreen
import com.a602.commonproject.feature.home.viewmodel.MemoryDetailViewModel
import com.a602.commonproject.feature.home.viewmodel.MemoryGridViewModel
import com.a602.commonproject.feature.home.MemoryMainContainer
import com.a602.commonproject.feature.home.viewmodel.MemoryMainViewModel
import com.a602.commonproject.navigation.Navigator


import com.a602.commonproject.feature.camera.navigation.CameraNavKey
import com.a602.commonproject.feature.home.navigation.NotificationNavKey
import com.a602.commonproject.feature.home.NotificationScreen

fun EntryProviderScope<NavKey>.memoryEntries(
    navigator: Navigator
) {
    entry<NotificationNavKey> {
        NotificationScreen(
            onBackClick = { navigator.goBack() }
        )
    }
    // 1) Main (데이터 있을때, 없을때 분기 포함)
    entry<MemoryNavKey> { key ->
        val viewModel = hiltViewModel<MemoryMainViewModel>()

        MemoryMainContainer(
            viewModel = viewModel,
            onOpenGrid = { keywordId ->
                MemoryNavArgsStore.setKeywordId(keywordId)
                MemoryNavArgsStore.clearMediaId()
                navigator.navigate(MemoryGridKey)
            },
            onNavigateToCamera = { isPhoto ->
                // 실제 Camera 모듈 NavKey 사용
                navigator.navigate(CameraNavKey)
            },
            onNavigateToSlideshow = { request ->
                navigator.navigate(SlideshowEntryKey(request))
            },
            onNavigateToNotification = {
                navigator.navigate(NotificationNavKey)
            }
        )
    }

    // 2) GRID (행성 선택 후 사진 목록)
    entry<MemoryGridKey> { key ->
        val viewModel = hiltViewModel<MemoryGridViewModel>()

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
        val viewModel = hiltViewModel<MemoryDetailViewModel>()

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
    entry<SlideshowEntryKey> { key ->
        // Auto-dismiss Success Screen
        androidx.compose.runtime.LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000) // 2 seconds delay
            navigator.goBack()
        }

        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.White),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "🎞️",
                    style = androidx.compose.material3.MaterialTheme.typography.displayMedium
                )
                androidx.compose.material3.Text(
                    text = "영상 생성을 요청했습니다!",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = com.a602.commonproject.designsystem.theme.main
                )
                androidx.compose.material3.Text(
                    text = "잠시 후 메인 화면으로 돌아갑니다.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
        }
    }
}
