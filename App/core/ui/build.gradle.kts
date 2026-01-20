plugins {
    alias(libs.plugins.commonproject.android.library)
    alias(libs.plugins.commonproject.android.library.compose)
}

android {
    namespace = "com.a602.commonproject.ui"
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)

    implementation(libs.androidx.browser)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}
