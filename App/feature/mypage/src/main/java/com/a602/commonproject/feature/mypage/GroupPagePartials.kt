package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import com.a602.commonproject.designsystem.icon.LMicons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.*

@Composable
fun ManageableMemberItem(
    name: String,
    role: String,
    color: Color, // 💡 마이페이지에서 생성된 랜덤 색상을 그대로 전달받습니다
    isOwner: Boolean = false,
    // 💡 이 줄이 있는지 확인하고, 없다면 추가해주세요!
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = lightbackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 💡 전달받은 색상이 적용된 사각형 박스
                Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(color))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = name, style = AppTypography.bodyMedium)
                        Spacer(modifier = Modifier.width(15.dp))
                    }

                    // 역할별 아이콘 매칭 (이미지 디자인 준수)
                    val (icon, tint) = when(role) {
                        "관리자" -> Icons.Default.EmojiEvents to color1
                        "멤버" -> LMicons.Shield to main
                        else -> Icons.Default.StarBorder to color4
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = role, color = tint, style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            // 권한 변경 버튼 (현서님이 요청하신 파란 상자 스타일)
            if (!isOwner) {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = color3),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    onClick = onEditClick,
                ) {
                    Text(text = "권한 변경", fontSize = 11.sp, color = lightbackground)
                }
            }
        }
    }
}

// 1. 상단 그룹 요약 카드 정의
@Composable
fun GroupSummaryCard(groupName: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = lightbackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 이미지의 하트 아이콘 부분
            Surface(
                modifier = Modifier.size(50.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = background
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = errorRed,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = groupName, style = AppTypography.bodyMedium) // Suite 폰트 적용
                Text(text = description, style = AppTypography.labelSmall, color = color4)
            }
        }
    }
}

// 2. 하단 권한 안내 섹션 정의
@Composable
fun PermissionGuideSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = lightbackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, lightblue)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "권한 안내",
                style = AppTypography.headlineSmall,
                color = color3
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 각 권한별 행 배치
            PermissionRow(icon = Icons.Default.EmojiEvents, title = "관리자", desc = "모든 권한", color = color1)
            PermissionRow(icon = LMicons.Shield, title = "멤버", desc = "편집 및 업로드 가능", color = main)
            PermissionRow(icon = Icons.Default.StarBorder, title = "뷰어", desc = "보기만 가능", color = color4)
        }
    }
}

// 권한 안내용 소형 행 컴포넌트
@Composable
fun PermissionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = AppTypography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color)
        Text(text = " - $desc", style = AppTypography.labelSmall, color = color4)
    }
}
