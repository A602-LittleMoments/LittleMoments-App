package com.a602.commonproject.feature.camera.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.camera.CameraScreen
import com.a602.commonproject.feature.camera.UploadScreen
import com.a602.commonproject.navigation.Navigator
import com.a602.commonproject.feature.camera.navigation.UploadNavKey

fun EntryProviderScope<NavKey>.cameraEntries(navigator: Navigator) {
    entry<CameraNavKey> {
        CameraScreen(
            onCloseClick = { navigator.goBack() },
            onCaptureSuccess = { backUri, frontUri ->
                navigator.navigate(
                    UploadNavKey(
                        backUri = backUri,
                        subLocalUri = frontUri
                    )
                )
            }
        )
    }

    entry<UploadNavKey> { args ->
        UploadScreen(
            backUri = args.backUri,
            subLocalUri = args.subLocalUri,
            onBackClick = { navigator.goBack() },
            onSaveSuccess = {
                // 저장 성공 시 홈으로 이동하거나 뒤로 가기
                // 지금은 뒤로 가기 (카메라 화면 종료)
                navigator.goBack() 
            }
        )
    }
}
