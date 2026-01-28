//package com.a602.commonproject.feature.mypage
//
//import androidx.navigation3.NavGraphBuilder
//import androidx.navigation3.composable
//import androidx.navigation3.runtime.NavKey
//import com.a602.commonproject.navigation.Navigator
//import com.a602.commonproject.model.data.SampleData
//
///**
// * 💡 1. 마이페이지 내의 각 화면을 구분하는 '이름표(Key)' 정의
// * 팀의 Nav3 규격에 따라 NavKey를 상속받습니다.
// */
//sealed class MyPageKey : NavKey {
//    object Main : MyPageKey()         // 마이페이지 메인
//    object ProfileEdit : MyPageKey()  // 내 정보 수정
//    object KidEdit : MyPageKey()      // 아이 정보 수정
//    object GroupManage : MyPageKey()  // 그룹 관리
//    object KidAdd : MyPageKey()       // 아이 추가
//}
//
///**
// * 💡 2. 네비게이션 그래프 등록
// * Navigator를 통해 화면 이동 이벤트를 처리합니다.
// */
//fun NavGraphBuilder.myPageScreen(
//    navigator: Navigator
//) {
//    // --- 마이페이지 메인 화면 ---
//    composable<MyPageKey.Main> {
//        MyPageScreen(
//            nickname = SampleData.user.nickname,
//            onNavigateToProfileEdit = { navigator.navigate(MyPageKey.ProfileEdit) },
//            onNavigateToKidEdit = { navigator.navigate(MyPageKey.KidEdit) },
//            onNavigateToGroupManagement = { navigator.navigate(MyPageKey.GroupManage) },
//            onBackClick = { navigator.goBack() }
//        )
//    }
//
//    // --- 내 정보 수정 화면 ---
//    composable<MyPageKey.ProfileEdit> {
//        ProfileEditScreen(
//            user = SampleData.user,
//            onSaveClick = { navigator.goBack() }, // 저장 후 이전 화면으로
//            onBackClick = { navigator.goBack() }
//        )
//    }
//
//    // --- 아이 정보 수정 화면 ---
//    composable<MyPageKey.KidEdit> {
//        KidEditScreen(
//            baby = SampleData.baby,
//            onSaveClick = { navigator.goBack() },
//            onBackClick = { navigator.goBack() }
//        )
//    }
//
//    // --- 그룹 구성원 관리 (상세) 화면 ---
//    composable<MyPageKey.GroupManage> {
//        GroupChangeScreen(
//            members = SampleData.group.members,
//            onBackClick = { navigator.goBack() }
//        )
//    }
//
//    // --- 아이 추가 화면 (필요 시) ---
//    composable<MyPageKey.KidAdd> {
//        KidAddScreen(
//            onSaveClick = { navigator.goBack() },
//            onBackClick = { navigator.goBack() }
//        )
//    }
//}
