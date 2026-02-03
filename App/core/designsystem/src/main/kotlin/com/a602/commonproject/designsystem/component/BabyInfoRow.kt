package com.a602.commonproject.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color3
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.model.data.Baby

/**
 * 아이의 정보(사진, 이름, 생년월일)를 보여주는 행(Row) 컴포넌트입니다.
 * MyPage와 Home 화면에서 공통으로 사용됩니다.
 *
 * @param baby 표시할 아기 정보
 * @param onEditClick 수정 버튼 클릭 시 실행될 콜백 (babyId 전달)
 */
@Composable
fun BabyInfoRow(
    baby: Baby,
    onEditClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(background)
                    .border(2.dp, color4, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (baby.imageUrl != null) {
                    AsyncImage(
                        model = baby.imageUrl,
                        contentDescription = "baby profile photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("👶", fontSize = 32.sp)
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = baby.babyName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = color4
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = baby.birthDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = color4
                )
            }
        }
        IconButton(onClick = { onEditClick(baby.babyId) }) {
            Icon(imageVector = LMicons.Edit, contentDescription = "수정", tint = color3)
        }
    }
}
