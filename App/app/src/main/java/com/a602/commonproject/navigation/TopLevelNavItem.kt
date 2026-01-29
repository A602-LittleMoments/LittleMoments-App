package com.a602.commonproject.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Tablet
import androidx.compose.ui.geometry.Rect
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
    selectedIcon = Icons.Filled.Home,
    unselectedIcon = LMicons.Home,
    iconTextId = R.string.home,
    titleTextId = R.string.app_name
)

val ALBUM = TopLevelNavItem(
    selectedIcon = Icons.Filled.Photo,
    unselectedIcon = LMicons.Photo,
    iconTextId = R.string.gallery,
    titleTextId = R.string.app_name
)

val MEMORY = TopLevelNavItem(
    selectedIcon = Icons.Filled.Star,
    unselectedIcon = Icons.Outlined.Tablet,
    iconTextId = R.string.memory,
    titleTextId = R.string.app_name
)

val MY_PAGE = TopLevelNavItem(
    selectedIcon = Icons.Filled.AccountCircle,
    unselectedIcon = LMicons.Mypage,
    iconTextId = R.string.my_page,
    titleTextId = R.string.app_name
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
    AlbumNavKey to ALBUM,
    MemoryNavKey to MEMORY,
    MyPageNavKey to MY_PAGE
)
