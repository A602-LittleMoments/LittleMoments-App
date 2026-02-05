package com.a602.commonproject.feature.login.signup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.a602.commonproject.designsystem.R
import com.a602.commonproject.designsystem.component.GenderButton
import com.a602.commonproject.designsystem.component.LMEditInputField
import com.a602.commonproject.designsystem.component.ProfileFullAstronaut
import com.a602.commonproject.designsystem.theme.NavyBlue
import com.a602.commonproject.model.data.Baby
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 아기 등록 화면 (2단계)
 * - BabyFormScreen의 디자인을 적용 (배경 이미지, Glassmorphism, GenderButton)
 */
@Composable
fun AddBabyContent(
    onAddBaby: (String, String, Baby.Gender, File?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedGender by remember { mutableStateOf("MALE") } // Default MALE to match BabyFormScreen logic

    val context = LocalContext.current

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedUri = uri
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.baby_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // 1. Profile Image Area (Fully Clickable)
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ProfileFullAstronaut(
                        remoteImageUrl = null,
                        selectedImageUri = selectedUri,
                        onClick = { /* Check handling via parent Box */ },
                        clickableEnabled = false,
//                        headSize = 140.dp,
//                        bodyWidth = 150.dp,
//                        bodyOffsetY = 105.dp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 2. Form Container (Glassmorphism)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.65f))
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Name Input
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "이름",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    LMEditInputField(
                        value = name,
                        onValueChange = { name = it },
                        label = "",
                        placeholder = "이름 (예: 김싸피)",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // BirthDate Input
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "생년월일",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    LMEditInputField(
                        value = birthDate,
                        onValueChange = { birthDate = it },
                        label = "",
                        placeholder = "생년월일 (예: 20240101)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Gender Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GenderButton(
                        text = "남자",
                        isSelected = selectedGender == "MALE",
                        onClick = { selectedGender = "MALE" },
                        modifier = Modifier.weight(1f)
                    )
                    GenderButton(
                        text = "여자",
                        isSelected = selectedGender == "FEMALE",
                        onClick = { selectedGender = "FEMALE" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 3. Submit Button
            Button(
                onClick = {
                    val file = selectedUri?.let { uriToFile(context, it) }
                    val dataGender = if (selectedGender == "MALE") Baby.Gender.MALE else Baby.Gender.FEMALE
                    onAddBaby(name, birthDate, dataGender, file)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyBlue,
                    contentColor = Color.White
                ),
                enabled = name.isNotBlank() && birthDate.isNotBlank()
            ) {
                Text(
                    text = "추가",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Simple utility to convert URI to File (Copied/Adapted for local use within feature)
private fun uriToFile(context: android.content.Context, uri: Uri): File? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_profile_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun AddBabyContentPreview() {
    com.a602.commonproject.designsystem.theme.LMTheme {
        AddBabyContent(
            onAddBaby = { _, _, _, _ -> }
        )
    }
}
