plugins {
    alias(libs.plugins.baseandroid.jvm.library)
    alias(libs.plugins.baseandroid.hilt)
}
dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}
