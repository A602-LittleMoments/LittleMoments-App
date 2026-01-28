package com.a602.commonproject.feature.mypage

import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.* // 공통 컴포넌트 임포트
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.icon.LMicons
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidAddScreen( // 이름을 Add(추가)로 변경합니다.
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {} // 저장 완료 후 메인으로!
) {
    // 1. 입력을 위한 상태 변수들 (비어있는 상태로 시작)
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedGender by remember { mutableStateOf<Gender?>(Gender.Male) }

    // 사진 선택기 (주석 해제해서 사용하세요!)
//    val pickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickVisualMedia()
//    ) { uri -> selectedUri = uri }

    Scaffold(
        containerColor = background,
        topBar = {
            LMTopAppBar(
                title = "아이 추가", // 제목 변경
                onNavigationClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 2. 우주복 사진 선택기 (클릭 시 갤러리 열기)
            AstronautPhotoPicker(
                imageUri = selectedUri,
                onClick = {
//                    pickerLauncher.launch(
//                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                    )
                },
                headSize = 140.dp,
                bodyWidth = 150.dp,
                bodyOffsetY = 100.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. 실제 입력이 가능한 필드로 변경
            KidEditInputField(
                label = "이름",
                value = name,
                onValueChange = { name = it },
                placeholder = "아이 이름을 입력해주세요",
                icon = androidx.compose.material.icons.Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            KidEditInputField(
                label = "생년월일",
                value = birthDate,
                onValueChange = { birthDate = it },
                placeholder = "YYYY.MM.DD"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. 성별 선택 (이미 있는 GenderToggle 활용)
            GenderToggle(
                selected = selectedGender,
                onSelectedChange = { selectedGender = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            // 5. 등록하기 버튼
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = main),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "아이 등록하기", style = MaterialTheme.typography.labelLarge, color = lightbackground)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// 💡 입력 전용 컴포넌트 추가
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
        label = { Text(label, color = color4.copy(alpha = 0.6f)) },
        placeholder = { Text(placeholder, color = color4.copy(alpha = 0.3f)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        trailingIcon = {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, tint = color4.copy(alpha = 0.4f))
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = main,
            unfocusedBorderColor = Color(0xFFEEEEEE)
        )
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun KidDetailScreenPreview() {
    // 팀의 테마 이름이 NiaTheme가 맞는지 확인해 보세요!
    NiaTheme {
        KidAddScreen(
            onBackClick = {},
            onSaveClick = {} // 💡 onEditClick 대신 onSaveClick으로 수정!
        )
    }
}
