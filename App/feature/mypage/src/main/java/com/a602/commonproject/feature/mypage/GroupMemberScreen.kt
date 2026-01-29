package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.*
// 💡 Member 모델을 인식하기 위해 추가
import com.a602.commonproject.model.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupManagementScreen(
    // 💡 밖에서 실제 가족 명단을 받을 수 있게 'members' 파라미터를 추가했습니다.
    members: List<GroupMember>,
    onBackClick: () -> Unit = {},
    onRoleEditClick: (String) -> Unit = {}
) {
    Scaffold(
        containerColor = background,
        topBar = {
            LMTopAppBar(title = "그룹 구성원 관리", onNavigationClick = onBackClick)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            item {
                GroupSummaryCard(groupName = "우리 가족 그룹", description = "함께 추억을 공유해요")
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "그룹원", style = AppTypography.labelMedium, color = color3)
                    Spacer(modifier = Modifier.width(5.dp))
                    Surface(color = lightblue, shape = RoundedCornerShape(12.dp)) {
                        // 💡 실제 넘겨받은 members의 개수를 보여줍니다.
                        Text(
                            text = "${members.size}명",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = main
                        )
                    }
                }
            }

            // 💡 실제 넘겨받은 'members' 리스트로 아이템을 생성합니다.
            items(members) { member -> // 'GroupMember'가 아닌 개별 변수 'member'를 사용합니다.
                val memberColor = remember(member.nickname) { getRandomColor() }

                ManageableMemberItem(
                    name = member.nickname, // 💡 name 대신 nickname 사용
                    role = member.role.name, // 💡 role(객체) 대신 role.name(문자열) 사용
                    color = memberColor,
                    // 💡 OWNER 권한인지 체크
                    isOwner = (member.role == GroupRole.OWNER),
                    onEditClick = { onRoleEditClick(member.nickname) }
                )
            }

            item {
                Button(
                    onClick = { /* 초대 로직 */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
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

/*@Preview(showBackground = true, name = "그룹 구성원 관리 메인", widthDp = 360, heightDp = 800)
@Composable
fun GroupManagementPreview() {
    NiaTheme {
        // 💡 4. 프리뷰에서도 샘플 데이터를 넣어줘야 빨간 줄이 안 생깁니다.
        GroupManagementScreen(
            members = SampleData.group.members,
            onBackClick = { }
        )
    }
}*/
