package com.a602.commonproject.feature.home.navigation

import androidx.compose.material3.Text
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
//import com.a602.commonproject.feature.gallery.GalleryNavKey
import com.a602.commonproject.feature.home.HomeRoute
import com.a602.commonproject.feature.home.NotificationScreen
import com.a602.commonproject.navigation.Navigator
import com.a602.commonproject.feature.camera.navigation.CameraNavKey


fun EntryProviderScope<NavKey>.homeEntries(navigator: Navigator) {
    entry<HomeNavKey> {
        HomeRoute(
            onNotificationClick = { navigator.navigate(NotificationNavKey) },
            onNavigateToUpload = { navigator.navigate(CameraNavKey) },
//            onNavigateToGallery = { navigator.navigate(GalleryNavKey) }
        )
    }

    entry<NotificationNavKey> {
        NotificationScreen(
            onBackClick = { navigator.goBack() }
        )
    }


}
