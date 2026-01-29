package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import kotlin.random.Random as KotlinRandom // 이름을 겹치지 않게 별명(as)을 붙여줍니다.

// 필요한 디자인 시스템 임포트 유지
import com.a602.commonproject.designsystem.component.LMFilledIconButton
import com.a602.commonproject.designsystem.theme.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
// 클릭을 위함
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Person

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
// 만약 item 하나에만 빨간줄이 있다면 이것도 확인하세요
import androidx.compose.foundation.lazy.LazyItemScope
import com.a602.commonproject.designsystem.component.LMTopAppBar
// 샘플 데이터
import com.a602.commonproject.model.data.*

// 1. 랜덤 색상 생성 함수 (데이터베이스 연동 전 시각적 구분을 위함)
fun getRandomColor(): Color {
    // KotlinRandom이라는 별명을 명확하게 사용합니다.
    return Color(
        red = KotlinRandom.nextInt(180, 255),
        green = KotlinRandom.nextInt(180, 255),
        blue = KotlinRandom.nextInt(180, 255),
        alpha = 255
    )
}

// 2. 마이페이지 메인 화면
@Composable
fun MyPageScreen(
    // 💡 1. 닉네임 문자열 대신 User와 Baby 객체를 통째로 받습니다.
    user: User,
    baby: Baby,
    onNavigateToProfileEdit: () -> Unit = {},
    onNavigateToKidEdit: () -> Unit = {},
    onNavigateToGroupManagement: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    // 💡 그룹원 리스트는 나중에 DB 연결 시 Container에서 받아오도록 관리하면 더 좋습니다.
    val groupMembers = listOf(
        Pair("엄마", "관리자"),
        Pair("아빠", "멤버"),
        Pair("언니", "뷰어"),
        Pair("할머니", "뷰어")
    )

    Scaffold(
        containerColor = background,
        topBar = {
            LMTopAppBar(
                title = "마이페이지",
                onNavigationClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp)
        ) {
            // [내 정보 섹션]
            item {
                ProfileInfoCard(
                    // 💡 2. 고정된 이름 대신 전달받은 user의 데이터를 사용합니다.
                    name = user.nickname,
                    nickname = user.nickname,
                    email = user.email,
                    onEditClick = onNavigateToProfileEdit
                )
            }

            // [아이 정보 섹션]
            item {
                KidInfoCard(
                    // 💡 3. 고정된 아이 이름 대신 전달받은 baby의 데이터를 사용합니다.
                    kidName = baby.babyName,
                    birthDate = baby.birthDate,
                    onEditClick = onNavigateToKidEdit,
                    onAddClick = { /* 아이 추가 로직 */ }
                )
            }

            // [그룹원 헤더]
            item {
                GroupSectionHeader(
                    memberCount = groupMembers.size,
                    onEditClick = onNavigateToGroupManagement
                )
            }

            // [그룹원 리스트]
            items(groupMembers) { (name, role) ->
                val boxColor = remember(name) { getRandomColor() }

                MemberItem(
                    name = name,
                    role = role,
                    color = boxColor
                )
            }
        }
    }
}


/*
// 화면 플로우 확인 가능
@Preview(showBackground = true, name = "2. 마이페이지 전체 흐름(클릭 가능)")
@Composable
fun MyPageFlowPreview() {
    MyPageMainContainer()
}

// 화면 이동 구성 로직
// 화면 이동 구성 로직
@Composable
fun MyPageMainContainer() {
    var currentScreen by remember { mutableStateOf("main") }

    var currentUser by remember { mutableStateOf(SampleData.user) }
    var currentBaby by remember { mutableStateOf(SampleData.baby) }

    when (currentScreen) {
        "main" -> MyPageScreen(
            // ✅ 수정: 아래처럼 user와 baby를 통째로 넘겨줘야 합니다!
            user = currentUser,
            baby = currentBaby,
            onNavigateToProfileEdit = { currentScreen = "profile_edit" },
            onNavigateToKidEdit = { currentScreen = "kid_edit" },
            onNavigateToGroupManagement = { currentScreen = "group_manage" },
            onBackClick = { */
/* 이전 메인 화면으로 *//*
 }
        )

        "profile_edit" -> ProfileEditScreen(
            user = currentUser,
            onSaveClick = { updatedUser ->
                currentUser = updatedUser
                currentScreen = "main"
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

        "group_manage" -> GroupChangeScreen(
            members = SampleData.group.members,
            onBackClick = { currentScreen = "main" }
        )
    }
}

*/
