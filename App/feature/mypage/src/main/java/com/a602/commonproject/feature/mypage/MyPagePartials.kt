package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.LMFilledIconButton
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.component.BabyInfoRow
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.model.data.GroupRole

// 마이페이지 화면에서 사용하는 재사용 가능한 UI 컴포넌트들을 모아놓은 파일

/**
 * 그룹 구성원 한 명의 정보를 보여주는 카드 형태의 컴포저블입니다.
 *
 * @param name 멤버의 이름.
 * @param role 멤버의 역할 (예: "관리자", "멤버").
 * @param color 멤버를 대표하는 색상 (프로필 이미지 대신 사용).
 */
@Composable
fun MemberItem(name: String, role: String, icon: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘 (망원경, 카메라 등)
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        // 이름
        Text(
            text = name,
            style = AppTypography.headlineLarge.copy(color = NavyBlue, fontSize = 24.sp),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )

        // 역할
        Text(
            text = role,
            style = AppTypography.bodyMedium.copy(color = NavyBlue.copy(alpha = 0.6f)),
            modifier = Modifier.padding(end = 12.dp)
        )
    }

}

/**
 * "그룹원" 텍스트와 멤버 수, "수정" 버튼을 포함하는 섹션 헤더입니다.
 *
 * @param memberCount 그룹 멤버의 총 수.
 * @param onEditClick "수정" 버튼을 눌렀을 때 실행될 함수.
 */
@Composable
fun FamilyCard(
    groupName: String,
    groupMembers: List<com.a602.commonproject.model.data.GroupMember>,
    onEditClick: () -> Unit,
    onAddNewMemberClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // 헤더: "우리 가족"과 수정 버튼
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(IntrinsicSize.Max),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = groupName,
                        style = AppTypography.headlineLarge.copy(
                            color = NavyBlue,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    // 밑줄
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(NavyBlue)
                    )
                }

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = LMicons.Edit,
                        contentDescription = "수정",
                        tint = NavyBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 멤버 리스트
            groupMembers.forEach { member ->
                val iconRes = if (member.role == GroupRole.VIEWER) {
                    com.a602.commonproject.designsystem.R.drawable.telescope
                } else {
                    com.a602.commonproject.designsystem.R.drawable.camera
                }

                MemberItem(
                    name = member.nickname,
                    role = member.relation, // role 대신 relation 사용 (아빠, 할아버지 등)
                    icon = iconRes
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddNewMemberClick, // 새 멤버 추가 클릭 시 팝업 호출
                modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = "새 멤버 추가",
                    style = AppTypography.bodyLarge.copy(color = background)
                )
            }
        }
    }
}

@Composable
fun NoGroupSection(onCreateClick: () -> Unit, onJoinClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Groups,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = NavyBlue.copy(alpha = 0.6f)
        )
        Text(
            text = "아직 참여중인 그룹이 없어요.\n가족과 함께 아이의 성장을 기록해보세요.",
            style = AppTypography.bodyLarge,
            color = NavyBlue,
            textAlign = TextAlign.Center
        )

        // 버튼 영역
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            FilledButton(
                text = "그룹 만들기",
                onClick = onCreateClick,
                size = ButtonSize.Small
            )
            FilledButton(
                onClick = onJoinClick,
                text = "그룹 참여하기",
                size = ButtonSize.Small
            )

        }
    }
}

/**
 * 사용자의 프로필 정보(이름, 닉네임, 이메일)를 보여주는 카드입니다.
 *
 * @param name 사용자의 실명.
 * @param nickname 사용자의 닉네임.
 * @param email 사용자의 이메일.
 * @param onEditClick "수정" 버튼 클릭 시 실행될 함수.
 */
@Composable
fun ProfileInfoCard(
    nickname: String,
    email: String,
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = nickname,
                    style = AppTypography.displayLarge.copy(
                        color = NavyBlue,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = email,
                    style = AppTypography.bodyLarge.copy(
                        color = NavyBlue.copy(alpha = 0.6f),
                        fontSize = 18.sp
                    )
                )
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = LMicons.Edit,
                    contentDescription = "수정",
                    tint = NavyBlue,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * "라벨: 값" 형태의 텍스트 한 줄을 표시하기 위한 작은 재사용 컴포넌트입니다.
 */
@Composable
fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = color4,
            modifier = Modifier.width(60.dp) // 라벨의 너비를 고정하여 콜론(:) 위치를 맞추는 효과
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium,
            color = color3
        )
    }
}

/**
 * 아이의 정보(사진, 이름, 생년월일)와 추가 버튼을 보여주는 카드입니다.
 *
 * @param onEditClick "수정" 버튼 클릭 시 실행될 함수.
 * @param onAddClick "+" 버튼 클릭 시 실행될 함수.
 */
@Composable
fun KidsInfoCard(
    babies: List<Baby>,
    onEditClick: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = lightbackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "아이 정보",
                style = MaterialTheme.typography.labelMedium,
                color = color4,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            if (babies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp), // 위아래로 넉넉한 여백
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "등록된 아이가 없어요.\n아래 버튼을 눌러 아이를 추가해주세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = color4,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    babies.forEach { baby ->
                        BabyInfoRow(
                            baby = baby,
                            onEditClick = onEditClick // 콜백을 그대로 전달
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. 하단 중앙의 아이 추가 버튼
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // designsystem에 미리 만들어둔 버튼 컴포넌트를 사용합니다.
                LMFilledIconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = LMicons.Add,
                        contentDescription = "추가",
                        tint = lightbackground
                    )
                }
            }
        }
    }
}


