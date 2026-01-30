package com.a602.commonproject.feature.login.signup

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.ButtonSize
import com.a602.commonproject.designsystem.component.FilledButton
import com.a602.commonproject.designsystem.component.Gender
import com.a602.commonproject.designsystem.component.GenderToggle
import com.a602.commonproject.designsystem.component.LMEditInputField
import com.a602.commonproject.designsystem.component.ProfileFullAstronaut
import com.a602.commonproject.model.data.Baby
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 아기 등록 화면 (2단계)
 * - KidAddScreen의 로직과 디자인을 차용
 */
@Composable
fun AddBabyContent(
    onAddBaby: (String, String, Baby.Gender, File?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedGender: Gender? by remember { mutableStateOf(Gender.Male) } // Default

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // 1. Profile Image
        ProfileFullAstronaut(
            remoteImageUrl = null,
            selectedImageUri = selectedUri,
            onClick = {
                // TODO: Image Picker Linkage
                // For now, placeholder or implement simple picker if needed
            },
            headSize = 140.dp,
            bodyWidth = 150.dp,
            bodyOffsetY = 100.dp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Inputs
        LMEditInputField(
            value = name,
            onValueChange = { name = it },
            label = "이름",
            placeholder = "아이 이름을 입력해주세요",
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        LMEditInputField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = "생년월일",
            placeholder = "YYYY-MM-DD",
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Gender
        GenderToggle(
            selected = selectedGender,
            onSelectedChange = { selectedGender = it },
        )

        Spacer(Modifier.weight(1f))

        // 4. Submit
        FilledButton(
            text = "추가",
            onClick = {
                val file = selectedUri?.let { uriToFile(context, it) }
                val dataGender = when (selectedGender) {
                    Gender.Male -> Baby.Gender.MALE
                    Gender.Female -> Baby.Gender.FEMALE
                    else -> Baby.Gender.UNKNOWN
                }
                onAddBaby(name, birthDate, dataGender, file)
            },
            modifier = Modifier.fillMaxWidth(),
            size = ButtonSize.Full,
            enabled = name.isNotBlank() && birthDate.isNotBlank(),
        )

        Spacer(modifier = Modifier.height(32.dp))
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
