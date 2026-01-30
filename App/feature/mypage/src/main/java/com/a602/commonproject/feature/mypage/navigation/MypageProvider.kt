package com.a602.commonproject.feature.mypage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.mypage.GroupChangeScreen
import com.a602.commonproject.feature.mypage.KidAddScreen
import com.a602.commonproject.feature.mypage.KidEditScreen
import com.a602.commonproject.feature.mypage.MyPageMainContainer
import com.a602.commonproject.feature.mypage.ProfileEditScreen
import com.a602.commonproject.model.data.Baby
import com.a602.commonproject.model.data.User
import com.a602.commonproject.navigation.Navigator

fun EntryProviderScope<NavKey>.myPageEntryProvider(
    navigator: Navigator
) {
    // 1. 마이페이지 메인
    entry<MyPageKey> {
        MyPageMainContainer()
    }

    // 2. 프로필 수정
    entry<ProfileEditKey> {
        ProfileEditScreen(
            user = User(id = "", email = "", nickname = ""),
            onBackClick = { navigator.goBack() }
        )
    }

    // 3. 아이 추가
    entry<KidAddKey> {
        KidAddScreen(
            onBackClick = { navigator.goBack() }
        )
    }

    // 4. 아이 정보 수정
    entry<KidEditKey> { key ->
        KidEditScreen(
            baby = Baby(babyId = key.babyId, babyName = "", birthDate = "", gender = Baby.Gender.MALE, imageUrl = null),
            onBackClick = { navigator.goBack() }
        )
    }

    // 5. 그룹(가족) 관리
    entry<GroupManageKey> {
        GroupChangeScreen(
            members = emptyList(),
            onBackClick = { navigator.goBack() }
        )
    }
}
