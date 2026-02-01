package com.a602.commonproject.feature.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp

@Composable
fun CameraScreen(
    onCloseClick: () -> Unit,
    onCaptureSuccess: (String, String) -> Unit // BackUri, FrontUri
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

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
                    onCaptureSuccess = onCaptureSuccess
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

            // Close Button Overlay
            Button(
                onClick = onCloseClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("닫기")
            }
        }
    }
}

@Composable
fun CameraContent(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onCaptureSuccess: (String, String) -> Unit
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // 두 개의 PreviewView를 위한 레이아웃: PIP (Picture-in-Picture) 스타일
    // 후면 카메라: 전체 화면 / 전면 카메라: 우측 상단 작은 화면

    // Previews are created once and reused to avoid re-inflating
    val backPreviewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }
    val frontPreviewView = remember { PreviewView(context).apply { scaleType = PreviewView.ScaleType.FILL_CENTER } }

    // Implements ImageCapture use cases
    val backImageCapture = remember { androidx.camera.core.ImageCapture.Builder().build() }
    val frontImageCapture = remember { androidx.camera.core.ImageCapture.Builder().build() }
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }

    // State to track if dual mode (concurrent camera) is actually active
    var isDualMode by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Back Camera View (Background)
        AndroidView(
            factory = { backPreviewView },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Front Camera View (Floating PIP) - Show only if dual mode
        if (isDualMode) {
            AndroidView(
                factory = { frontPreviewView },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 16.dp)
                    .size(120.dp, 160.dp)
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            )
        }

        // Capture Button
        val scope = rememberCoroutineScope()

        Button(
            onClick = {
                val timestamp = System.currentTimeMillis()
                val backFile = java.io.File(context.externalCacheDir, "back_$timestamp.jpg")
                // Front file is only needed if dual mode
                val frontFile = if (isDualMode) java.io.File(context.externalCacheDir, "front_$timestamp.jpg") else null

                val backOutputOptions = androidx.camera.core.ImageCapture.OutputFileOptions.Builder(backFile).build()
                val frontOutputOptions = frontFile?.let { androidx.camera.core.ImageCapture.OutputFileOptions.Builder(it).build() }

                scope.launch {
                    try {
                        // Use supervisorScope to prevent child coroutine (async) failure from crashing the parent scope
                        kotlinx.coroutines.supervisorScope {
                            // Back Capture is always done
                            val backJob = async {
                                backImageCapture.takePicture(backOutputOptions, mainExecutor)
                                backFile.absolutePath
                            }

                            val frontPath = if (isDualMode && frontOutputOptions != null) {
                                val frontJob = async {
                                    frontImageCapture.takePicture(frontOutputOptions, mainExecutor)
                                    frontFile!!.absolutePath
                                }
                                frontJob.await()
                            } else {
                                "" // No front image
                            }

                            val backPath = backJob.await()

                            Log.d("CameraScreen", "촬영 성공: 후면=$backPath, 전면=$frontPath")
                            onCaptureSuccess(backPath, frontPath)
                        }
                    } catch (e: Exception) { // ImageCaptureException includes "Camera is closed"
                        Log.e("CameraScreen", "촬영 실패: ${e.message}", e)
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text("촬영")
        }

        // Camera Binding Logic
        LaunchedEffect(cameraProviderFuture) {
            val cameraProvider = cameraProviderFuture.get()

            val backSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val frontSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            val backPreview = Preview.Builder().build().also {
                it.setSurfaceProvider(backPreviewView.surfaceProvider)
            }
            val frontPreview = Preview.Builder().build().also {
                it.setSurfaceProvider(frontPreviewView.surfaceProvider)
            }

            // Check Concurrent Support
            val isConcurrentSupported = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_CONCURRENT)
            Log.d("CameraScreen", "동시 카메라(Concurrent) 지원 여부: $isConcurrentSupported")

            try {
                cameraProvider.unbindAll()

                if (isConcurrentSupported) {
                    Log.d("CameraScreen", "동시 카메라 바인딩 시작 (전면 + 후면)")
                    // Concurrent Binding
                    val backConfig = androidx.camera.core.ConcurrentCamera.SingleCameraConfig(
                        backSelector,
                        androidx.camera.core.UseCaseGroup.Builder()
                            .addUseCase(backPreview)
                            .addUseCase(backImageCapture)
                            .build(),
                        lifecycleOwner
                    )
                    val frontConfig = androidx.camera.core.ConcurrentCamera.SingleCameraConfig(
                        frontSelector,
                        androidx.camera.core.UseCaseGroup.Builder()
                            .addUseCase(frontPreview)
                            .addUseCase(frontImageCapture)
                            .build(),
                        lifecycleOwner
                    )

                    cameraProvider.bindToLifecycle(listOf(backConfig, frontConfig))
                    isDualMode = true
                } else {
                    Log.d("CameraScreen", "동시 카메라 미지원. 후면 카메라만 사용합니다.")
                    // Fallback: Bind only Back Camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        backSelector,
                        backPreview,
                        backImageCapture
                    )
                    isDualMode = false
                }
            } catch (e: Exception) {
                Log.e("CameraScreen", "카메라 바인딩 실패", e)
                isDualMode = false
            }
        }
    }
}

suspend fun androidx.camera.core.ImageCapture.takePicture(
    outputOptions: androidx.camera.core.ImageCapture.OutputFileOptions,
    executor: java.util.concurrent.Executor
): androidx.camera.core.ImageCapture.OutputFileResults = kotlin.coroutines.suspendCoroutine { continuation ->
    this.takePicture(
        outputOptions,
        executor,
        object : androidx.camera.core.ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: androidx.camera.core.ImageCapture.OutputFileResults) {
                continuation.resumeWith(Result.success(outputFileResults))
            }

            override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                continuation.resumeWith(Result.failure(exception))
            }
        }
    )
}
