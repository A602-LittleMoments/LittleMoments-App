package com.a602.commonproject.feature.baby

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday // Added explicit import for generic icons used if any
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.component.ProfileFullAstronaut
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.model.data.Baby

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyFormScreen(
    babyId: String? = null, // null = Add Mode, non-null = Edit Mode
    initialBabyData: Baby? = null, // Optional: Pass directly if available
    onBackClick: () -> Unit,
    onSaveClick: (String, String, String, Uri?) -> Unit, // name, birthDate, gender, imageUri
    onDeleteClick: (() -> Unit)? = null,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isEditMode = babyId != null
    val uiState by viewModel.uiState.collectAsState()

    // Find baby data if in Edit Mode
    val targetBaby = remember(uiState, babyId) {
        if (babyId != null && uiState is HomeUiState.Success) {
            (uiState as HomeUiState.Success).babies.find { it.babyId == babyId }
        } else initialBabyData
    }

    // State
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("MALE") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Image Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) selectedImageUri = uri }
    )

    // Load data into state when targetBaby becomes available
    LaunchedEffect(targetBaby) {
        targetBaby?.let {
            name = it.babyName
            birthDate = it.birthDate.replace("-", "") // Convert YYYY-MM-DD to YYYYMMDD
            gender = it.gender.name
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            com.a602.commonproject.designsystem.component.LMTopAppBar(
                title = if (isEditMode) "아이 정보 수정" else "아이 정보 등록",
                onNavigationClick = onBackClick,
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.Transparent,
//                    titleContentColor = Color.White,
//                    navigationIconContentColor = Color.White,
//                    actionIconContentColor = Color.White
//                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.baby_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp)) // Lower components

                // Profile Image Area (Fully Clickable)
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        ProfileFullAstronaut(
                            selectedImageUri = selectedImageUri,
                            remoteImageUrl = targetBaby?.imageUrl,
                            clickableEnabled = false // Parent handles click
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Name Input (Standard Component - Label-less style, Center Aligned)
                com.a602.commonproject.designsystem.component.LMEditInputField(
                    label = "",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "이름 (예: 김싸피)",
                    modifier = Modifier.fillMaxWidth(),

                )

                Spacer(modifier = Modifier.height(16.dp))

                // BirthDate Input (Standard Component - Label-less style, Center Aligned)
                com.a602.commonproject.designsystem.component.LMEditInputField(
                    label = "",
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    placeholder = "생년월일 (예: 20240101)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),

                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gender Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GenderButton(
                        text = "남자",
                        isSelected = gender == "MALE",
                        onClick = { gender = "MALE" },
                        modifier = Modifier.weight(1f)
                    )
                    GenderButton(
                        text = "여자",
                        isSelected = gender == "FEMALE",
                        onClick = { gender = "FEMALE" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isEditMode) {
                        Button(
                            onClick = {
                                viewModel.updateBaby(
                                    babyId = babyId!!,
                                    name = name,
                                    birthDate = birthDate,
                                    gender = gender,
                                    imageUri = selectedImageUri
                                ) {
                                    onSaveClick(name, birthDate, gender, selectedImageUri)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.a602.commonproject.designsystem.theme.NavyBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "수정완료",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.deleteBaby(babyId = babyId!!) {
                                    onDeleteClick?.invoke()
                                }
                            },
                            modifier = Modifier
                                .weight(0.5f)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF5252),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "삭제",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.addBaby(
                                    name = name,
                                    birthDate = birthDate,
                                    gender = gender,
                                    imageUri = selectedImageUri
                                ) {
                                    onSaveClick(name, birthDate, gender, selectedImageUri)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.a602.commonproject.designsystem.theme.NavyBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "등록하기",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
// Removed BabyFormField


@Composable
fun GenderButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(24.dp)) // [Theme] 24.dp match
            .background(
                // Selected: PointYellow, Unselected: Opaque Gray
                if (isSelected) com.a602.commonproject.designsystem.theme.PointYellow else Color(0xFFEEEEEE)
            )
            .clickable { onClick() }
            .border(
                width = 1.dp,
                // Selected: None, Unselected: Gray alpha
                color = if (isSelected) Color.Transparent else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            // Selected: NavyBlue, Unselected: Dark Gray
            color = if (isSelected) com.a602.commonproject.designsystem.theme.NavyBlue else Color.Gray,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


