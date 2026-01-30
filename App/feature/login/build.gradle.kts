plugins {
    alias(libs.plugins.commonproject.android.feature.api)
    alias(libs.plugins.commonproject.android.feature.impl)
    alias(libs.plugins.commonproject.android.library.compose)
    alias(libs.plugins.commonproject.hilt)
}

android {
    namespace = "com.a602.commonproject.login"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.core.navigation)
    implementation(projects.core.common)

    implementation(libs.androidx.compose.ui.tooling.preview)

    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
//    implementation(libs.androidx.hilt.navigation.compose)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.cloud.messaging)

}
