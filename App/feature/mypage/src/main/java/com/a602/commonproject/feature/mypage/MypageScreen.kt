package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.component.LMNavigationDefaults
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.component.GroupRoleChangeDialog
import com.a602.commonproject.designsystem.component.GroupCodeDialog
import com.a602.commonproject.designsystem.icon.LMicons
import androidx.compose.foundation.BorderStroke
import com.a602.commonproject.feature.mypage.navigation.GroupCreateKey
import com.a602.commonproject.feature.mypage.navigation.GroupJoinKey
import com.a602.commonproject.feature.mypage.navigation.GroupManageKey
import com.a602.commonproject.feature.mypage.navigation.KidAddKey
import com.a602.commonproject.feature.mypage.navigation.KidEditKey
import com.a602.commonproject.feature.mypage.navigation.ProfileEditKey
import com.a602.commonproject.feature.login.navigation.LoginNavKey
import com.a602.commonproject.feature.mypage.viewmodel.MyPageUiState
import com.a602.commonproject.feature.mypage.viewmodel.MyPageViewModel
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.model.data.Group
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.GroupRole
import com.a602.commonproject.model.data.User
import com.a602.commonproject.navigation.Navigator

@Composable
fun MyPageMainContainer(navigator: Navigator, viewModel: MyPageViewModel =hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        MypageScreen(
            uiState = uiState,
            onNavigateToProfileEdit = { navigator.navigate(ProfileEditKey) },
            onNavigateToKidEdit = { babyId -> navigator.navigate(KidEditKey(babyId)) },
            onNavigateToKidAdd = { navigator.navigate(KidAddKey) },
            onNavigateToGroupManagement = { viewModel.openGroupEditDialog() },
            onNavigateToGroupJoin = { navigator.navigate(GroupJoinKey) },
            onNavigateToGroupCreate = { navigator.navigate(GroupCreateKey) }, // 그룹 만들기 화면으로 이동
            onLogoutClick = {
                viewModel.logout()
                navigator.replaceRoot(LoginNavKey)
            },
            onGroupEditDismiss = viewModel::closeGroupEditDialog,
            onGroupNameChange = viewModel::onGroupNameChange,
            onGroupSave = viewModel::saveGroupName,
            // 초대 관련 콜백 연결
            onAddNewMemberClick = viewModel::onAddNewMemberClick,
            onRoleSelected = viewModel::onRoleSelected,
            onCloseInviteDialog = viewModel::closeInviteDialogs,
            onRefreshInviteCode = viewModel::refreshInviteCode
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
    onRefreshInviteCode: () -> Unit = {}
) {
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
                .statusBarsPadding()
        ) {
            // 3. 메인 콘텐츠
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = LMNavigationDefaults.NavigationBarHeight + 24.dp)
            ) {
                if (uiState.user != null) {
                    item {
                        ProfileInfoCard(
                            nickname = uiState.user.nickname,
                            email = uiState.user.email,
                            onEditClick = onNavigateToProfileEdit
                        )
                    }
                }

                if (uiState.hasGroup) {
                    item {
                        FamilyCard(
                            groupName = uiState.group?.name ?: "우리 가족",
                            groupMembers = uiState.groupMembers,
                            onEditClick = onNavigateToGroupManagement
                        )
                    }
                } else {
                    item {
                        NoGroupSection(
                            onCreateClick = onNavigateToGroupCreate,
                            onJoinClick = onNavigateToGroupJoin
                        )
                    }
                }

                // 하단 버튼들
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = onLogoutClick,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OffWhite),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = "로그아웃",
                                style = AppTypography.bodyLarge.copy(color = NavyBlue)
                            )
                        }

                        Button(
                            onClick = onAddNewMemberClick, // 새 멤버 추가 클릭 시 팝업 호출
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OffWhite),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = "새 멤버 추가",
                                style = AppTypography.bodyLarge.copy(color = NavyBlue)
                            )
                        }
                    }
                }
            }
        }

        // 그룹 이름 수정 다이얼로그
        if (uiState.showGroupEditDialog) {
            AlertDialog(
                onDismissRequest = onGroupEditDismiss,
                title = { Text(text = "가족 이름 수정", style = AppTypography.headlineMedium) },
                text = {
                    Column {
                        Text(text = "새로운 가족 이름을 입력해주세요.", style = AppTypography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = uiState.editingGroupName,
                            onValueChange = onGroupNameChange,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NavyBlue,
                                unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f)
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = onGroupSave) {
                        Text("저장", color = NavyBlue)
                    }
                },
                dismissButton = {
                    TextButton(onClick = onGroupEditDismiss) {
                        Text("취소", color = Color.Gray)
                    }
                },
                containerColor = OffWhite,
                shape = RoundedCornerShape(16.dp)
            )
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
