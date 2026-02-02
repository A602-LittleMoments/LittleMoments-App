plugins {
    alias(libs.plugins.commonproject.android.feature.api)
    alias(libs.plugins.commonproject.android.feature.impl)
    alias(libs.plugins.commonproject.android.library.compose)
    alias(libs.plugins.commonproject.hilt)
}

android {
    namespace = "com.a602.commonproject.feature.home"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.feature.camera)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

}
