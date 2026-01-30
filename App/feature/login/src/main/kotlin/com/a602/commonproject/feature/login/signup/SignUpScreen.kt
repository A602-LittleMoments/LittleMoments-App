package com.a602.commonproject.feature.login.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.model.data.Baby
import java.io.File

/**
 * 회원가입 화면의 진입점(Route)입니다.
 * ViewModel의 상태를 관찰하고, 성공/에러 처리 및 네비게이션 로직을 담당합니다.
 *
 * @param onSignUpSuccess 회원가입 및 그룹 설정이 모두 완료되었을 때 호출되는 콜백
 * @param onBackClick 뒤로가기 버튼 클릭 시 호출
 */
@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel = hiltViewModel(),
    onSignUpSuccess: () -> Unit,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 다이얼로그가 안 떠있고 성공 상태면 네비게이션 이동 (홈으로)
    LaunchedEffect(uiState.isSuccess, uiState.showGroupDialog) {
        if (uiState.isSuccess && !uiState.showGroupDialog) {
            onSignUpSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    SignUpScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onSignUpClick = viewModel::signUp,
        onCreateGroup = viewModel::createGroup,
        onJoinGroup = viewModel::joinGroup,
        onAddBaby = viewModel::addBaby,
        updateUserInfo = viewModel::updateUserInfo,
    )
}

/**
 * 회원가입 UI의 메인 스크린입니다.
 * 기본 정보 입력 화면을 보여주며, 조건에 따라 그룹 설정 다이얼로그([GroupSettingDialog])를 띄웁니다.
 *
 * @param uiState 화면 UI 상태
 * @param snackbarHostState 스낵바 상태 호스트
 * @param onSignUpClick "가입하기" 버튼 클릭 시 실행 (기본 정보 전송)
 * @param onCreateGroup 그룹 생성 요청 시 실행
 * @param onJoinGroup 그룹 참여 요청 시 실행
 */
@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onCreateGroup: (String, String) -> Unit,
    onJoinGroup: (String, String) -> Unit,
    onAddBaby: (String, String, Baby.Gender, File?) -> Unit,
    updateUserInfo: (String, String, String, String) -> Unit,
) {
    BackHandler {
        onBackClick()
    }

    // Flag to control the visibility of the "Join Group" dialog
    var showJoinDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFFFF9E6), // 아이보리 배경
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
            ) {
                // 뒤로가기는 UserInfo 입력 단계에서만 허용 (그룹 생성 단계에서는 이미 가입됨)
                if (!uiState.showGroupDialog) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterStart),
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
                Text(
                    text = when {
                        uiState.showBabyForm -> "아이 등록"
                        uiState.showGroupDialog -> "그룹 생성"
                        else -> "회원 가입"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // 1. 회원 정보 입력 단계
            if (!uiState.showGroupDialog) {
                UserInfoContent(
                    uiState = uiState,
                    onSignUp = { email, pw, nick ->
                        updateUserInfo(email, pw, nick, "dummy_token")
                        onSignUpClick()
                    },
                )
            }
            // 2. 아기 등록 단계 (그룹 생성 후)
            else if (uiState.showBabyForm) {
                AddBabyContent(
                    onAddBaby = onAddBaby,
                )
            }
            // 3. 그룹 생성 단계 (이미 가입 완료, 그룹 없음)
            else {
                CreateGroupContent(
                    onCreateGroup = onCreateGroup,
                    onOpenJoinDialog = { showJoinDialog = true },
                )
            }

            // 3. 그룹 참여 다이얼로그 (버튼 클릭 시에만 표시)
            if (showJoinDialog) {
                JoinGroupDialog(
                    onDismissRequest = { showJoinDialog = false },
                    onJoinGroup = onJoinGroup,
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}


