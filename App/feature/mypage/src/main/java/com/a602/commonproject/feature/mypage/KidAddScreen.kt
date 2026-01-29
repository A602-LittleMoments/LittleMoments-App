package com.a602.commonproject.feature.mypage

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.AstronautPhotoPicker
import com.a602.commonproject.designsystem.component.Gender
import com.a602.commonproject.designsystem.component.GenderToggle
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.theme.AppTypography
import com.a602.commonproject.designsystem.theme.NiaTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.color4
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.lightblue
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.model.data.Baby


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidAddScreen( // 이름을 Add(추가)로 변경합니다.
    onBackClick: () -> Unit = {},
    onSaveClick: (Baby) -> Unit = {} // 💡 저장 시 Baby 객체를 전달하도록 변경
) {
    // 1. 입력을 위한 상태 변수들 (비어있는 상태로 시작)
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }

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
                onClick = {
                     // 💡 입력된 정보로 Baby 객체 생성 후 전달
                    val newBaby = Baby(
                        babyId = "", // ID는 보통 서버에서 생성하므로 비워둡니다.
                        babyName = name,
                        birthDate = birthDate,
                        gender = when(selectedGender) { // designsystem Gender -> model Gender
                            Gender.Male -> Baby.Gender.MALE
                            Gender.Female -> Baby.Gender.FEMALE
                            else -> Baby.Gender.UNKNOWN
                        },
                        imageUrl = selectedUri?.toString()
                    )
                    onSaveClick(newBaby)
                },
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


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun KidAddScreenPreview() { // 💡 프리뷰 이름 수정
    NiaTheme {
        KidAddScreen(
            onBackClick = {},
            onSaveClick = {}
        )
    }
}
