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
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.color5
import com.a602.commonproject.designsystem.theme.gray2
import com.a602.commonproject.designsystem.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.offset

//Preview용
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import com.a602.commonproject.designsystem.theme.lightbackground


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
                imageVector = LMicons.Camera,
                contentDescription = "add photo",
                tint = lightbackground,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun AstronautPhotoPicker(
    imageUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,

    // 얼굴 설정
    headSize: Dp = 180.dp,

    // 우주복 설정 (네가 조절할 핵심 포인트)
    bodyResId: Int = R.drawable.astronaut_body,
    bodyWidth: Dp = 240.dp,
    bodyOffsetY: Dp = 130.dp
) {
    val extraBottom =
        if (bodyOffsetY + bodyWidth > headSize) {
            bodyOffsetY + bodyWidth - headSize
        } else {
            0.dp
        }
    Box(
        modifier = modifier.padding(bottom = extraBottom),
        contentAlignment = Alignment.TopCenter
    ) {
        // 1️⃣ 얼굴 (기존 컴포넌트 그대로 사용)
        CircularPhotoPicker(
            imageUri = imageUri,
            onClick = onClick,
            size = headSize
        )

        // 2️⃣ 우주복 (항상 얼굴 아래)
        Image(
            painter = painterResource(bodyResId),
            contentDescription = null,
            modifier = Modifier
                .offset(y = bodyOffsetY)
                .size(bodyWidth),
            contentScale = ContentScale.Fit
        )
    }
}

// 사용 예시
// Photo Picker 사용해서 갤러리에서 사진 고르기

@Preview(showBackground = true, backgroundColor = 0xFFF6F1E8)
@Composable
private fun Preview_CircularPhotoPicker_Empty() {
    MaterialTheme {
        Surface {
            CircularPhotoPicker(
                imageUri = null,
                onClick = {},
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F1E8)
@Composable
private fun Preview_AstronautPhotoPicker_Empty() {
    MaterialTheme {
        Surface(
            modifier = Modifier.wrapContentSize()
        ) {
            AstronautPhotoPicker(
                imageUri = null,
                onClick = {},
                modifier = Modifier.padding(24.dp),

                // 필요하면 여기서 값 조절하면서 위치 맞추기
                headSize = 180.dp,
                bodyWidth = 240.dp,
                bodyOffsetY = 130.dp
            )
        }
    }
}


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
