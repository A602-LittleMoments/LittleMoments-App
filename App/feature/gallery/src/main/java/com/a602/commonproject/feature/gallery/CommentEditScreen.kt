package com.a602.commonproject.feature.gallery

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.component.Polaroid
import com.a602.commonproject.designsystem.component.PolaroidMeta
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.lightbackground


data class CommentEditUiState(
    val caption: String,
    val rearImage: ImageBitmap,
    val frontImage: ImageBitmap,
    val meta: PolaroidMeta,
)


//sealed interface CommentEditAction {
//    data object Back : CommentEditAction
//    data class CaptionChanged(val text: String) : CommentEditAction
//    data object Submit : CommentEditAction
//}
//
@Composable
fun CommentEditScreen(
    uiState: CommentEditUiState,
    onCaptionChange: (String) -> Unit,
    onSave: () -> Unit

) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            LMTopAppBar(
                title = "코멘트",
                actionIcon = LMicons.Download,
                actionIconContentDescription = "저장",
                onActionClick = onSave
            )
        },
        containerColor = background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Polaroid(
                rearImage = uiState.rearImage,
                frontImage = uiState.frontImage,
                meta = uiState.meta.copy(comment = ""),
                modifier = Modifier.fillMaxWidth().padding(24.dp)
            )

            TextField(
                value = uiState.caption,
                onValueChange = onCaptionChange,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.Center)
                    .padding(bottom = 80.dp)
                    .focusRequester(focusRequester)
                    .background(lightbackground),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray
                )
            )
        }
    }
}



private fun previewBitmap(
    width: Int = 1080,
    height: Int = 1440,
    color: Int
): ImageBitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bmp.eraseColor(color)
    return bmp.asImageBitmap()
}


@Preview(showBackground = true, heightDp = 760)
@Composable
fun CommentEditScreenPreview() {
    val rear = previewBitmap(color = Color.DarkGray.toArgb())
    val front = previewBitmap(color = Color.LightGray.toArgb())

    var caption by remember { mutableStateOf("첫 산책 😊") }

    CommentEditScreen(
        uiState = CommentEditUiState(
            caption = caption,
            rearImage = rear,
            frontImage = front,
            meta = PolaroidMeta(
                date = "2026.01.02",
                role = "엄마"
            )
        ),
        onCaptionChange = { caption = it },
        onSave = {
            println("저장됨: $caption")
        }
    )
}

