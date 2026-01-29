package com.a602.commonproject.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.R
import com.a602.commonproject.designsystem.icon.LMicons
import kotlinx.serialization.Serializable

data class TopLevelNavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
)


val HOME = TopLevelNavItem(
    selectedIcon = LMicons.Home_Selected,
    unselectedIcon = LMicons.Home_Unselected,
    iconTextId = R.string.home,
    titleTextId = R.string.app_name,
)

val GALLERY = TopLevelNavItem(
    selectedIcon = LMicons.Gallery_Selected,
    unselectedIcon = LMicons.Gallery_Unselected,
    iconTextId = R.string.gallery,
    titleTextId = R.string.app_name,
)

val MEMORY = TopLevelNavItem(
    selectedIcon = LMicons.Memory_Selected,
    unselectedIcon = LMicons.Memory_Unselected,
    iconTextId = R.string.memory,
    titleTextId = R.string.app_name,
)

val MY_PAGE = TopLevelNavItem(
    selectedIcon = LMicons.my_page_Selected,
    unselectedIcon = LMicons.my_page_Unselected,
    iconTextId = R.string.my_page,
    titleTextId = R.string.app_name,
)

@Serializable
object HomeNavKey : NavKey

@Serializable
object AlbumNavKey : NavKey

@Serializable
object MemoryNavKey : NavKey

@Serializable
object MyPageNavKey : NavKey

val TOP_LEVEL_NAV_ITEMS = mapOf(
    HomeNavKey to HOME,
    AlbumNavKey to GALLERY,
    MemoryNavKey to MEMORY,
    MyPageNavKey to MY_PAGE,
)
