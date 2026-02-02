package com.a602.commonproject.feature.mypage

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.a602.commonproject.designsystem.component.Gender
import com.a602.commonproject.designsystem.component.GenderToggle
import com.a602.commonproject.designsystem.component.LMEditInputField
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.component.ProfileFullAstronaut
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.LMTheme
import com.a602.commonproject.designsystem.theme.*
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.feature.mypage.navigation.KidEditKey
import com.a602.commonproject.feature.mypage.viewmodel.KidEditUiState
import com.a602.commonproject.feature.mypage.viewmodel.KidEditViewModel
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.navigation.Navigator


@Composable
fun KidEditContainer(
    navigator: Navigator,
    key: KidEditKey,
    viewModel: KidEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key) {
        viewModel.initialize(key)
    }

    // 저장 또는 삭제 성공 시 뒤로가기
    LaunchedEffect(uiState.isSaveSuccess, uiState.isDeleteSuccess) {
        if (uiState.isSaveSuccess) {
            navigator.goBack()
            viewModel.onSaveSuccessConsumed()
        }
        if (uiState.isDeleteSuccess) {
            navigator.goBack()
            viewModel.onDeleteSuccessConsumed()
        }
    }

    // 에러 메시지 표시
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

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
            onDeleteClick = viewModel::deleteBaby,
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
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onImageSelected(uri) }
    )

    Scaffold(
        containerColor = background,
        topBar = {
            LMTopAppBar(
                title = "아이 정보 수정",
                onNavigationClick = onBackClick,
                actionIcon = LMicons.Delete,
                onActionClick = onDeleteClick
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
                        pickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    clickableEnabled = true,
                    showEditBadge = true,
                    headSize = 140.dp,
                    bodyWidth = 150.dp,
                    bodyOffsetY = 100.dp
                )

                Spacer(modifier = Modifier.height(24.dp))

                LMEditInputField(
                    label = "이름",
                    value = uiState.name,
                    onValueChange = onNameChanged,
                    trailingIcon = { Icon(LMicons.Person, contentDescription = "이름", tint = color4) }
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
                    onSelectedChange = { selected ->
                        if (selected != null) {
                            onGenderSelected(selected)
                        }
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                FilledButton(
                    text = "수정 완료",
                    onClick = onSaveClick,
                    size = ButtonSize.Full,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp, top = 12.dp)
                )
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
            onDeleteClick = {},
            onBackClick = {}
        )
    }
}
