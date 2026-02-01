package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.feature.mypage.navigation.GroupManageKey
import com.a602.commonproject.feature.mypage.navigation.KidAddKey
import com.a602.commonproject.feature.mypage.navigation.KidEditKey
import com.a602.commonproject.feature.mypage.navigation.ProfileEditKey
import com.a602.commonproject.feature.mypage.viewmodel.MyPageViewModel
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.model.data.Group
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.GroupRole
import com.a602.commonproject.model.data.User
import com.a602.commonproject.navigation.Navigator

@Composable
fun MyPageMainContainer(navigator: Navigator, viewModel: MyPageViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        MypageScreen(
            user = uiState.user,
            babies = uiState.babies,
            group = uiState.group, // 그룹 정보 전달
            groupMembers = uiState.groupMembers,
            onNavigateToProfileEdit = { navigator.navigate(ProfileEditKey) },
            onNavigateToKidEdit = { babyId -> // babyId를 파라미터로 받습니다.
                navigator.navigate(KidEditKey(babyId))
            },
            onNavigateToKidAdd = { navigator.navigate(KidAddKey) },
            onNavigateToGroupManagement = { navigator.navigate(GroupManageKey) }
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
    user: User?,
    babies: List<Baby>,
    group: Group?,
    groupMembers: List<GroupMember>,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToKidEdit: (String) -> Unit,
    onNavigateToKidAdd: () -> Unit,
    onNavigateToGroupManagement: () -> Unit
) {
    // Scaffold는 화면의 기본 구조(상단바, 본문 등)를 잡아주는 유용한 틀입니다.
    Scaffold(
        containerColor = background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize() // 화면을 가득 채우고
                .padding(innerPadding) // 상단바 아래부터 내용이 시작되도록 패딩을 적용하고
                .padding(horizontal = 24.dp), // 앱의 표준에 맞춰 좌우 여백을 24.dp로 설정합니다.
            verticalArrangement = Arrangement.spacedBy(16.dp), // 각 항목 사이의 수직 간격을 16.dp로 줍니다.
            contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp) // 목록 전체의 위, 아래 여백을 줍니다.
        ) {
            if (user != null) {
                item { ProfileInfoCard(nickname = user.nickname, email = user.email, onEditClick = onNavigateToProfileEdit) }
            }

            item {
                KidsInfoCard(
                    babies = babies,
                    onEditClick = onNavigateToKidEdit,
                    onAddClick = onNavigateToKidAdd
                )
            }

            item { GroupSectionHeader(memberCount = groupMembers.size, onEditClick = onNavigateToGroupManagement) }

            items(groupMembers) { member ->
                MemberItem(
                    name = member.nickname,
                    groupName = group?.name ?: "내 그룹", // 그룹 이름 전달
                    role = member.role.name,
                    color = getColorForRole(member.role)
                )
            }
        }
    }
}

/**
 * [정적 프리뷰]
 */
@Preview(showBackground = true, name = "마이페이지 화면 단독 프리뷰")
@Composable
fun MyPageScreenPreview() {
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
            user = User(id = "1", email = "lilly@example.com", nickname = "Lilly"),
            babies = sampleBabies,
            group = sampleGroup, // 프리뷰용 그룹 정보
            groupMembers = sampleMembers,
            onNavigateToProfileEdit = {},
            onNavigateToKidEdit = {},
            onNavigateToKidAdd = {},
            onNavigateToGroupManagement = {}
        )
    }
}
