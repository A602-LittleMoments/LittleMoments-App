plugins {
    alias(libs.plugins.baseandroid.android.library)
    alias(libs.plugins.baseandroid.android.room)
    alias(libs.plugins.baseandroid.hilt)
}

android {
    namespace = "com.kguard.baseandroid.database"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
