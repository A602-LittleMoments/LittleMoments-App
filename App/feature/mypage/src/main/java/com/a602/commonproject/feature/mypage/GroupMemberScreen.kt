package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.a602.commonproject.designsystem.component.GroupCodeDialog
import com.a602.commonproject.designsystem.component.GroupRoleChangeDialog
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color1
import com.a602.commonproject.designsystem.theme.color2
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.gray1
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.mypage.viewmodel.GroupMemberViewModel
import com.a602.commonproject.model.data.GroupMember
import com.a602.commonproject.model.data.GroupRole
import com.a602.commonproject.navigation.Navigator

@Composable
fun GroupManageContainer(
    navigator: Navigator,
    viewModel: GroupMemberViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.showInviteDialog) {
        uiState.inviteCode?.let { invite ->
            GroupCodeDialog(
                initialCode = invite.codeMember,
                initialSeconds = invite.expiredAt.toIntOrNull() ?: 180,
                onRefreshClick = {
                    viewModel.onAddMemberClicked()
                },
                onDismissRequest = viewModel::onInviteDialogDismissed
            )
        }
    }

    uiState.memberToEdit?.let { member ->
        GroupRoleChangeDialog(
            currentRole = member.role.name, // Pass the role name as a String
            onConfirm = { isMemberSelected ->
                // Convert the boolean result back to a GroupRole
                val newRole = if (isMemberSelected) GroupRole.MEMBER else GroupRole.VIEWER
                viewModel.updateMemberRole(member.userId, newRole)
            },
            // The Dialog has two ways to be dismissed
            onCloseClick = viewModel::onRoleEditDialogDismissed,
            onDismiss = viewModel::onRoleEditDialogDismissed
        )
    }

    GroupMemberScreen(
        members = uiState.members,
        onBackClick = { navigator.goBack() },
        onRoleEditClick = { memberId ->
            uiState.members.find { it.userId == memberId }?.let {
                viewModel.onRoleEditClicked(it)
            }
        },
        onAddMemberClick = viewModel::onAddMemberClicked
    )
}

@Composable
fun GroupMemberScreen(
    // 이 화면을 그리기 위해 필요한 '그룹 멤버 목록' 데이터입니다.
    // List<GroupMember> 형태로, 밖(ViewModel이나 상위 컴포저블)에서 전달받습니다.
    members: List<GroupMember>,
    onBackClick: () -> Unit,
    onRoleEditClick: (String) -> Unit,
    onAddMemberClick: () -> Unit
) {
    // Scaffold는 Material Design의 기본적인 화면 레이아웃(상단바, 본문, 하단 버튼 등)을 제공하는 틀입니다.
    Scaffold(
        containerColor = background,
        topBar = {
            // 우리가 designsystem 모듈에 미리 만들어 둔 LMTopAppBar를 사용합니다.
            // 제목은 "그룹 구성원 관리"로, 뒤로가기 버튼 클릭 시에는 onBackClick 함수를 실행하도록 연결합니다.
            LMTopAppBar(title = "그룹 구성원 관리", onNavigationClick = onBackClick)
        }
    ) { innerPadding -> // Scaffold가 상단바 등의 영역을 제외한 '본문' 영역의 패딩 값을 알려줍니다.

        // LazyColumn은 화면에 보이는 부분만 렌더링하여 긴 목록을 효율적으로 보여주는 스크롤 가능한 컴포넌트입니다.
        LazyColumn(
            // Modifier를 사용해 UI 요소를 꾸미거나 속성을 지정합니다.
            // .fillMaxSize(): 화면 전체를 채웁니다.
            // .padding(innerPadding): Scaffold가 알려준 본문 영역에 맞게 패딩을 적용합니다.
            // .padding(horizontal = 20.dp): 좌우에 24dp의 추가 여백을 줍니다.
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),

            // 항목들 사이의 수직 간격을 12.dp로 설정합니다.
            verticalArrangement = Arrangement.spacedBy(12.dp),

            // 전체 목록의 위쪽과 아래쪽에 각각 20dp, 40dp의 여백을 줍니다.
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            // 'item'은 LazyColumn 안에서 하나의 고정된 항목
            item {
                // 그룹의 이름과 설명을 보여주는 카드 UI 컴포넌트입니다. (별도 파일에 정의됨)
                GroupSummaryCard(groupName = "우리 가족 그룹", description = "함께 추억을 공유해요")
                // 카드 아래에 12.dp 만큼의 수직 공간을 만듭니다.
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                // Row를 사용해 UI 요소들을 가로로 나란히 배치합니다.
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "그룹원", style = AppTypography.labelMedium, color = color3)
                    Spacer(modifier = Modifier.width(5.dp))
                    Surface(color = lightblue, shape = RoundedCornerShape(12.dp)) {
                        // 밖에서 전달받은 'members' 리스트의 크기(개수)를 사용해 "N명" 텍스트를 보여줍니다.
                        Text(
                            text = "${members.size}명",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = main,
                        )
                    }
                }
            }

            // 'items'는 LazyColumn 안에서 동적인 목록을 표시하는 데 사용됩니다.
            // 밖에서 전달받은 'members' 리스트의 각 항목('member')에 대해 아래 코드를 반복 실행합니다.
            items(members) { member ->
                // 관리 가능한 멤버 한 명을 표시하는 UI 컴포넌트입니다. (별도 파일에 정의됨)
                ManageableMemberItem(
                    // GroupMember 객체에서 'nickname' 속성값을 가져와 이름으로 전달합니다.
                    name = member.nickname,
                    // GroupMember 객체의 'role' 속성(enum)에서 '.name'으로 실제 이름("OWNER" 등)을 문자열로 가져옵니다.
                    role = member.role.name,
                    color = getColorForRole(member.role),
                    isOwner = (member.role == GroupRole.OWNER),
                    onEditClick = { onRoleEditClick(member.userId) }
                )
            }

            item {
                Button(
                    // 💡 onClick 부분을 전달받은 onAddMemberClick으로 바꿉니다.
                    onClick = onAddMemberClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = main)
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "새 구성원 추가하기", style = AppTypography.labelLarge)
                }
            }
        }
    }
}

private fun getColorForRole(role: GroupRole): Color {
    return when (role) {
        GroupRole.OWNER -> main
        GroupRole.MEMBER -> color1
        GroupRole.VIEWER -> color2
        else -> gray1
    }
}

@Preview(showBackground = true, name = "그룹 구성원 관리 메인", widthDp = 360, heightDp = 800)
@Composable
fun GroupMemberScreenPreview() {
    LMTheme {
        // 💡 프리뷰에서 사용할 샘플 데이터를 직접 생성합니다.
        val sampleMembers = listOf(
            GroupMember(userId = "1", nickname = "엄마", relation = "엄마", role = GroupRole.OWNER),
            GroupMember(userId = "2", nickname = "아빠", relation = "아빠", role = GroupRole.MEMBER),
            GroupMember(userId = "3", nickname = "언니", relation = "언니", role = GroupRole.VIEWER),
            GroupMember(userId = "4", nickname = "할머니", relation = "할머니", role = GroupRole.VIEWER)
        )
        GroupMemberScreen(
            members = sampleMembers,
            onBackClick = {},
            onRoleEditClick = {},
            onAddMemberClick = {}
        )
    }
}
