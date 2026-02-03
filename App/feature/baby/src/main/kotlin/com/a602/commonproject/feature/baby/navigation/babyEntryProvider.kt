package com.a602.commonproject.feature.baby.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
//import com.a602.commonproject.feature.gallery.GalleryNavKey
import com.a602.commonproject.feature.baby.BabyRoute
import com.a602.commonproject.feature.camera.navigation.CameraNavKey
import com.a602.commonproject.feature.home.navigation.NotificationNavKey
import com.a602.commonproject.navigation.Navigator

fun EntryProviderScope<NavKey>.babyEntries(navigator: Navigator) {
    entry<BabyNavKey> {
        BabyRoute(
            onNotificationClick = { navigator.navigate(NotificationNavKey) },
            onNavigateToUpload = { navigator.navigate(CameraNavKey) },
//            onNavigateToGallery = { navigator.navigate(GalleryNavKey) }
        )
    }
}
