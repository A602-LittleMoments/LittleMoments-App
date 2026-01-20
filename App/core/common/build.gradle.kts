plugins {
    alias(libs.plugins.commonproject.jvm.library)
    alias(libs.plugins.commonproject.hilt)
}
dependencies {
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
}
