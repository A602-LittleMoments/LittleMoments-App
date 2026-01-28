package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.StarBorder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.component.LMFilledIconButton

import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.theme.AppTypography
import androidx.compose.foundation.clickable

// 마이페이지 화면에서 사용하는 컴포넌트들 모음
// 그룹 구성원 개별 컴포넌트
@Composable
fun MemberItem(name: String, role: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // 상세 페이지와 동일한 24.dp 곡률
        colors = CardDefaults.cardColors(containerColor = lightbackground), // 0xFFFFFEFB
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 상세 페이지와 동일한 크기의 컬러 박스
            Box(
                modifier = Modifier
                    .size(50.dp) // 40dp에서 50dp로 키워 상세 페이지와 맞춤
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 2. 이름 및 가족 태그
                    Text(text = name, style = AppTypography.bodyMedium)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFFF5F5F5), // 상세 페이지와 동일한 연회색 태그
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "가족",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = color4
                        )
                    }
                }

                // 3. 역할별 아이콘 및 텍스트 로직 (상세 페이지와 동일)
                val (icon, tint) = when(role) {
                    "관리자" -> Icons.Default.EmojiEvents to color1
                    "멤버" -> Icons.Default.Shield to main
                    else -> Icons.Default.StarBorder to color4
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = role,
                        color = tint,
                        style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

// --- [분리된 함수: 그룹 섹션 헤더] ---
@Composable
fun GroupSectionHeader(memberCount: Int, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 1. '그룹원' 텍스트
            Text(
                text = "그룹원",
                style = AppTypography.headlineSmall,
                color = color3 // 0xFF6D625E
            )
            Spacer(modifier = Modifier.width(8.dp))

            // 2. 인원수 배지 (상세 페이지와 동일)
            Surface(
                color = lightblue, // 0xFFEBF0FF
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${memberCount}명",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = main // 0xFF6ca0ff
                )
            }
        }

        // 3. 통일된 파란색 상자 수정 버튼
        Surface(
            onClick = onEditClick,
            color = main, // 0xFF6ca0ff
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "수정",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = lightbackground, // 0xFFFFFEFB
                fontSize = 10.sp,
                style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}

// --- ProfileInfoCard와 KidInfoCard 컴포넌트
@Composable
fun ProfileInfoCard(
    // 💡 고정된 기본값을 삭제하여, 반드시 상위에서 데이터를 넘겨주도록 설정합니다.
    name: String,      // user.username (실명)를 받습니다.
    nickname: String,  // user.nickname (별명)을 받습니다.
    email: String,     // user.email (이메일)을 받습니다.
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = lightbackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 1. 헤더: 내 정보 라벨 & 수정 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "내 정보",
                    style = AppTypography.labelMedium,
                    color = color4.copy(alpha = 0.6f)
                )

                // 수정 버튼 (메인 컬러 상자 스타일)
                Surface(
                    onClick = onEditClick,
                    color = main,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "수정",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = lightbackground,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. 텍스트 정보 레이아웃
            // 상위에서 받은 name, nickname, email 변수가 여기에 꽂힙니다.
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoRow(label = "이름", value = name)
                InfoRow(label = "닉네임", value = nickname)
                InfoRow(label = "이메일", value = email)
            }
        }
    }
}

// 정보 표시용 소형 한 줄 부품
@Composable
fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = color4,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium,
            color = color3 // 0xFF6D625E
        )
    }
}

@Composable
fun KidInfoCard(
    // 💡 1. 기본값을 삭제하고 상위(MyPageScreen)에서 데이터를 직접 받도록 수정합니다.
    kidName: String,   // baby.babyName을 받습니다.
    birthDate: String, // baby.birthDate를 받습니다.
    imageUri: android.net.Uri? = null,
    onEditClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = lightbackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 1. 상단 라벨 및 수정 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "아이 정보", style = MaterialTheme.typography.labelMedium, color = color4)

                // 수정 버튼 (메인 파란색 테마)
                Surface(
                    onClick = onEditClick,
                    color = main,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "수정",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = lightbackground,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. 사진 + 이름/생년월일 배치
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 아이 사진 영역 (현재는 기본 이모지로 설정)
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(background)
                        .border(2.dp, color4, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        // 실제 사진이 연동될 때 보여주는 로직 (AsyncImage 등 사용 가능)
                    } else {
                        Text("👶", fontSize = 32.sp)
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // 이름 및 생년월일 정보
                // 💡 2. 전달받은 kidName과 birthDate 변수를 여기에 꽂아줍니다.
                Column {
                    Text(
                        text = kidName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = color4
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = birthDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = color4,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. 중앙 하단 추가 버튼 (LMFilledIconButton 사용)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LMFilledIconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "추가",
                        tint = lightbackground
                    )
                }
            }
        }
    }
}
