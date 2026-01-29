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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// 우리 앱의 디자인 시스템(색상, 테마 등)을 가져옵니다.
import com.a602.commonproject.designsystem.theme.*
// User 데이터가 어떤 모양인지 알려주기 위해 데이터 모델을 가져옵니다.
import com.a602.commonproject.model.data.User

/**
 * '내 정보 확인' 화면 전체를 담당하는 메인 컴포저블입니다.
 * 이 화면은 데이터를 보여주기만 하고, 직접 수정하지는 않습니다.
 *
 * @param user 화면에 표시할 사용자의 정보. 밖(ViewModel 등)에서 전달받습니다.
 * @param onEditClick "수정하기" 버튼을 눌렀을 때 실행될 화면 이동 함수입니다.
 */
@Composable
fun ProfileDetailScreen(
    user: User,
    onEditClick: () -> Unit = {}
) {
    // Scaffold는 화면의 기본 구조(상단바, 본문 등)를 잡아주는 유용한 틀입니다.
    Scaffold(
        // 화면 전체의 배경색을 우리 디자인 시스템에 정의된 background 색상으로 설정합니다.
        containerColor = background
    ) { innerPadding -> // Scaffold가 상단바 등의 영역을 제외한 '본문' 영역의 패딩 값을 알려줍니다.

        // UI 요소들을 세로로 배치하기 위해 Column을 사용합니다.
        Column(
            modifier = Modifier
                .fillMaxSize() // 화면을 가득 채우고,
                .padding(innerPadding) // 상단바가 있다면 그 아래부터 내용이 시작되도록 하고,
                .padding(horizontal = 24.dp), // 앱 표준에 맞춰 좌우 여백을 24.dp로 설정합니다.
            horizontalAlignment = Alignment.CenterHorizontally // 자식 요소들을 수평 중앙 정렬합니다.
        ) {
            // 상단 여백 (실제 앱에서는 상단바 높이에 따라 조절될 수 있습니다.)
            Spacer(modifier = Modifier.height(150.dp))

            // 밖에서 전달받은 user 객체의 데이터를 사용하여 정보 필드를 하나씩 그립니다.
            // 재사용 가능한 InfoDisplayField 컴포넌트를 호출합니다.
            InfoDisplayField(label = "nickname", value = user.nickname, icon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(16.dp)) // 필드 사이의 간격

            InfoDisplayField(label = "email", value = user.email, icon = Icons.Default.Email)
            Spacer(modifier = Modifier.height(16.dp))

            // 비밀번호는 실제 값을 보여주지 않고, 보안을 위해 점(•)으로 마스킹 처리하여 보여줍니다.
            InfoDisplayField(label = "password", value = "••••••••", icon = Icons.Default.Lock)

            // 버튼과 정보 필드 사이의 공간을 확보합니다.
            Spacer(modifier = Modifier.height(80.dp))

            // "수정하기" 버튼입니다.
            Button(
                onClick = onEditClick, // 버튼이 클릭되면, 밖에서 전달받은 onEditClick 함수를 실행합니다.
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = main) // 버튼 배경색을 메인 색상으로 지정
            ) {
                Text(
                    text = "수정하기",
                    style = AppTypography.labelLarge,
                    color = lightbackground
                )
            }

            // 화면 하단의 작은 안내 문구입니다.
            Text(
                text = "소중한 정보를 안전하게 보관해요",
                style = AppTypography.labelSmall, // 가장 작은 폰트 스타일 적용
                color = color4,
                modifier = Modifier.padding(top = 12.dp, bottom = 32.dp)
            )
        }
    }
}

/**
 * "라벨: 값" 형태의 정보 필드 한 줄을 그리는 재사용 가능한 컴포저블입니다.
 *
 * @param label 정보의 종류 (예: "nickname", "email").
 * @param value 실제 표시될 정보의 값.
 * @param icon 정보 오른쪽에 표시될 아이콘.
 */
@Composable
fun InfoDisplayField(label: String, value: String, icon: ImageVector) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Card를 사용해 테두리와 배경이 있는 UI를 만듭니다.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = lightbackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // 그림자 없음
            border = BorderStroke(1.dp, color4) // color4 색상의 1.dp 두께 테두리
        ) {
            // 카드 안의 내용물을 가로로 배치합니다. (텍스트 | 아이콘)
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // 양쪽 끝으로 정렬
            ) {
                // 왼쪽 텍스트 영역 (라벨 + 값)
                Column {
                    // "nickname", "email" 등의 라벨 텍스트
                    Text(
                        text = label,
                        style = AppTypography.labelMedium,
                        color = color4.copy(alpha = 0.6f) // 기존 색상을 약간 투명하게
                    )
                    // 실제 값을 보여주는 텍스트
                    Text(
                        text = value,
                        style = AppTypography.titleMedium, // 제목 중간 크기 스타일
                        color = color4,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                // 오른쪽 아이콘 영역
                Icon(
                    imageVector = icon,
                    contentDescription = null, // 장식용 아이콘
                    tint = color4.copy(alpha = 0.4f), // 아이콘도 약간 투명하게
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "내 정보 확인 미리보기")
@Composable
fun ProfileDetailPreview() {
    LMTheme {
        ProfileDetailScreen(
            user = User(id = "1", email = "lilly@example.com", nickname = "Lilly"),
            onEditClick = {}
        )
    }
}
