plugins {
    alias(libs.plugins.commonproject.android.library)
    alias(libs.plugins.commonproject.hilt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.a602.commonproject.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.savedstate.compose)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)

    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.lifecycle.viewModel.testing)
}
