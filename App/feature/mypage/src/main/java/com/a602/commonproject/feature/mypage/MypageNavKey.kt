package com.a602.commonproject.feature.mypage

import kotlinx.serialization.Serializable

// 1. 마이페이지 기능의 각 화면에 대한 '스마트 주소'들을 정의합니다.
//    @Serializable 어노테이션은 이 주소들이 안전하게 전달될 수 있도록 보장합니다.

@Serializable
object MyPageKey

@Serializable
object ProfileEditKey

@Serializable
object KidEditKey

@Serializable
object KidAddKey

@Serializable
object GroupManageKey
