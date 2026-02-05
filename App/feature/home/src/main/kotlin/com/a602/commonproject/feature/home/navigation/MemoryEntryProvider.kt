package com.a602.commonproject.feature.home.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.album.GridNavKey
import com.a602.commonproject.feature.home.viewmodel.MemoryMainViewModel
import com.a602.commonproject.navigation.Navigator
import com.a602.commonproject.feature.camera.navigation.CameraNavKey
import com.a602.commonproject.feature.home.HighlightLoadingRoute
import com.a602.commonproject.feature.home.NotificationScreen

import kotlinx.coroutines.delay
import com.a602.commonproject.feature.home.LoadingContent
import com.a602.commonproject.feature.home.MemoryMainContainer
import com.a602.commonproject.feature.home.viewmodel.SlideshowEntryViewModel

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
            onOpenGrid = { keywordId, label ->
                navigator.navigate(GridNavKey(keywordId = keywordId, title = label))
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



    // 7. 하이라이트 로딩
    entry<HighlightLoadingNavKey> { key ->
        HighlightLoadingRoute(
            startMillis = key.startMillis,
            endMillis = key.endMillis,
            onSuccess = {},
            onFailure = {
                // TODO: 에러 토스트 또는 스낵바
                navigator.goBack()
            }
        )
    }

    entry<SlideshowEntryKey> { key ->
        val viewModel = hiltViewModel<SlideshowEntryViewModel>()
        
        // API 호출 (한 번만 실행)
        LaunchedEffect(key.request) {
            viewModel.createSlideshow(
                request = key.request,
                onSuccess = {
                    // 성공 시 2초 후 뒤로 가기
                    delay(2000)
                    navigator.goBack()
                },
                onFailure = {
                    // 실패 시에도 뒤로 가기
                    delay(1000)
                    navigator.goBack()
                }
            )
        }

        LoadingContent(
            title = "영상 생성을 요청했습니다!",
            subTitle = "요청이 성공적으로 전송되었습니다."
        )
    }
}
