package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.LMNavigationDefaults
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.component.GroupRoleChangeDialog
import com.a602.commonproject.designsystem.component.GroupCodeDialog
import com.a602.commonproject.designsystem.icon.LMicons
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.a602.commonproject.feature.mypage.navigation.GroupCreateKey
import com.a602.commonproject.feature.mypage.navigation.GroupJoinKey
import com.a602.commonproject.feature.mypage.navigation.GroupManageKey
import com.a602.commonproject.feature.mypage.navigation.KidAddKey
import com.a602.commonproject.feature.mypage.navigation.KidEditKey
import com.a602.commonproject.feature.mypage.navigation.ProfileEditKey
import com.a602.commonproject.feature.login.navigation.LoginNavKey
import com.a602.commonproject.feature.login.navigation.SplashNavKey
import com.a602.commonproject.feature.mypage.viewmodel.MyPageUiState
import com.a602.commonproject.feature.mypage.viewmodel.MyPageViewModel
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.model.data.Group
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.GroupRole
import com.a602.commonproject.model.data.User
import com.a602.commonproject.navigation.Navigator
import com.a602.commonproject.designsystem.component.LMEditInputField

@Composable
fun MyPageMainContainer(navigator: Navigator, viewModel: MyPageViewModel =hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 로그아웃 성공 시 로그인 화면으로 이동
    LaunchedEffect(uiState.isLogoutSuccess) {
        if (uiState.isLogoutSuccess) {
            navigator.replaceRoot(LoginNavKey)
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        MypageScreen(
            uiState = uiState,
            onNavigateToPasswordChange = { viewModel.openPasswordChangeDialog() },
            onNavigateToProfileEdit = { viewModel.openProfileEditDialog() },
            onNavigateToKidEdit = { babyId -> navigator.navigate(KidEditKey(babyId)) },
            onNavigateToKidAdd = { navigator.navigate(KidAddKey) },
            onNavigateToGroupManagement = { viewModel.openGroupEditDialog() },
            onNavigateToGroupJoin = { navigator.navigate(GroupJoinKey) },
            onNavigateToGroupCreate = { navigator.navigate(GroupCreateKey) }, // 그룹 만들기 화면으로 이동
            onLogoutClick = { viewModel.logout() },
            onGroupEditDismiss = viewModel::closeGroupEditDialog,
            onGroupNameChange = viewModel::onGroupNameChange,
            onGroupSave = viewModel::saveGroupName,
            // 초대 관련 콜백 연결
            onAddNewMemberClick = viewModel::onAddNewMemberClick,
            onRoleSelected = viewModel::onRoleSelected,
            onCloseInviteDialog = viewModel::closeInviteDialogs,
            onRefreshInviteCode = viewModel::refreshInviteCode,
            // 내 정보 수정 관련 콜백 연결
            onProfileEditDismiss = viewModel::closeProfileEditDialog,
            onNicknameChange = viewModel::onNicknameChange,
            onCurrentPasswordChange = viewModel::onCurrentPasswordChange,
            onNewPasswordChange = viewModel::onNewPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onNicknameSave = viewModel::saveNickname,
            onPasswordSave = viewModel::savePassword,
            onPasswordChangeDismiss = viewModel::closePasswordChangeDialog
        )
    }
}

/**
 * 마이페이지의 메인 화면 UI를 구성하는 컴포저블 함수입니다.
 *
 * @param user 화면에 표시할 사용자의 정보 (User 데이터 클래스).
 * @param babies 화면에 표시할 아기의 정보 (Baby 데이터 클래스).
 * @param groupMembers 화면에 표시할 그룹 멤버 목록.
 * @param onNavigateToProfileEdit '내 정보 수정' 버튼을 눌렀을 때 실행될 화면 이동 함수.
 * @param onNavigateToKidEdit '아이 정보 수정' 버튼을 눌렀을 때 실행될 화면 이동 함수.
 * @param onNavigateToKidAdd '아이 추가' 버튼을 눌렀을 때 실행될 화면 이동 함수.
 * @param onNavigateToGroupManagement '그룹 관리' 버튼을 눌렀을 때 실행될 화면 이동 함수.
 */
@Composable
fun MypageScreen(
    uiState: MyPageUiState,
    onNavigateToPasswordChange: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToKidEdit: (String) -> Unit,
    onNavigateToKidAdd: () -> Unit,
    onNavigateToGroupManagement: () -> Unit,
    onNavigateToGroupJoin: () -> Unit,
    onNavigateToGroupCreate: () -> Unit,
    onLogoutClick: () -> Unit = {},
    onGroupEditDismiss: () -> Unit = {},
    onGroupNameChange: (String) -> Unit = {},
    onGroupSave: () -> Unit = {},
    // 초대 관련 파라미터 추가
    onAddNewMemberClick: () -> Unit = {},
    onRoleSelected: (Boolean) -> Unit = {}, // true: Member, false: Viewer
    onCloseInviteDialog: () -> Unit = {},
    onRefreshInviteCode: () -> Unit = {},
    // 내 정보 수정 관련 파라미터 추가
    onProfileEditDismiss: () -> Unit = {},
    onNicknameChange: (String) -> Unit = {},
    onCurrentPasswordChange: (String) -> Unit = {},
    onNewPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onNicknameSave: () -> Unit = {},
    onPasswordSave: () -> Unit = {},
    onPasswordChangeDismiss: () -> Unit = {}
) {
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    var isEditingGroupName by remember { androidx.compose.runtime.mutableStateOf(false) }
    var isEditingNickname by remember { androidx.compose.runtime.mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 배경 이미지
        Image(
            painter = painterResource(id = com.a602.commonproject.designsystem.R.drawable.gallery_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 2. 상단 아이콘 (설정, 로그아웃)
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 설정 아이콘 (비밀번호 변경)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.1f)) // Semi-transparent glass effect
                        .border(1.dp, Color.White.copy(alpha = 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                        .clickable(onClick = onNavigateToPasswordChange),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "설정",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 로그아웃 아이콘
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.1f)) // Semi-transparent glass effect
                        .border(1.dp, Color.White.copy(alpha = 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                        .clickable(onClick = onLogoutClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = LMicons.logout,
                        contentDescription = "로그아웃",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            // 3. 메인 콘텐츠 (단일 카드 형태)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .padding(bottom = LMNavigationDefaults.NavigationBarHeight + 24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.user != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(), // 내용물에 맞게 높이 조절, 너무 길어지면 스크롤 필요할수도 있음
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 3-1. 상단 아이콘 (나의 권한에 맞는 아이콘 표시)
                            val profileIconRes = if (uiState.group?.role == GroupRole.VIEWER) {
                                com.a602.commonproject.designsystem.R.drawable.telescope
                            } else {
                                com.a602.commonproject.designsystem.R.drawable.camera
                            }

                             Image(
                                 painter = painterResource(id = profileIconRes),
                                 contentDescription = "Profile Icon",
                                 modifier = Modifier.size(80.dp)
                             )
                            Spacer(modifier = Modifier.height(16.dp))

                            // 3-2. 유저 닉네임 + 수정 아이콘 (팝업)
                            // 3-2. 유저 닉네임 + 수정 아이콘 (인라인 수정)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (isEditingNickname) {
                                    LMEditInputField(
                                        label = "닉네임",
                                        value = uiState.editingNickname,
                                        onValueChange = onNicknameChange,
                                        modifier = Modifier.width(200.dp),
                                        placeholder = "닉네임"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "저장",
                                        tint = NavyBlue,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                onNicknameSave()
                                                isEditingNickname = false
                                                keyboardController?.hide()
                                            }
                                    )
                                } else {
                                    Text(
                                        text = uiState.user.nickname,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NavyBlue,
                                            fontSize = 28.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = LMicons.edit_outline,
                                        contentDescription = "닉네임 수정",
                                        tint = NavyBlue,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                onNicknameChange(uiState.user.nickname)
                                                isEditingNickname = true
                                            }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            // 3-3. 이메일
                            Text(
                                text = uiState.user.email,
                                style = AppTypography.bodyMedium.copy(
                                    color = NavyBlue.copy(alpha = 0.6f)
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 3-4. 구분선
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(NavyBlue)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            if (uiState.hasGroup) {
                                // 3-5. 가족 이름 (가족 이름) + 수정 아이콘 (인라인)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center // 중앙 정렬 요청 이미지 기반
                                ) {
                                    if (isEditingGroupName) {
                                        // 인라인 수정 모드 (BasicTextField -> LMEditInputField)
                                        LMEditInputField(
                                            label = "가족 이름",
                                            value = uiState.editingGroupName,
                                            onValueChange = onGroupNameChange,
                                            modifier = Modifier.width(240.dp), // 충분한 너비 확보
                                            placeholder = "가족 이름"
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // 완료 버튼 (체크 아이콘)
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "저장",
                                            tint = NavyBlue,
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable {
                                                    onGroupSave()
                                                    isEditingGroupName = false
                                                    keyboardController?.hide()
                                                }
                                        )

                                    } else {
                                        // 보기 모드
                                        Text(
                                            text = uiState.group?.name ?: "우리 가족", // 그룹 이름 표시
                                            style = MaterialTheme.typography.headlineLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = NavyBlue,
                                                fontSize = 24.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = LMicons.edit_outline,
                                            contentDescription = "가족 이름 수정",
                                            tint = NavyBlue,
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clickable {
                                                    // 편집 시작 시 현재 이름으로 초기화
                                                    onGroupNameChange(uiState.group?.name ?: "")
                                                    isEditingGroupName = true
                                                }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // 3-6. 멤버 리스트
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally // Center the rows
                                ) {
                                    val members = uiState.groupMembers
                                    members.forEach { member ->
                                        Row(
                                            modifier = Modifier.width(220.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            val iconRes = if (member.role == GroupRole.VIEWER) {
                                                com.a602.commonproject.designsystem.R.drawable.telescope
                                            } else {
                                                com.a602.commonproject.designsystem.R.drawable.camera
                                            }

                                            Image(
                                                painter = painterResource(id = iconRes),
                                                contentDescription = null,
                                                modifier = Modifier.size(48.dp)
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Text(
                                                text = member.nickname,
                                                style = AppTypography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = NavyBlue,
                                                    fontSize = 20.sp
                                                ),
                                                modifier = Modifier.weight(1f),
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            val relationKorean = when(member.relation.uppercase()) {
                                                "MOTHER" -> "엄마"
                                                "FATHER" -> "아빠"
                                                "GRANDMOTHER" -> "할머니"
                                                "GRANDFATHER" -> "할아버지"
                                                "AUNT" -> "이모"
                                                "UNCLE" -> "삼촌"
                                                else -> member.relation
                                            }

                                            Text(
                                                text = relationKorean,
                                                style = AppTypography.bodyMedium.copy(
                                                    color = NavyBlue.copy(alpha = 0.6f),
                                                    fontWeight = FontWeight.Normal
                                                )
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                // 3-7. 새 멤버 추가 버튼
                                Button(
                                    onClick = onAddNewMemberClick,
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f) // 이미지상 너비가 좀 좁음
                                        .height(50.dp)
                                        .shadow(8.dp, RoundedCornerShape(12.dp)), // 그림자 추가
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NavyBlue
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp) // shadow modifier 사용
                                ) {
                                    Icon(
                                        imageVector = LMicons.person_add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "새 멤버 추가",
                                        style = AppTypography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            } else {
                                // 그룹 없음 상태
                                NoGroupSection(
                                    onCreateClick = onNavigateToGroupCreate,
                                    onJoinClick = onNavigateToGroupJoin
                                )
                            }
                        }
                    }
                }
            }
        }

        // 그룹 이름 수정 다이얼로그
        if (uiState.showGroupEditDialog) {
            Dialog(onDismissRequest = onGroupEditDismiss) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = OffWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "가족 이름 수정",
                                style = AppTypography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyBlue,
                                    fontSize = 24.sp
                                ),
                                modifier = Modifier.align(Alignment.Center)
                            )
                            IconButton(
                                onClick = onGroupEditDismiss,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .offset(x = 12.dp) // 약간 우측으로 이동해서 여백 확보 시각적 보정
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "닫기",
                                    tint = NavyBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        LMEditInputField(
                            label = "가족 이름",
                            value = uiState.editingGroupName,
                            onValueChange = onGroupNameChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "가족 이름을 입력하세요"
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = onGroupSave,
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyBlue
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "저장하기",
                                style = AppTypography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // 1) 권한 선택 다이얼로그
        if (uiState.showRoleSelectDialog) {
            GroupRoleChangeDialog(
                currentRole = "", // 신규 초대는 현재 역할이 없으므로 둘 다 선택 가능하게
                onDismiss = onCloseInviteDialog,
                onConfirm = { isMember -> onRoleSelected(isMember) },
                onCloseClick = onCloseInviteDialog
            )
        }

        // 2) 초대 코드 다이얼로그
        if (uiState.showInviteCodeDialog) {
            GroupCodeDialog(
                onDismissRequest = onCloseInviteDialog,
                onRefreshClick = onRefreshInviteCode,
                initialCode = uiState.currentInviteCode
            )
        }

        // 내 정보 수정 다이얼로그 (그룹 이름 수정과 동일한 디자인 적용)
        if (uiState.showProfileEditDialog) {
            Dialog(onDismissRequest = onProfileEditDismiss) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = OffWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "내 정보 수정",
                                style = AppTypography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyBlue,
                                    fontSize = 24.sp
                                ),
                                modifier = Modifier.align(Alignment.Center)
                            )
                            IconButton(
                                onClick = onProfileEditDismiss,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .offset(x = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "닫기",
                                    tint = NavyBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // 닉네임 입력
                        LMEditInputField(
                            label = "닉네임",
                            value = uiState.editingNickname,
                            onValueChange = onNicknameChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "닉네임을 입력하세요"
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onNicknameSave,
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyBlue
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "저장하기",
                                style = AppTypography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // 비밀번호 변경 다이얼로그 (설정 버튼 클릭 시)
        if (uiState.showPasswordChangeDialog) {
            Dialog(onDismissRequest = onPasswordChangeDismiss) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = OffWhite,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "비밀번호 변경",
                                style = AppTypography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyBlue,
                                    fontSize = 24.sp
                                ),
                                modifier = Modifier.align(Alignment.Center)
                            )
                            IconButton(
                                onClick = onPasswordChangeDismiss,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .offset(x = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "닫기",
                                    tint = NavyBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        val errorMessage = uiState.passwordChangeError
                        val isMismatchError = errorMessage?.contains("새 비밀번호") == true
                        val isCurrentPwdError = errorMessage != null && !isMismatchError

                        // 1. 현재 비밀번호
                        LMEditInputField(
                            label = "현재 비밀번호",
                            value = uiState.currentPassword,
                            onValueChange = onCurrentPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            isPassword = true,
                            isError = isCurrentPwdError,
                            supportingText = if (isCurrentPwdError) errorMessage else null,
                            placeholder = "현재 비밀번호를 입력해주세요"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 2. 새 비밀번호
                        LMEditInputField(
                            label = "새 비밀번호",
                            value = uiState.newPassword,
                            onValueChange = onNewPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            isPassword = true,
                            placeholder = "새 비밀번호를 입력해주세요"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // 3. 새 비밀번호 확인
                        LMEditInputField(
                            label = "새 비밀번호 확인",
                            value = uiState.confirmPassword,
                            onValueChange = onConfirmPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            isPassword = true,
                            isError = isMismatchError,
                            supportingText = if (isMismatchError) errorMessage else null,
                            placeholder = "새 비밀번호를 다시 입력해주세요"
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = onPasswordSave,
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyBlue
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "변경하기",
                                style = AppTypography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // 로그아웃 중 로딩 표시
        if (uiState.isLoggingOut) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, name = "마이페이지 (그룹 있음)")
@Composable
fun MyPageScreenWithGroupPreview() {
    LMTheme {
        val sampleGroup = Group(id = "1", name = "우리 가족 그룹", role = GroupRole.OWNER, relation = "엄마")
        val sampleMembers = listOf(
            GroupMember("id1", "엄마", "엄마", GroupRole.OWNER),
            GroupMember("id2", "아빠", "아빠", GroupRole.MEMBER)
        )
        val sampleBabies = listOf(
            Baby(babyId = "1", babyName = "첫째", birthDate = "2022-01-15", gender = Baby.Gender.MALE, imageUrl = null),
            Baby(babyId = "2", babyName = "둘째", birthDate = "2024-03-20", gender = Baby.Gender.FEMALE, imageUrl = null)
        )
        MypageScreen(
            uiState = MyPageUiState(
                user = User(id = "1", email = "lilly@example.com", nickname = "Lilly"),
                babies = sampleBabies, // 샘플 아이 데이터 연결
                group = sampleGroup,
                groupMembers = sampleMembers,
                hasGroup = true
            ),
            onNavigateToPasswordChange = {},
            onNavigateToProfileEdit = {},
            onNavigateToKidEdit = {},
            onNavigateToKidAdd = {},
            onNavigateToGroupManagement = {},
            onNavigateToGroupJoin = {},
            onNavigateToGroupCreate = {},
            onGroupEditDismiss = {},
            onGroupNameChange = {},
            onGroupSave = {}
        )
    }
}

@Preview(showBackground = true, name = "마이페이지 (그룹 없음)")
@Composable
fun MyPageScreenWithoutGroupPreview() {
    LMTheme {
        MypageScreen(
            uiState = MyPageUiState(
                user = User(id = "1", email = "lilly@example.com", nickname = "Lilly"),
                hasGroup = false
            ),
            onNavigateToPasswordChange = {},
            onNavigateToProfileEdit = {},
            onNavigateToKidEdit = {},
            onNavigateToKidAdd = {},
            onNavigateToGroupManagement = {},
            onNavigateToGroupJoin = {},
            onNavigateToGroupCreate = {},
            onGroupEditDismiss = {},
            onGroupNameChange = {},
            onGroupSave = {}
        )
    }
}
