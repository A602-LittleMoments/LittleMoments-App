plugins {
    alias(libs.plugins.commonproject.android.feature.api)
    alias(libs.plugins.commonproject.android.feature.impl)
    alias(libs.plugins.commonproject.android.library.compose)
}

android {
    namespace = "com.a602.commonproject.login"
}

dependencies {
    implementation(projects.core.data)
    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}
