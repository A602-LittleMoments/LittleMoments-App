package com.a602.commonproject.feature.mypage.navigation

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

// 데이터를 전달하지 않는 단순 주소는 @Serializable object로 정의합니다.
@Serializable
object MyPageNavKey : NavKey

@Serializable
object ProfileEditKey : NavKey

// 데이터를 화면 간에 전달해야 하는 Key는 @Serializable과 @Parcelize를 모두 사용합니다.
@Serializable
@Parcelize
data class KidEditKey(val babyId: String) : NavKey, Parcelable

@Serializable
object KidAddKey : NavKey

@Serializable
object GroupManageKey : NavKey

@Serializable
object GroupJoinKey : NavKey

@Serializable
object GroupCreateKey : NavKey
