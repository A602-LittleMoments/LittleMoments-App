package com.a602.commonproject.feature.gallery

import Polaroid
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.a602.commonproject.designsystem.component.LMTopAppBar
import com.a602.commonproject.designsystem.icon.LMicons
import com.a602.commonproject.designsystem.theme.background
import com.a602.commonproject.designsystem.theme.lightbackground
import com.a602.commonproject.model.data.SharedMedia

@Composable
fun CommentEditScreen(
    media: SharedMedia,
    caption: String,
    onCaptionChange: (String) -> Unit,
    onBack: () -> Unit,
    onDone: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = background,
        topBar = {
            LMTopAppBar(
                title = "코멘트",
                actionIcon = LMicons.Download,
                actionIconContentDescription = "저장",
                onActionClick = { onDone(caption) },             )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Polaroid(
                        media = media,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                TextField(
                    value = caption,
                    onValueChange = onCaptionChange,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .align(Alignment.Center)
                        .padding(bottom = 80.dp)
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center
                    ),
                    placeholder = {
                        androidx.compose.material3.Text(
                            text = "코멘트를 입력하세요",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Gray
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightbackground,
                        unfocusedContainerColor = lightbackground,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Gray
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun CommentEditScreenPreview() {
    val fakeMedia = SharedMedia(
        id = "1",
        type = SharedMedia.MediaType.PHOTO,
        localUri = null,
        remoteUrl = "https://picsum.photos/600/800",
        thumbnailUrl = null,
        subLocalUri = null,
        subRemoteUrl = null,
        subThumbnailUrl = null,
        cameraFacing = "DUAL",
        caption = null,
        dateTaken = System.currentTimeMillis(),
        orientation = 0,
        uploaderName = "엄마",
        syncStatus = SharedMedia.SyncStatus.SYNCED
    )

    CommentEditScreen(
        media = fakeMedia,
        caption ="안녕하새요",
        onCaptionChange = {},
        onDone = {},
        onBack = {}
    )
}
