plugins {
    alias(libs.plugins.commonproject.android.library)
    alias(libs.plugins.commonproject.android.room)
    alias(libs.plugins.commonproject.hilt)
}

android {
    namespace = "com.a602.commonproject.database"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
