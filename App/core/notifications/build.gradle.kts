plugins {
    alias(libs.plugins.baseandroid.android.library)
    alias(libs.plugins.baseandroid.hilt)
}

android {
    namespace = "com.kguard.baseandroid.notifications"

}

dependencies {
    api(projects.core.model)
    implementation(projects.core.common)
    compileOnly(platform(libs.androidx.compose.bom))
}
