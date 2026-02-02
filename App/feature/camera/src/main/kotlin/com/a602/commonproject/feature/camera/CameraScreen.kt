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
import androidx.compose.ui.unit.dp
import java.util.concurrent.Executor
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.supervisorScope

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
    // Back Preview: Use COMPATIBLE mode (TextureView) to allow bitmap capture for double bitmap concurrent shooting
    val backPreviewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    // Front Preview: Use COMPATIBLE mode (TextureView) to allow bitmap capture for hybrid concurrent shooting
    val frontPreviewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    // Implements ImageCapture use cases
    val backImageCapture = remember { ImageCapture.Builder().build() }
    val frontImageCapture = remember { ImageCapture.Builder().build() }
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }

    // State to track if dual mode (concurrent camera) is actually active
    var isDualMode by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Back Camera View (Background)
        AndroidView<PreviewView>(
            factory = { _ -> backPreviewView },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Front Camera View (Floating PIP) - Show only if dual mode
        if (isDualMode) {
            AndroidView<PreviewView>(
                factory = { _ -> frontPreviewView },
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
                val sdf = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.KOREA)
                val timestamp = sdf.format(java.util.Date())
                val backFile = java.io.File(context.externalCacheDir, "back_$timestamp.jpg")
                // Front file is only needed if dual mode
                val frontFile = if (isDualMode) java.io.File(context.externalCacheDir, "front_$timestamp.jpg") else null

                val backOutputOptions = androidx.camera.core.ImageCapture.OutputFileOptions.Builder(backFile).build()
                val frontOutputOptions = frontFile?.let { androidx.camera.core.ImageCapture.OutputFileOptions.Builder(it).build() }

                scope.launch {
                    Log.d("CameraScreen", "촬영 버튼 클릭됨")
                    try {
                        // Use supervisorScope to prevent child coroutine (async) failure from crashing the parent scope
                        supervisorScope {
                             Log.d("CameraScreen", "동시 촬영 시작 (Hybrid: Back=API, Front=Bitmap)")

                             // 1. 후면 촬영 시작 (API 사용 - 고화질)
                             val backJob = async {
                                 Log.d("CameraScreen", "후면 카메라 takePicture 요청")
                                 val result = backImageCapture.takePicture(backOutputOptions, mainExecutor)
                                 Log.d("CameraScreen", "후면 카메라 촬영 완료: ${result.savedUri}")
                                 backFile.absolutePath
                             }

                             // 2. 전면 촬영 시작 (API 사용 - Staggered 방식)
                             // NOTE: 실기기 테스트를 위해 API 방식으로 복구. (Bitmap 방식은 아래에 주석 처리됨)
                             
                             val frontPath = if (isDualMode && frontOutputOptions != null) {
                                 val frontJob = async {
                                     delay(150) // Driver crash workaround
                                     Log.d("CameraScreen", "전면 카메라 takePicture 요청")
                                     val result = frontImageCapture.takePicture(frontOutputOptions, mainExecutor)
                                     Log.d("CameraScreen", "전면 카메라 촬영 완료: ${result.savedUri}")
                                     frontFile.absolutePath
                                 }
                                 frontJob.await()
                             } else { "" }
                             

                             /*
                             // Bitmap 캡처 방식 (에뮬레이터/드라이버 호환성 이슈 해결용)
                             val frontPath = if (isDualMode) {
                                 Log.d("CameraScreen", "전면 카메라 Bitmap 캡처 시작")
                                 val bitmap = frontPreviewView.bitmap
                                 if (bitmap != null) {
                                     val fFile = java.io.File(context.externalCacheDir, "front_${System.currentTimeMillis()}.jpg")
                                     try {
                                         java.io.FileOutputStream(fFile).use { out ->
                                             bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                                         }
                                         Log.d("CameraScreen", "전면 카메라 Bitmap 저장 완료: ${fFile.absolutePath}")
                                         fFile.absolutePath
                                     } catch (e: Exception) {
                                         Log.e("CameraScreen", "전면 Bitmap 저장 실패", e)
                                         ""
                                     }
                                 } else {
                                     Log.e("CameraScreen", "전면 PreviewView Bitmap이 null입니다.")
                                     ""
                                 }
                             } else {
                                 ""
                             }
                             */

                             Log.d("CameraScreen", "후면 카메라 결과 대기 중")
                             val backPath = backJob.await()

                             Log.d("CameraScreen", "모든 촬영 완료. onCaptureSuccess 호출: 후면=$backPath, 전면=$frontPath")
                             onCaptureSuccess(backPath, frontPath)
                         }
                    } catch (e: Exception) {
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
                it.surfaceProvider = backPreviewView.surfaceProvider
            }
            val frontPreview = Preview.Builder().build().also {
                it.surfaceProvider = frontPreviewView.surfaceProvider
            }

            // Check Concurrent Support
            val isConcurrentSupported = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_CONCURRENT)
            Log.d("CameraScreen", "동시 카메라(Concurrent) 지원 여부: $isConcurrentSupported")

            try {
                cameraProvider.unbindAll()

                if (isConcurrentSupported) {
                    Log.d("CameraScreen", "동시 카메라 바인딩 시작 (전면 + 후면)")
                    // Concurrent Binding
                    val backConfig = ConcurrentCamera.SingleCameraConfig(
                        backSelector,
                        UseCaseGroup.Builder()
                            .addUseCase(backPreview)
                            .addUseCase(backImageCapture)
                            .build(),
                        lifecycleOwner
                    )
                    val frontConfig = ConcurrentCamera.SingleCameraConfig(
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
