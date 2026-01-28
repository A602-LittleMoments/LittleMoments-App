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
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)


}
