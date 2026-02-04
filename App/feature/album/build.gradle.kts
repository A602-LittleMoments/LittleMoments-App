plugins {
    alias(libs.plugins.commonproject.android.feature.api)
    alias(libs.plugins.commonproject.android.feature.impl)
    alias(libs.plugins.commonproject.android.library.compose)
}

android {
    namespace = "com.a602.commonproject.feature.album"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.sync)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

}
