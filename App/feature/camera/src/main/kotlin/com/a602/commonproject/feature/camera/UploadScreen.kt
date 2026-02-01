package com.a602.commonproject.feature.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun UploadScreen(
    backUri: String,
    subLocalUri: String,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: UploadViewModel = hiltViewModel()
) {
    var isSaving by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Main Image (Back Camera)
            Image(
                painter = rememberAsyncImagePainter(File(backUri)),
                contentDescription = "Main Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Sub Image (Front Camera) - PIP
            if (subLocalUri.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(100.dp, 133.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(File(subLocalUri)),
                        contentDescription = "Sub Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(32.dp),
            ) {
                Button(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("다시 찍기")
                }

                Button(
                    onClick = {
                        isSaving = true
                        viewModel.saveMedia(
                            backUri = backUri,
                            subLocalUri = subLocalUri,
                            onSuccess = {
                                isSaving = false
                                onSaveSuccess()
                            },
                            onError = {
                                isSaving = false
                                // Handle error
                            }
                        )
                    },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("저장")
                    }
                }
            }
        }
    }
}
