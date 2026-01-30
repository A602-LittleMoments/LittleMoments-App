package com.a602.commonproject.feature.home

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.component.ProfileFullAstronaut
import com.a602.commonproject.designsystem.component.SaveButton
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.feature.home.components.MemorableMoments
import com.a602.commonproject.feature.home.components.TimelineSection
import com.a602.commonproject.model.data.Baby

// 홈 화면의 라우트 (진입점) 역할을 하는 Composable 입니다.
// ViewModel을 주입받아 UI 상태를 구독하고, 상태에 따라 적절한 화면을 보여줍니다.
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(), // Hilt를 사용하여 HomeViewModel 주입
    onNotificationClick: () -> Unit, // 알림 아이콘 클릭 시 동작할 콜백
    onNavigateToUpload: () -> Unit // 업로드 버튼 클릭 시 동작할 콜백
) {
    // ViewModel의 uiState를 State로 수집 (Collect)하여 UI에 반영
    val uiState by viewModel.uiState.collectAsState()

    // UI 상태에 따른 분기 처리
    when (val state = uiState) {
        // 로딩 상태일 때: 화면 중앙에 로딩 인디케이터 표시
        is HomeUiState.Loading -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        // 에러 상태일 때: 화면 중앙에 에러 메시지 표시
        is HomeUiState.Error -> Box(Modifier.fillMaxSize()) { Text("Error : ${state.errorMessage}", Modifier.align(Alignment.Center)) }
        // 성공 상태일 때: 실제 홈 화면 (HomeScreen) 표시
        is HomeUiState.Success -> {
            HomeScreen(
                uiState = state,
                onNotificationClick = onNotificationClick,
                onNavigateToUpload = onNavigateToUpload,
            )
        }
    }
    Log.d("uiState", "HomeRoute: $uiState")
}

// 실제 홈 화면의 UI를 그리는 Composable 입니다.
// 상위 HomeRoute에서 전달받은 '성공' 상태의 데이터(uiState)를 기반으로 화면을 구성합니다.
@Composable
fun HomeScreen(
    uiState: HomeUiState.Success, // 성공 상태의 데이터 (아기 정보, 컬렉션, 타임라인 등 포함)
    onNotificationClick: () -> Unit,
    onNavigateToUpload: () -> Unit,
) {
    // Scaffold는 기본적인 메테리얼 디자인 레이아웃 구조를 제공합니다.
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        // LazyColumn을 사용하여 스크롤 가능한 리스트 형태의 UI 구성
        // 전체 화면의 패딩 값(paddingValues)을 contentPadding으로 적용
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 아기 프로필 영역 (우주복 이미지)
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    // 아기 정보가 있을 경우 프로필 이미지를 보여줌
                    if (uiState.baby != null) {
                         ProfileFullAstronaut(
                            selectedImageUri = null,
                            remoteImageUrl = uiState.baby.imageUrl,
                            clickableEnabled = false, // 클릭 불가능하도록 설정
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    } else {
                        // 아기 정보가 없을 경우 빈 상태 표시
                        EmptyBabyState()
                    }
                }
            }

            // 2. 기억하고 싶은 순간 (컬렉션) 슬라이드 영역
            // 컬렉션(추억) 데이터가 있을 경우에만 표시
            if (uiState.collections.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    MemorableMoments(collections = uiState.collections)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // 3. 사진 리스트 (타임라인) 영역
            // 업로드된 미디어 리스트를 타임라인 형태로 표시
            item {
                TimelineSection(mediaList = uiState.mediaList)
            }

            // 미디어가 하나도 없을 경우 (업로드 유도 화면)
            if (uiState.mediaList.isEmpty()) {
                item {
                    EmptyTimelineState()
                }
            } else {
                 // 미디어가 있는 경우
                item {
                   Spacer(modifier = Modifier.height(32.dp)) // 하단 여백 추가
                }
            }
        }
    }
}


// 아기 정보가 없을 때 보여줄 UI (Empty State)
@Composable
fun EmptyBabyState() {
     Box(modifier = Modifier
         .fillMaxWidth()
         .height(200.dp), contentAlignment = Alignment.Center) {
         Text("아기를 등록해주세요!", color = Color.Gray)
     }
}

// 타임라인(사진)이 없을 때 보여줄 UI (Empty State)
// 사용자에게 사진 업로드를 유도하는 메시지와 버튼을 포함
@Composable
fun EmptyTimelineState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "사진 속에 자라는 우리 아이,\nAI가 성장의 기록을\n앨범으로 담아드려요.",
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}


