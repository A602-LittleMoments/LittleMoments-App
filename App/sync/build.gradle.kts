plugins {
    alias(libs.plugins.baseandroid.android.library)
    alias(libs.plugins.baseandroid.hilt)
}

android {
    namespace = "com.kguard.baseandroid.sync"
}

dependencies {
    implementation(libs.androidx.test.ext)
    ksp(libs.hilt.ext.compiler)

    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)

    implementation(projects.core.data)
    implementation(projects.core.notifications)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.cloud.messaging)

    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlinx.coroutines.guava)

}
