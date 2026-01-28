plugins {
    alias(libs.plugins.commonproject.android.feature.api)
    alias(libs.plugins.commonproject.android.feature.impl)
    alias(libs.plugins.commonproject.android.library.compose)
}

android {
    namespace = "com.a602.commonproject.feature.gallery"
}

dependencies {
    implementation(projects.core.data)
    implementation("io.coil-kt:coil-compose:2.5.0")
    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}
