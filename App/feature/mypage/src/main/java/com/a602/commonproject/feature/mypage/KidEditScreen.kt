package com.a602.commonproject.feature.mypage

import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.a602.commonproject.designsystem.component.Gender
import com.a602.commonproject.designsystem.component.GenderToggle
import com.a602.commonproject.designsystem.component.LMEditInputField
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.component.ProfileFullAstronaut
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.designsystem.theme.main
import com.a602.commonproject.feature.mypage.viewmodel.KidEditUiState
import com.a602.commonproject.feature.mypage.viewmodel.KidEditViewModel
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.navigation.Navigator


@Composable
fun KidEditContainer(
    navigator: Navigator,
    viewModel: KidEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 저장 성공 시 뒤로가기
    LaunchedEffect(uiState.isSaveSuccess) {
        if (uiState.isSaveSuccess) {
            navigator.goBack()
            viewModel.onSaveSuccessConsumed()
        }
    }

    // 에러 메시지 표시
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // 데이터 로딩 중 UI
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        KidEditScreen(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onNameChanged = viewModel::onNameChanged,
            onBirthDateChanged = viewModel::onBirthDateChanged,
            onGenderSelected = {
                val modelGender = when (it) {
                    Gender.Male -> Baby.Gender.MALE
                    Gender.Female -> Baby.Gender.FEMALE
                }
                viewModel.onGenderSelected(modelGender)
            },
            onImageSelected = { uri ->
                viewModel.onImageSelected(uri?.toString())
            },
            onSaveClick = viewModel::updateBaby,
            onBackClick = { navigator.goBack() }
        )
    }
}

@Composable
fun KidEditScreen(
    uiState: KidEditUiState,
    snackbarHostState: SnackbarHostState,
    onNameChanged: (String) -> Unit,
    onBirthDateChanged: (String) -> Unit,
    onGenderSelected: (Gender) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
//    val pickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickVisualMedia(),
//        onResult = { uri -> onImageSelected(uri) }
//    )

    Scaffold(
        containerColor = background,
        topBar = {
            LMTopAppBar(
                title = "아이 정보 수정",
                onNavigationClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                ProfileFullAstronaut(
                    selectedImageUri = uiState.imageUri?.let { Uri.parse(it) },
                    remoteImageUrl = uiState.imageUri,
                    onClick = {
//                        pickerLauncher.launch(
//                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//                        )
                    },
                    headSize = 140.dp,
                    bodyWidth = 150.dp,
                    bodyOffsetY = 100.dp
                )

                Spacer(modifier = Modifier.height(24.dp))

                LMEditInputField(
                    label = "이름",
                    value = uiState.name,
                    onValueChange = onNameChanged,
                    icon = Icons.Outlined.Person
                )

                Spacer(modifier = Modifier.height(16.dp))

                LMEditInputField(
                    label = "생년월일",
                    value = uiState.birthDate,
                    onValueChange = onBirthDateChanged,
                    placeholder = "YYYY.MM.DD"
                )

                Spacer(modifier = Modifier.height(24.dp))

                val selectedGender = when (uiState.gender) {
                    Baby.Gender.MALE -> Gender.Male
                    Baby.Gender.FEMALE -> Gender.Female
                    else -> null
                }
                GenderToggle(
                    selected = selectedGender,
                    onSelectedChange = { selected -> // 람다의 파라미터 이름을 'it'에서 'selected'로 변경
                        // 👇 null이 아닐 때만 onGenderSelected를 호출하도록 수정
                        if (selected != null) {
                            onGenderSelected(selected)
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = main),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "수정 완료", style = MaterialTheme.typography.labelLarge, color = lightbackground)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true, name = "아이 정보 수정 미리보기")
@Composable
fun KidEditScreenPreview() {
    LMTheme {
        KidEditScreen(
            uiState = KidEditUiState(name = "튼튼이", birthDate = "2024.05.20"),
            snackbarHostState = remember { SnackbarHostState() },
            onNameChanged = {},
            onBirthDateChanged = {},
            onGenderSelected = {},
            onImageSelected = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
