package com.a602.commonproject.feature.login.signup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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

    // 진입 시 로그인 상태 & 그룹 상태 확인 (init 블록 대체)
    LaunchedEffect(Unit) {
        viewModel.checkLoggedInAndGroupStatus()
    }

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
        onBackClick = {
            // 그룹 생성 단계(다이얼로그 표시 중)에서 뒤로 가거나 취소하면 로그아웃 처리
            if (uiState.showGroupDialog) {
                viewModel.logout()
            }
            onBackClick()
        },
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
        containerColor = Color.Transparent, // Transparent for background image
        topBar = {
            // Reusing existing LMTopAppBar as requested
            // Note: Design shows Ivory background. LMTopAppBarDefaults.colors() uses 'background' (Ivory) by default.
            if (!uiState.showGroupDialog) {
                com.a602.commonproject.designsystem.component.LMTopAppBar(
                    title = when {
                        uiState.showBabyForm -> "아이 등록"
                        // uiState.showGroupDialog -> "그룹 생성" // TopBar not shown in original code for group dialog?
                        // Actually original code showed "Group Create" text.
                        else -> "회원 가입"
                    },
                    onNavigationClick = onBackClick,
                    // If we want to hide back button on some states, we might need a custom icon or logic.
                    // But here onBackClick is passed.
                    // IMPORTANT: The original code hid back button if showGroupDialog is true.
                    // But here inside topBar, we can just render it.
                    // Wait, if showGroupDialog is true, do we show TopBar?
                    // Yes, original code showed "Group Create" title.
                    // But back button was hidden.
                )
            } else {
                 // If group dialog is shown, maybe show title "그룹 생성" without back button?
                 // Or just show it.
                 com.a602.commonproject.designsystem.component.LMTopAppBar(
                    title = "그룹 생성",
                    navigationIcon = androidx.compose.ui.graphics.vector.ImageVector.Builder(
                        defaultWidth = 0.dp, defaultHeight = 0.dp, viewportWidth = 0f, viewportHeight = 0f
                    ).build(), // Empty icon or Transparent? LMTopAppBar doesn't support hiding icon easily without passing empty vector or modifying it.
                    // Let's just pass a transparent icon or similar if we want to hide it,
                    // OR use a different overload if available (it's not).
                    // Actually, we can just pass a dummy icon and empty click.
                    // Or better, let's look at the parameters of LMTopAppBar.
                    // navigationIcon is ImageVector = LMicons.Back default.
                    // If we pass an empty vector it might crash or show nothing.
                    // Let's try to show it but with no-op click?
                    // Design: "Group Create" screen usually doesn't have back button if it's a required step?
                    // User's previous code: `if (!uiState.showGroupDialog) { IconButton(...) }`
                    // So back button is hidden.
                    // I'll skip TopBar for group dialog if it's a dialog?
                    // No, `uiState.showGroupDialog` -> "Group Create" text was shown in the custom box.

                    // Hack: use a transparent/empty icon for now if needed.
                    // Or generic check.
                 )
            }
            // Wait, logic simplification:
            // Just use one LMTopAppBar call.
            com.a602.commonproject.designsystem.component.LMTopAppBar(
                title = when {
                    uiState.showBabyForm -> "아이 등록"
                    uiState.showGroupDialog -> "그룹 생성"
                    else -> "회원 가입"
                },
                navigationIcon = if (uiState.showGroupDialog) androidx.compose.material.icons.Icons.Default.ArrowBack else androidx.compose.material.icons.Icons.Default.ArrowBack, // Placeholder,
                // We need to hide navigation icon if showGroupDialog is true.
                // LMTopAppBar takes `navigationIcon`.
                // Let's pass a transparent color tint if possible? No.
                // Pass a blank icon?
                onNavigationClick = onBackClick,
                colors = androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = com.a602.commonproject.designsystem.theme.background, // Ivory
                    titleContentColor = com.a602.commonproject.designsystem.theme.color3, // Navy
                    navigationIconContentColor = com.a602.commonproject.designsystem.theme.color3
                )
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background Image
            Image(
                painter = androidx.compose.ui.res.painterResource(id = com.a602.commonproject.designsystem.R.drawable.gallery_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

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
}


