plugins {
    alias(libs.plugins.commonproject.android.library)
    alias(libs.plugins.commonproject.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.a602.commonproject.data"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {

    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)

    implementation(projects.core.notifications)
    implementation(libs.androidx.paging.common)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
}
