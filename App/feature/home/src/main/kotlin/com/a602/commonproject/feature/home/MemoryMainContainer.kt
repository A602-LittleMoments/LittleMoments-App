package com.a602.commonproject.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.LMNavigationDefaults.NavigationBarHeight
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.feature.home.viewmodel.MemoryMainSideEffect
import com.a602.commonproject.feature.home.viewmodel.MemoryMainUiState
import com.a602.commonproject.feature.home.viewmodel.MemoryMainViewModel
import androidx.compose.material3.CircularProgressIndicator


import com.a602.commonproject.feature.home.viewmodel.SlideshowRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryMainContainer(
    viewModel: MemoryMainViewModel,
    onOpenGrid: (String, String) -> Unit, // id, label
    onNavigateToCamera: (Boolean) -> Unit,
    onNavigateToSlideshow: (SlideshowRequest) -> Unit,
    onNavigateToNotification: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Side Effect 관찰
    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is MemoryMainSideEffect.NavigateToCamera -> onNavigateToCamera(effect.isPhoto)
                is MemoryMainSideEffect.NavigateToSlideshow -> onNavigateToSlideshow(effect.request)
            }
        }
    }

    // Status Bar 색상 제어 (이 화면에서는 흰색 아이콘 사용)
    // Status Bar 색상 및 아이콘은 이제 글로벌 테마(Theme.kt)에서 관리합니다.
    // NavyBlue 배경 + White 아이콘으로 통일되었습니다.

    // 다이얼로그 상태 관리
    var showCameraDialog by remember { mutableStateOf(false) }
    var showSlideshowDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(bottom = NavigationBarHeight)
            .navigationBarsPadding()
    ) {
        when (uiState) {
            MemoryMainUiState.Empty -> MemoryEmptyScreen()
            is MemoryMainUiState.Main -> {
                val items = (uiState as MemoryMainUiState.Main).collections
                MemoryScreen(
                    items = items,
                    onPlanetClick = onOpenGrid,
                    onCameraClick = { viewModel.onCameraAction(true) },
                    onMakeSlideshowClick = { showSlideshowDialog = true },
                    onNotificationClick = onNavigateToNotification,
                    onHelpClick = { showHelpDialog = true }
                )
            }


            is MemoryMainUiState.Error -> MemoryEmptyScreen()
            MemoryMainUiState.Loading -> {
                 Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 카메라 다이얼로그
        if (showCameraDialog) {
            BasicAlertDialog(
                onDismissRequest = { showCameraDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                MemoryDialogContent(
                    title = "우리 아이와의 추억을 남겨봐요!",
                    imageResId = R.drawable.boy_camera,
                    buttons = listOf(
                        "사진 찍기" to {
                            showCameraDialog = false
                            viewModel.onCameraAction(true)
                        },
                        "영상 만들기" to {
                            showCameraDialog = false
                            viewModel.onCameraAction(false)
                        }
                    ),
                    onDismiss = { showCameraDialog = false }
                )
            }
        }

        // 슬라이드쇼 다이얼로그 (New Custom Dialog)
        if (showSlideshowDialog) {
            val currentCollections = (uiState as? MemoryMainUiState.Main)?.collections ?: emptyList()
            SlideshowCreationDialog(
                collections = currentCollections,
                onConfirm = { request ->
                    showSlideshowDialog = false
                    viewModel.onSlideshowAction(request)
                },
                onDismiss = { showSlideshowDialog = false }
            )
        }
        }

        // 도움말 다이얼로그 (튜토리얼)
        if (showHelpDialog) {
            BasicAlertDialog(
                onDismissRequest = { showHelpDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                HelpDialogContent(onDismiss = { showHelpDialog = false })
            }
        }
    }


@Composable
fun HelpDialogContent(onDismiss: () -> Unit) {
    // 0: 사진 찍기 가이드, 1: 영상 만들기 가이드
    var page by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .clickable(enabled = false) {}
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "우리 아이와의 추억을 남겨봐요!",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = com.a602.commonproject.designsystem.theme.main
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 컨텐츠 영역 (이미지 + 텍스트 + 화살표)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 왼쪽 화살표 (페이지 0일때 숨김)
                if (page > 0) {
                     androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "이전",
                        tint = Color(0xFFFFCC00), // 노란색
                        modifier = Modifier.size(32.dp).clickable { page-- }
                    )
                } else {
                    Spacer(modifier = Modifier.size(32.dp))
                }

                // 중앙 컨텐츠
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = if (page == 0) R.drawable.boy_camera else R.drawable.girl_slideshow),
                        contentDescription = null,
                        modifier = Modifier.size(128.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (page == 0) "사진 찍기" else "영상 만들기",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = com.a602.commonproject.designsystem.theme.main)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                         text = if (page == 0) "위 이미지를 누르면\n아이와의 순간을 촬영할 수 있어요." else "위 이미지를 누르면\n아이와의 추억 영상을 만들 수 있어요.",
                         style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                         textAlign = TextAlign.Center
                    )
                }

                // 오른쪽 화살표 (페이지 1일때 숨김)
                if (page < 1) {
                     androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "다음",
                        tint = Color(0xFFFFCC00),
                        modifier = Modifier.size(32.dp).clickable { page++ }
                    )
                } else {
                    Spacer(modifier = Modifier.size(32.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 닫기 버튼 (텍스트)
            Text(
                text = "닫기",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onDismiss() }
            )
        }
    }
}

@Composable
fun MemoryDialogContent(
    title: String,
    subTitle: String? = null,
    imageResId: Int,
    buttons: List<Pair<String, () -> Unit>>,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss), // 바깥 클릭 닫기
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .clickable(enabled = false) {} // 내부 클릭 무시
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 이미지 (상단 배치)
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            if (subTitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 버튼 목록
            buttons.forEach { (text, onClick) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF4CC)) // 연한 노란색/주황색 계열 (디자인 참고)
                        .clickable(onClick = onClick)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.Black)
                    )
                }
            }
        }
    }
}
