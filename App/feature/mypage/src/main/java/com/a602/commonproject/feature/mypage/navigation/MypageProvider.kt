package com.a602.commonproject.feature.mypage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.mypage.GroupChangeContainer
import com.a602.commonproject.feature.mypage.KidAddContainer
import com.a602.commonproject.feature.mypage.KidEditContainer
import com.a602.commonproject.feature.mypage.MyPageMainContainer
import com.a602.commonproject.feature.mypage.ProfileEditContainer
import com.a602.commonproject.navigation.Navigator

fun EntryProviderScope<NavKey>.myPageEntryProvider(
    navigator: Navigator
) {
    // 1. 마이페이지 메인
    entry<MyPageKey> {
        MyPageMainContainer(navigator)
    }

    // 2. 프로필 수정
    entry<ProfileEditKey> {
        ProfileEditContainer(navigator = navigator)
    }

    // 3. 아이 추가
    entry<KidAddKey> {
        KidAddContainer(navigator = navigator)
    }

    // 4. 아이 정보 수정
    entry<KidEditKey> {
        KidEditContainer(navigator = navigator)
    }

    // 5. 그룹(가족) 관리
    entry<GroupManageKey> {
        GroupChangeContainer(navigator = navigator)
    }
}
