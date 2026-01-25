package com.a602.commonproject.designsystem.component

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.a602.commonproject.designsystem.theme.color5
import com.a602.commonproject.designsystem.theme.gray2

@Composable
fun CircularPhotoPicker(
    imageUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    borderWidth: Dp = 10.dp,
    borderColor: Color = color5,
    placeholderColor: Color = gray2
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(placeholderColor)
            .border(
                BorderStroke(borderWidth, borderColor),
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {

        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "selected photo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.AddAPhoto,
                contentDescription = "add photo",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

// 사용 예시
// Photo Picker 사용해서 갤러리에서 사진 고르기
// 우주복은 실제 사용할 때 밑에 넣어야 함

//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.PickVisualMediaRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.runtime.*
//
//@Composable
//fun ProfilePhotoSection() {
//    var selectedUri by remember { mutableStateOf<Uri?>(null) }
//
//    val pickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickVisualMedia()
//    ) { uri ->
//        selectedUri = uri
//    }
//
//    CircularPhotoPicker(
//        imageUri = selectedUri,
//        onClick = {
//            pickerLauncher.launch(
//                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//            )
//        }
//    )
//}
