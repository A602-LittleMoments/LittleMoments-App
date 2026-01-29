//package com.a602.commonproject.feature.mypage.navigation
//
//import androidx.navigation3.runtime.EntryProviderScope
//import androidx.navigation3.runtime.NavKey
//import androidx.navigation3.runtime.entry
//import com.a602.commonproject.navigation.Navigator
//
//// 마이페이지의 모든 화면 입구를 관리하는 함수입니다.
//fun EntryProviderScope<NavKey>.myPageEntryProvider(
//    navigator: Navigator
//) {
//    // 1. 마이페이지 메인
//    entry<MyPageKey> {
//        MyPageMainScreen(
//            onEditProfileClick = { navigator.navigate(ProfileEditKey) },
//            onAddKidClick = { navigator.navigate(KidAddKey) },
//            onBack = { navigator.goBack() }
//        )
//    }
//
//    // 2. 프로필 수정
//    entry<ProfileEditKey> {
//        ProfileEditScreen(
//            onBack = { navigator.goBack() }
//        )
//    }
//
//    // 3. 아이 추가
//    entry<KidAddKey> {
//        KidAddScreen(
//            onBack = { navigator.goBack() }
//        )
//    }
//
//    // 4. 아이 정보 수정
//    entry<KidEditKey> {
//        KidEditScreen(
//            onBack = { navigator.goBack() }
//        )
//    }
//
//    // 5. 그룹(가족) 관리
//    entry<GroupManageKey> {
//        GroupManageScreen(
//            onBack = { navigator.goBack() }
//        )
//    }
