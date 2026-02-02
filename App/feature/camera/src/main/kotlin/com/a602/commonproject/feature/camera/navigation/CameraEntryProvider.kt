package com.a602.commonproject.feature.camera.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.camera.CameraScreen
import com.a602.commonproject.feature.camera.UploadScreen

import com.a602.commonproject.navigation.Navigator
import com.a602.commonproject.feature.camera.navigation.UploadNavKey

import com.a602.commonproject.feature.gallery.TempAlbumNavKey
import com.a602.commonproject.feature.gallery.MultiPhotoUploadNavKey
import com.a602.commonproject.feature.gallery.GalleryNavKey

fun EntryProviderScope<NavKey>.cameraEntries(navigator: Navigator) {
    entry<CameraNavKey> {
        CameraScreen(
            onCloseClick = { navigator.goBack() },
            onNavigateToTempAlbum = {
                navigator.navigate(TempAlbumNavKey)
            }
        )
    }

    entry<UploadNavKey> { args ->
        UploadScreen(
            backUri = args.backUri,
            subLocalUri = args.subLocalUri,
            onBackClick = { navigator.goBack() },
            onSaveSuccess = {
                navigator.goBack()
            }
        )
    }


}
