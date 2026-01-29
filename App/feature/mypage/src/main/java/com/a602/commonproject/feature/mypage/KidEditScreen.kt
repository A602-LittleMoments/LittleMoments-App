package com.a602.commonproject.feature.mypage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.component.GenderToggle
import com.a602.commonproject.designsystem.component.ProfileHead
import com.a602.commonproject.designsystem.component.Gender
import com.a602.commonproject.designsystem.theme.*
import androidx.compose.ui.tooling.preview.Preview
import com.a602.commonproject.designsystem.component.ProfileFullAstronaut

import com.a602.commonproject.model.data.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidEditScreen(
    baby: Baby,
    onBackClick: () -> Unit = {},
    onSaveClick: (Baby) -> Unit = {},
) {
    // 💡 1. 초기값을 SampleData(baby)에서 직접 가져오도록 연결합니다.
    var name by remember { mutableStateOf(baby.babyName) }
    var birthDate by remember { mutableStateOf(baby.birthDate) }

    // 💡 2. 성별도 baby에 저장된 값을 기본값으로 불러옵니다.
    // (모델에 따라 Gender.MALE 또는 Gender.Male 형식을 확인하세요)
    var selectedGender by remember { mutableStateOf<Baby.Gender?>(baby.gender) }

    Scaffold(
        containerColor = background,
        topBar = {
            LMTopAppBar(
                title = "아이 정보 수정",
                onNavigationClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 2. 우주복 사진 선택기 (클릭 시 갤러리 열기)
            ProfileFullAstronaut(
                remoteImageUrl = null,
                selectedImageUri = null,
                onClick = {
//                    pickerLauncher.launch(
//                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                    )
                },
                // ✅ [수정] 아래 파라미터들을 추가하여 크기를 조절합니다.
                headSize = 140.dp,
                bodyWidth = 150.dp,
                bodyOffsetY = 100.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            KidEditInputField(
                label = "이름",
                value = name,
                onValueChange = { name = it }, // 💡 name 변수 사용
                icon = androidx.compose.material.icons.Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            KidEditInputField(
                label = "생년월일",
                value = birthDate,
                onValueChange = { birthDate = it } // 💡 birthDate 변수 사용
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 성별 선택 창
            GenderToggle(
                // 💡 1. 우리 장부(Baby.Gender)를 버튼 양식(Gender)으로 바꿔서 보여줍니다.
                selected = when(selectedGender) {
                    Baby.Gender.MALE -> Gender.Male
                    Baby.Gender.FEMALE -> Gender.Female
                    else -> null
                },
                // 💡 2. 버튼에서 선택된 양식(it)을 다시 우리 장부(Baby.Gender)로 바꿔서 저장합니다.
                onSelectedChange = { it ->
                    selectedGender = when(it) {
                        Gender.Male -> Baby.Gender.MALE
                        Gender.Female -> Baby.Gender.FEMALE
                        else -> null
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // 💡 3. 변수 이름을 일치시키고(nameValue -> name), 성별(gender)도 포함합니다!
                    val updated = baby.copy(
                        babyName = name,
                        birthDate = birthDate,
                        gender = selectedGender ?: baby.gender // 선택 안 했으면 기존 성별 유지
                    )
                    onSaveClick(updated)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = main),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "수정 완료", style = MaterialTheme.typography.labelLarge, color = lightbackground)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun KidEditScreenPreview() {
    LtTheme {
        KidEditScreen(
            baby = Baby(
                babyId = "sampleId",
                babyName = "김이든",
                birthDate = "2023-08-25",
                gender = Baby.Gender.MALE,
                imageUrl = null
            ),
            onBackClick = {},
            onSaveClick = {}
        )
    }
}

