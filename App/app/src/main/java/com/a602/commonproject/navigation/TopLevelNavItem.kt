package com.a602.commonproject.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.R
import com.a602.commonproject.designsystem.R as DesignSystemR
import com.a602.commonproject.feature.album.GalleryNavKey
import com.a602.commonproject.feature.baby.navigation.BabyNavKey

import com.a602.commonproject.feature.home.navigation.MemoryNavKey
import com.a602.commonproject.feature.mypage.navigation.MyPageNavKey

data class TopLevelNavItem(
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
)

// 1. 앨범 (Gallery)
val ALBUM = TopLevelNavItem(
    selectedIcon = DesignSystemR.drawable.album_select,
    unselectedIcon = DesignSystemR.drawable.album_unselect,
    iconTextId = R.string.gallery,
    titleTextId = R.string.app_name,
)

// 2. 홈 (Memory Key -> Home Role)
val HOME_PAGE = TopLevelNavItem(
    selectedIcon = DesignSystemR.drawable.home_select,
    unselectedIcon = DesignSystemR.drawable.home_unselect,
    iconTextId = R.string.home,
    titleTextId = R.string.app_name,
)

// 3. 아기 (Home Key -> Baby Role)
val BABY_PAGE = TopLevelNavItem(
    selectedIcon = DesignSystemR.drawable.baby_select,
    unselectedIcon = DesignSystemR.drawable.baby_unselect,
    iconTextId = R.string.baby,
    titleTextId = R.string.app_name,
)

// 4. 마이페이지 (MyPage)
val MY_PAGE = TopLevelNavItem(
    selectedIcon = DesignSystemR.drawable.mypage_select,
    unselectedIcon = DesignSystemR.drawable.mypage_unselect,
    iconTextId = R.string.my_page,
    titleTextId = R.string.app_name,
)

// 순서: 앨범 -> 홈 -> 아기 -> 마이페이지
val TOP_LEVEL_NAV_ITEMS = mapOf<NavKey, TopLevelNavItem>(
    GalleryNavKey to ALBUM,
    MemoryNavKey to HOME_PAGE,
    BabyNavKey to BABY_PAGE,
    MyPageNavKey to MY_PAGE,
)
