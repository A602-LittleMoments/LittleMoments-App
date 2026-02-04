package com.a602.commonproject.feature.baby.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object BabyNavKey : NavKey

@Serializable
object AddBabyNavKey : NavKey

@Serializable
data class EditBabyNavKey(val babyId: String) : NavKey
