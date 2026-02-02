package com.a602.commonproject.feature.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.io.FileOutputStream
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.camera.core.CameraSelector
import androidx.camera.core.ConcurrentCamera
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.supervisorScope

@Composable
fun CameraScreen(
    onCloseClick: () -> Unit,
    onNavigateToTempAlbum: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.saveResultEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (hasCameraPermission) {
                CameraContent(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    onPhotoCaptured = { back, front ->
                        viewModel.savePhoto(back, front)
                    },
                    onNavigateToTempAlbum = onNavigateToTempAlbum
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("카메라 권한이 필요합니다.")
                    Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                        Text("권한 요청")
                    }
                }
            }

            // Close Button (Top End) is fine to keep or remove. Keeping for navigation safety.
            IconButton(
               onClick = onCloseClick,
               modifier = Modifier
                   .align(Alignment.TopEnd)
                   .padding(16.dp)
           ) {
                Icon(
                   imageVector = Default.Close,
                   contentDescription = "닫기",
                   tint = androidx.compose.ui.graphics.Color.White
               )
           }
        }
    }
}

@Composable
fun CameraContent(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onPhotoCaptured: (String, String) -> Unit,
    onNavigateToTempAlbum: () -> Unit
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // ... (PreviewViews initialization remains same)
    val backPreviewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    val frontPreviewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    val backImageCapture = remember { ImageCapture.Builder().build() }
    val frontImageCapture = remember { ImageCapture.Builder().build() }
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    var isDualMode by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Back Camera View
        AndroidView<PreviewView>(
            factory = { _ -> backPreviewView },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Front Camera View (PIP)
        if (isDualMode) {
            AndroidView<PreviewView>(
                factory = { _ -> frontPreviewView },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 16.dp)
                    .size(120.dp, 160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            )
        }

        // Bottom Control Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 40.dp, start = 32.dp, end = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Temp Album Shortcut
            Column(
                 horizontalAlignment = Alignment.CenterHorizontally,
                 modifier = Modifier.clickable(onClick = onNavigateToTempAlbum)
             ) {
                 // Placeholder for Gallery Icon/Thumbnail
               Box(
                     modifier = Modifier
                         .size(48.dp)
                         .clip(RoundedCornerShape(8.dp))
                         .background(Color.Black.copy(alpha = 0.5f))
                         .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                     contentAlignment = Alignment.Center
                 ) {
                      // TODO: Show latest image thumbnail if possible
                 Icon(
                         imageVector = Default.PhotoLibrary, // Use default or LMicon
                         contentDescription = "임시 앨범",
                         tint = Color.White
                     )
                 }
                 Text("임시 앨범", color = Color.White, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top=4.dp))
             }

            // Center: Shutter Button
             val scope = rememberCoroutineScope()
          Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable { // Click Logic
                        // ... Same capture logic ...
                         val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA)
                         val timestamp = sdf.format(Date())
                         val backFile = File(context.externalCacheDir, "back_$timestamp.jpg")
                         val frontFile = if (isDualMode) File(context.externalCacheDir, "front_$timestamp.jpg") else null
                         val backOutputOptions = ImageCapture.OutputFileOptions.Builder(backFile).build()
                         val frontOutputOptions = frontFile?.let { ImageCapture.OutputFileOptions.Builder(it).build() }

                         scope.launch {
                             try {
                                 supervisorScope {
                                     val backJob = async { backImageCapture.takePicture(backOutputOptions, mainExecutor); backFile.absolutePath }
                                     val frontPath = if (isDualMode && frontOutputOptions != null) {
                                         val frontJob = async { delay(150); frontImageCapture.takePicture(frontOutputOptions, mainExecutor); frontFile.absolutePath }
                                         frontJob.await()
                                     } else ""

                                     onPhotoCaptured(backJob.await(), frontPath)
                                 }
                             } catch (e: Exception) { Log.e("Camera", "Error", e) }
                         }
                    },
                contentAlignment = Alignment.Center
            ) {
                 Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Black, CircleShape)
                )
            }

            // Right: Spacer to balance layout
            Box(modifier = Modifier.size(48.dp))
        }

        // Camera Binding Logic (Same as before)
        LaunchedEffect(cameraProviderFuture) {
             // ... Binding logic ...
             val cameraProvider = cameraProviderFuture.get()
             val backSelector = CameraSelector.DEFAULT_BACK_CAMERA
             val frontSelector = CameraSelector.DEFAULT_FRONT_CAMERA
             val backPreview = Preview.Builder().build().also { it.surfaceProvider = backPreviewView.surfaceProvider }
             val frontPreview = Preview.Builder().build().also { it.surfaceProvider = frontPreviewView.surfaceProvider }
             val isConcurrentSupported = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_CONCURRENT)
             try {
                 cameraProvider.unbindAll()
                 if (isConcurrentSupported) {
                     val backConfig = ConcurrentCamera.SingleCameraConfig(backSelector, UseCaseGroup.Builder().addUseCase(backPreview).addUseCase(backImageCapture).build(), lifecycleOwner)
                     val frontConfig = ConcurrentCamera.SingleCameraConfig(frontSelector, UseCaseGroup.Builder().addUseCase(frontPreview).addUseCase(frontImageCapture).build(), lifecycleOwner)
                     cameraProvider.bindToLifecycle(listOf(backConfig, frontConfig))
                     isDualMode = true
                 } else {
                     cameraProvider.bindToLifecycle(lifecycleOwner, backSelector, backPreview, backImageCapture)
                     isDualMode = false
                 }
             } catch (e: Exception) { isDualMode = false }
        }
    }
}

suspend fun ImageCapture.takePicture(
    outputOptions: ImageCapture.OutputFileOptions,
    executor: Executor
): ImageCapture.OutputFileResults = suspendCoroutine { continuation ->
    Log.d("CameraScreenExt", "takePicture 확장 함수 호출됨")
    this.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d("CameraScreenExt", "onImageSaved 콜백 수신: ${outputFileResults.savedUri}")
                continuation.resumeWith(Result.success(outputFileResults))
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreenExt", "onError 콜백 수신: ${exception.message}", exception)
                continuation.resumeWith(Result.failure(exception))
            }
        }
    )
}
