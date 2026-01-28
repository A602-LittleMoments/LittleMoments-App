package com.a602.commonproject.navigation

import androidx.annotation.StringRes
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
    selectedIcon = LMicons.Home,
    unselectedIcon = LMicons.Home,
    iconTextId = R.string.home,
    titleTextId = R.string.app_name
)

val ALBUM = TopLevelNavItem(
    selectedIcon = LMicons.Photo,
    unselectedIcon = LMicons.Photo,
    iconTextId = R.string.album,
    titleTextId = R.string.app_name
)

val MEMORY = TopLevelNavItem(
    selectedIcon = LMicons.Star,
    unselectedIcon = LMicons.Star,
    iconTextId = R.string.memory,
    titleTextId = R.string.app_name
)

val MY_PAGE = TopLevelNavItem(
    selectedIcon = LMicons.Mypage,
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
