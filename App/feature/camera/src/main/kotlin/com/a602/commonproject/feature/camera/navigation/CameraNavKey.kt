package com.a602.commonproject.feature.camera.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object CameraNavKey : NavKey

@Serializable
data class UploadNavKey(
    val backUri: String,
    val subLocalUri: String
) : NavKey


