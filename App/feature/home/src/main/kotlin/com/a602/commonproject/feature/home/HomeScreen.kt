package com.a602.commonproject.feature.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a602.commonproject.designsystem.component.CameraButton
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.component.ProfileFullAstronaut
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.feature.home.components.MemorableMoments
import com.a602.commonproject.feature.home.components.TimelineSection
import com.a602.commonproject.model.data.Baby

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onNotificationClick: () -> Unit,
    onNavigateToUpload: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        is HomeUiState.Error -> Box(Modifier.fillMaxSize()) { Text("Error", Modifier.align(Alignment.Center)) }
        is HomeUiState.Success -> {
            HomeScreen(
                uiState = state,
                onNotificationClick = onNotificationClick,
                onNavigateToUpload = onNavigateToUpload
            )
        }
    }
}

@Composable
fun HomeScreen(
    uiState: HomeUiState.Success,
    onNotificationClick: () -> Unit,
    onNavigateToUpload: () -> Unit
) {
    // Scaffold removed as per requirement "BottomBar handles navigation... Main screen only needs content"
    // However, TopBar IS needed. So we use Column or Scaffold without bottomBar.
    // Given LMApp has BottomBar, and we want TopBar here:
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 아기 프로필 영역 (우주복)
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (uiState.baby != null) {
                         ProfileFullAstronaut(
                            selectedImageUri = null,
                            remoteImageUrl = uiState.baby.imageUrl,
                            clickableEnabled = false,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    } else {
                        EmptyBabyState()
                    }
                }
            }

            // 2. 기억하고 싶은 순간 (컬렉션)
            if (uiState.collections.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    MemorableMoments(collections = uiState.collections)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // 3. 사진 리스트 (타임라인)
            item {
                TimelineSection(mediaList = uiState.mediaList)
            }

            if (uiState.mediaList.isEmpty()) {
                item {
                    EmptyTimelineState(onUploadClick = onNavigateToUpload)
                }
            } else {
                 // 리스트가 있으면 TimelineSection 내에서 처리됨 (하지만 리스트 아래 버튼은?)
                 // TimelineSection이 전체 리스트를 그리므로, 그 아래에 버튼 추가
                item {
                   Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                       CameraButton(onClick = onNavigateToUpload)
                   }
                   Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyBabyState() {
     Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
         Text("아기를 등록해주세요!", color = Color.Gray)
     }
}

@Composable
fun EmptyTimelineState(onUploadClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "사진 속에 자라는 우리 아이,\nAI가 성장의 기록을\n앨범으로 담아드려요.",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        CameraButton(onClick = onUploadClick)
    }
}


