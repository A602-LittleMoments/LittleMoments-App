package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

// 필요한 디자인 시스템 임포트 유지
import com.a602.commonproject.designsystem.theme.*

// UI의 상태(State)를 기억하고, 값의 변경을 추적하기 위해 필요한 import
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
// 스크롤 가능한 목록(LazyColumn) 및 동적 아이템(items)을 사용하기 위한 import
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

// 다른 모듈(designsystem)에 미리 만들어둔 공용 상단바 컴포넌트를 가져옵니다.
import com.a602.commonproject.designsystem.component.LMTopAppBar
// 다른 모듈(model)에 정의된 데이터 클래스(User, Baby 등)를 가져옵니다.
import com.a602.commonproject.model.data.*

/**
 * 우리 앱 테마(color.kt)에 정의된 색상들 중 하나를 무작위로 반환하는 함수입니다.
 * @return Color 객체를 반환합니다.
 */
fun getRandomColor(): Color {
    val themeColors = listOf(
        main,
        background,
        lightblue,
        color1,
        color2,
        purple1,
        purple2,
        purple3,
        purple5,
    )
    // .random() 함수를 사용해 리스트에서 무작위로 하나의 색상을 선택하여 반환합니다.
    return themeColors.random()
}

/**
 * 마이페이지의 메인 화면 UI를 구성하는 컴포저블 함수입니다.
 *
 * @param user 화면에 표시할 사용자의 정보 (User 데이터 클래스).
 * @param baby 화면에 표시할 아기의 정보 (Baby 데이터 클래스).
 * @param groupMembers 화면에 표시할 그룹 멤버 목록.
 * @param onNavigateToProfileEdit '내 정보 수정' 버튼을 눌렀을 때 실행될 화면 이동 함수.
 * @param onNavigateToKidEdit '아이 정보 수정' 버튼을 눌렀을 때 실행될 화면 이동 함수.
 * @param onNavigateToKidAdd '아이 추가' 버튼을 눌렀을 때 실행될 화면 이동 함수.
 * @param onNavigateToGroupManagement '그룹 관리' 버튼을 눌렀을 때 실행될 화면 이동 함수.
 */
@Composable
fun MyPageScreen(
    //  1. 닉네임 문자열 대신 User와 Baby 객체
    user: User,
    baby: Baby,
    groupMembers: List<GroupMember>, // ✅ [수정] 외부에서 그룹 멤버 리스트를 받도록 변경
    onNavigateToProfileEdit: () -> Unit = {},
    onNavigateToKidEdit: () -> Unit = {},
    onNavigateToKidAdd: () -> Unit = {},
    onNavigateToGroupManagement: () -> Unit = {}
) {
    // Scaffold는 화면의 기본 구조(상단바, 본문 등)를 잡아주는 유용한 틀입니다.
    Scaffold(
        containerColor = background,
        topBar = { LMTopAppBar(title = "마이페이지") }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize() // 화면을 가득 채우고
                .padding(innerPadding) // 상단바 아래부터 내용이 시작되도록 패딩을 적용하고
                .padding(horizontal = 24.dp), // 앱의 표준에 맞춰 좌우 여백을 24.dp로 설정합니다.
            verticalArrangement = Arrangement.spacedBy(16.dp), // 각 항목 사이의 수직 간격을 16.dp로 줍니다.
            contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp) // 목록 전체의 위, 아래 여백을 줍니다.
        ) {
            item { ProfileInfoCard(name = user.nickname, nickname = user.nickname, email = user.email, onEditClick = onNavigateToProfileEdit) }
            item { KidInfoCard(kidName = baby.babyName, birthDate = baby.birthDate, onEditClick = onNavigateToKidEdit, onAddClick = onNavigateToKidAdd) }
            item { GroupSectionHeader(memberCount = groupMembers.size, onEditClick = onNavigateToGroupManagement) }

            // ✅ [수정] 중첩된 items 블록을 제거하고, 올바른 형식의 items 블록 하나만 남깁니다.
            items(groupMembers) { member ->
                val boxColor = remember(member.nickname) { getRandomColor() }
                MemberItem(name = member.nickname, role = member.role.name, color = boxColor)
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
        // ✅ [수정] 테스트용 데이터를 GroupMember 형식에 맞게 생성
        val sampleMembers = listOf(
            GroupMember("id1", "엄마", "엄마", GroupRole.OWNER),
            GroupMember("id2", "아빠", "아빠", GroupRole.MEMBER)
        )
        MyPageScreen(
            user = User(id = "1", email = "lilly@example.com", nickname = "Lilly"),
            baby = Baby(babyId = "1", babyName = "Leo", birthDate = "2023-05-12", gender = Baby.Gender.MALE, imageUrl = null),
            groupMembers = sampleMembers
        )
    }
}

/**
 * [동적 프리뷰 - 전체 흐름 테스트용]
 * 이 피처 내의 모든 화면 이동 흐름을 테스트하기 위한 미리보기입니다.
 * 실제 앱의 내비게이션과 완전히 동일하게 동작하지는 않지만, 개발 단계에서 매우 유용합니다.
 */
@Preview(showBackground = true, name = "2. 마이페이지 전체 흐름(클릭 가능)")
@Composable
fun MyPageFlowPreview() {
    LMTheme {
        MyPageMainContainer()
    }
}

/**
 * [미리보기 전용 내비게이션 로직]
 * 실제 앱에서는 사용되지 않으며, 오직 @Preview 환경에서만 화면 이동을 흉내 내기 위해 만들어진 컨테이너입니다.
 */
@Composable
fun MyPageMainContainer() {
    // '현재 화면이 무엇인지'를 기억하고, 이 값이 바뀌면 화면을 다시 그립니다. (State)
    var currentScreen by remember { mutableStateOf("main") }

    // 수정 화면 등에서 공유할 사용자 및 아기 정보를 기억합니다.
    var currentUser by remember { mutableStateOf(User(id = "1", email = "길동이@example.com", nickname = "길동이")) }
    var currentBaby by remember { mutableStateOf(Baby(babyId = "1", babyName = "튼튼이", birthDate = "2023-05-12", gender = Baby.Gender.MALE, imageUrl = null)) }

    // ✅ [핵심 수정] 컨테이너 내에서 테스트용 그룹 멤버 데이터를 생성하고 관리합니다.
    val sampleMembers = remember {
        listOf(
            GroupMember(userId = "1", nickname = "엄마", relation = "엄마", role = GroupRole.OWNER),
            GroupMember(userId = "2", nickname = "아빠", relation = "아빠", role = GroupRole.MEMBER),
            GroupMember(userId = "3", nickname = "언니", relation = "언니", role = GroupRole.VIEWER),
            GroupMember(userId = "4", nickname = "할머니", relation = "할머니", role = GroupRole.VIEWER)
        )
    }

    when (currentScreen) {
        // currentScreen이 "main"일 경우, MyPageScreen을 보여줍니다.
        "main" -> MyPageScreen(
            user = currentUser,
            baby = currentBaby,
            groupMembers = sampleMembers, // ✅ [수정] 누락되었던 groupMembers 파라미터 전달
            onNavigateToProfileEdit = { currentScreen = "profile_edit" },
            onNavigateToKidEdit = { currentScreen = "kid_edit" },
            onNavigateToKidAdd = { currentScreen = "kid_add" },
            onNavigateToGroupManagement = { currentScreen = "group_manage" }
        )

        // currentScreen이 "profile_edit"일 경우, ProfileEditScreen을 보여줍니다.
        "profile_edit" -> ProfileEditScreen(
            user = currentUser,
            onSaveClick = { updatedUser ->
                currentUser = updatedUser // 수정된 user 정보로 업데이트하고
                currentScreen = "main"      // 다시 메인 화면으로 돌아갑니다.
            },
            onBackClick = { currentScreen = "main" }
        )

        "kid_edit" -> KidEditScreen(
            baby = currentBaby,
            onSaveClick = { updatedBaby ->
                currentBaby = updatedBaby
                currentScreen = "main"
            },
            onBackClick = { currentScreen = "main" }
        )

        "kid_add" -> KidAddScreen(
            onSaveClick = { _ ->
                currentScreen = "main"
            },
            onBackClick = { currentScreen = "main" }
        )

        "group_manage" -> GroupChangeScreen(
            members = sampleMembers, // ✅ [수정] emptyList() 대신 생성된 테스트 데이터를 전달
            onBackClick = { currentScreen = "main" }
        )
    }
}
