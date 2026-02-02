package com.a602.commonproject.feature.mypage.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.a602.commonproject.feature.mypage.GroupChangeContainer
import com.a602.commonproject.feature.mypage.GroupCreateContainer
import com.a602.commonproject.feature.mypage.GroupJoinContainer
import com.a602.commonproject.feature.mypage.KidAddContainer
import com.a602.commonproject.feature.mypage.KidEditContainer
import com.a602.commonproject.feature.mypage.MyPageMainContainer
import com.a602.commonproject.feature.mypage.ProfileEditContainer
import com.a602.commonproject.navigation.Navigator

fun EntryProviderScope<NavKey>.myPageEntries(
    navigator: Navigator
) {
    // 1. 마이페이지 메인
    entry<MyPageNavKey> {
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
    entry<KidEditKey> { key ->
        KidEditContainer(navigator = navigator, key = key)
    }

    // 5. 그룹(가족) 관리
    entry<GroupManageKey> {
        GroupChangeContainer(navigator = navigator)
    }

    // 6. 그룹 생성
    entry<GroupCreateKey> {
        GroupCreateContainer(navigator = navigator)
    }

    // 7. 그룹 참여
    entry<GroupJoinKey> {
        GroupJoinContainer(navigator = navigator)
    }
}
