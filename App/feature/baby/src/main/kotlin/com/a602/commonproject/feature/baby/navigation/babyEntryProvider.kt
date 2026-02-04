package com.a602.commonproject.feature.baby.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.album.GridNavKey
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
            onNavigateToAddBaby = { navigator.navigate(AddBabyNavKey) },
            onNavigateToEditBaby = { babyId -> navigator.navigate(EditBabyNavKey(babyId)) },
            onNavigateToGallery = { babyId, year ->
                navigator.navigate(GridNavKey(babyId = babyId, year = year))
            }
        )
    }

    entry<AddBabyNavKey> {
        com.a602.commonproject.feature.baby.BabyFormScreen(
            onBackClick = { navigator.goBack() },
            onSaveClick = { _, _, _, _ ->
                // Logic handled in Screen via ViewModel
                navigator.goBack()
            }
        )
    }

    entry<EditBabyNavKey> { args ->
        // Logic handled in Screen via ViewModel (Baby ID passed)
        com.a602.commonproject.feature.baby.BabyFormScreen(
            babyId = args.babyId,
            initialBabyData = null,
            onBackClick = { navigator.goBack() },
            onSaveClick = { _, _, _, _ ->
                // Logic handled in Screen via ViewModel
                navigator.goBack()
            },
            onDeleteClick = {
                 // Logic handled in Screen via ViewModel
                 navigator.goBack()
            }
        )
    }
}
