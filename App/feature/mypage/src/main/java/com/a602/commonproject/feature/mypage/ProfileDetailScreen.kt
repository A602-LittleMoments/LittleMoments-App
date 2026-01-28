package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.lightbackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    onEditClick: () -> Unit = {} // 💡 수정 화면으로 이동하기 위한 콜백 추가
) {
    Scaffold(
        containerColor = background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 여백 (통합 시 상단바 공간 고려)
            Spacer(modifier = Modifier.height(150.dp))

            // 정보 필드 영역
            InfoDisplayField(label = "name", value = "홍길동", icon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(16.dp))

            InfoDisplayField(label = "nickname", value = "길동이", icon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(16.dp))

            InfoDisplayField(label = "email", value = "abc@naver.com", icon = Icons.Default.Email)
            Spacer(modifier = Modifier.height(16.dp))

            // 💡 새로 추가된 비밀번호란 (확인창이므로 마스킹 처리된 텍스트 표시)
            InfoDisplayField(label = "password", value = "••••••••", icon = Icons.Default.Lock)

            // 버튼을 아래로 밀기 위한 유연한 여백
            Spacer(modifier = Modifier.height(80.dp))

            // 수정하기 버튼
            Button(
                onClick = onEditClick, // 💡 이제 클릭 시 외부에서 정의한 이동 로직이 실행됩니다.
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = main)
            ) {
                Text(
                    text = "수정하기",
                    style = MaterialTheme.typography.labelLarge,
                    color = lightbackground // 가독성을 위해 흰색 권장
                )
            }

            Text(
                text = "소중한 정보를 안전하게 보관해요",
                style = MaterialTheme.typography.labelMedium,
                color = color4,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 32.dp)
            )
        }
    }
}

@Composable
fun InfoDisplayField(label: String, value: String, icon: ImageVector) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = lightbackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, color4)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = color4.copy(alpha = 0.6f) // 라벨은 살짝 연하게
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 18.sp,
                        color = color4,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color4.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

//@Preview(showBackground = true, name = "내 정보 확인 미리보기")
//@Composable
//fun ProfileDetailPreview() {
//    ProfileDetailScreen()
//}
