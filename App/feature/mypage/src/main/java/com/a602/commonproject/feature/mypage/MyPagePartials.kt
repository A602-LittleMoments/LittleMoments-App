package com.a602.commonproject.feature.mypage

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.component.LMFilledIconButton

import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.theme.AppTypography

// 마이페이지 화면에서 사용하는 재사용 가능한 UI 컴포넌트들을 모아놓은 파일

/**
 * 그룹 구성원 한 명의 정보를 보여주는 카드 형태의 컴포저블입니다.
 *
 * @param name 멤버의 이름.
 * @param role 멤버의 역할 (예: "관리자", "멤버").
 * @param color 멤버를 대표하는 색상 (프로필 이미지 대신 사용).
 */
@Composable
fun MemberItem(name: String, role: String, color: Color) {
    // Card를 사용해 그림자 효과와 둥근 모서리를 적용합니다.
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // 모서리 곡률
        colors = CardDefaults.cardColors(containerColor = lightbackground), // 카드 배경색
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // 그림자 깊이
    ) {
        // UI 요소들을 가로로 배치하기 위해 Row를 사용합니다.
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // 자식 요소들을 수직 중앙 정렬.
        ) {
            // 1. 멤버의 고유 색상을 보여주는 네모 상자
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp)) // 모서리
                    .background(color) // 전달받은 색상으로 배경을 칠합니다.
            )

            Spacer(modifier = Modifier.width(16.dp)) // 색상 상자와 텍스트 사이의 간격

            // 이름과 역할을 세로로 배치하기 위해 Column을 사용합니다.
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 2. 멤버의 이름과 "가족" 태그
                    Text(text = name, style = AppTypography.bodyMedium)
                    Spacer(modifier = Modifier.width(6.dp))
                    // Surface는 배경색과 모양을 지정할 수 있는 UI의 기본 판입니다. 태그 모양을 만드는 데 사용됩니다.
                    Surface(
                        color = gray1,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "가족",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = AppTypography.labelSmall,
                            color = color4
                        )
                    }
                }

                // 3. 멤버의 역할("관리자", "멤버" 등)에 따라 다른 아이콘과 색상을 표시하는 로직
                val (icon, tint) = when(role) {
                    "관리자" -> Icons.Default.EmojiEvents to color1 // 관리자는 왕관 아이콘과 노란색
                    "멤버" -> Icons.Default.Shield to main       // 멤버는 방패 아이콘과 메인 색상
                    else -> Icons.Default.StarBorder to color4   // 그 외는 별 아이콘과 회색
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon, // 위에서 결정된 아이콘
                        contentDescription = null, // 장식용 아이콘이므로 설명은 null
                        tint = tint, // 위에서 결정된 색상
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = role,
                        color = tint,
                        style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold) // 기본 스타일에 굵기만 추가
                    )
                }
            }
        }
    }
}

/**
 * "그룹원" 텍스트와 멤버 수, "수정" 버튼을 포함하는 섹션 헤더입니다.
 *
 * @param memberCount 그룹 멤버의 총 수.
 * @param onEditClick "수정" 버튼을 눌렀을 때 실행될 함수.
 */
@Composable
fun GroupSectionHeader(memberCount: Int, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // 자식 요소들을 양쪽 끝으로 밀어냅니다.
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽 부분 (그룹원 텍스트 + 인원수)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "그룹원",
                style = AppTypography.headlineSmall, // 디자인 시스템의 작은 제목 스타일
                color = color3
            )
            Spacer(modifier = Modifier.width(8.dp))

            // 인원수를 보여주는 둥근 사각형 배지
            Surface(
                color = lightblue, // 디자인 시스템의 밝은 파란색
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${memberCount}명",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = main
                )
            }
        }

        // 오른쪽 부분 (수정 버튼)
        // Surface에 onClick을 지정하여 버튼처럼 사용합니다.
        Surface(
            onClick = onEditClick,
            color = main, // 메인 색상을 배경으로 사용
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "수정",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = lightbackground,
                style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Medium)
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
    name: String,
    nickname: String,
    email: String,
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = lightbackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 1. 헤더: "내 정보" 라벨과 "수정" 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // 양쪽 끝으로 정렬
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "내 정보",
                    style = AppTypography.labelMedium,
                    color = color4.copy(alpha = 0.6f) // 기존 색상을 약간 투명하게 만듦
                )

                Surface(
                    onClick = onEditClick,
                    color = main,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "수정",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = lightbackground,
                        style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. 실제 정보 (이름, 닉네임, 이메일)
            // 재사용 가능한 InfoRow 컴포넌트를 사용하여 정보를 표시합니다.
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoRow(label = "이름", value = name)
                InfoRow(label = "닉네임", value = nickname)
                InfoRow(label = "이메일", value = email)
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
 * @param kidName 아이의 이름.
 * @param birthDate 아이의 생년월일.
 * @param imageUri 아이의 프로필 사진 URI (없을 경우 기본 이미지 표시).
 * @param onEditClick "수정" 버튼 클릭 시 실행될 함수.
 * @param onAddClick "+" 버튼 클릭 시 실행될 함수.
 */
@Composable
fun KidInfoCard(
    kidName: String,
    birthDate: String,
    imageUri: android.net.Uri? = null,
    onEditClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = lightbackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 1. 헤더: "아이 정보" 라벨과 "수정" 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "아이 정보", style = MaterialTheme.typography.labelMedium, color = color4)

                Surface(
                    onClick = onEditClick,
                    color = main,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "수정",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = lightbackground,
                        style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. 본문: 아이 사진과 텍스트 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 아이 사진을 보여주는 동그란 영역
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape) // 원 모양으로 자름
                        .background(background)
                        .border(2.dp, color4, CircleShape), // 원 모양 테두리
                    contentAlignment = Alignment.Center // 내용물(이모지)을 중앙에 배치
                ) {
                    // imageUri가 있으면 실제 사진을, 없으면 기본 이모지를 보여줍니다.
                    if (imageUri != null) {
                        // TODO: Coil이나 Glide 같은 이미지 로딩 라이브러리를 사용해 사진을 표시합니다.
                    } else {
                        Text("👶", fontSize = 32.sp) // 사진이 없을 때 보여줄 기본 이모지
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // 이름과 생년월일 텍스트
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
                        letterSpacing = 0.5.sp // 자간을 약간 넓혀 가독성 향상
                    )
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
                        imageVector = Icons.Default.Add,
                        contentDescription = "추가",
                        tint = lightbackground
                    )
                }
            }
        }
    }
}

@Composable
fun KidEditInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {Text(label, color = color4.copy(alpha = 0.6f), style = AppTypography.labelMedium) },
        placeholder = { Text(placeholder, color = color4.copy(alpha = 0.3f)) },
        // ✅ [수정] 사용자가 입력하는 글씨 스타일을 titleMedium으로 지정
        textStyle = AppTypography.titleMedium.copy(color = color4),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        trailingIcon = {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = color4.copy(alpha = 0.4f))
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = lightbackground,
            unfocusedContainerColor = lightbackground,
            focusedBorderColor = main,
            unfocusedBorderColor = lightblue
        )
    )
}

@Preview
@Composable
fun KidEditInputField() {
    LMTheme {
        KidEditInputField(label = "이름", value = "튼튼이", onValueChange = {})
    }
}
