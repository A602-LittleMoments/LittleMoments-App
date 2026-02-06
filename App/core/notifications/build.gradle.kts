plugins {
    alias(libs.plugins.commonproject.android.library)
    alias(libs.plugins.commonproject.hilt)
}

android {
    namespace = "com.a602.commonproject.notifications"

}

dependencies {
    api(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    compileOnly(platform(libs.androidx.compose.bom))
}
