plugins {
    alias(libs.plugins.baseandroid.android.library)
    alias(libs.plugins.baseandroid.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.kguard.baseandroid.data"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    /*
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)

    implementation(projects.core.analytics)
    implementation(projects.core.notifications)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(projects.core.datastoreTest)
    testImplementation(projects.core.testing)
    */

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
}
