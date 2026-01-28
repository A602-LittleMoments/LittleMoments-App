plugins {
    alias(libs.plugins.commonproject.android.feature.api)
    alias(libs.plugins.commonproject.android.feature.impl)
    alias(libs.plugins.commonproject.android.library.compose)
}

android {
    namespace = "com.a602.commonproject.feature.memory"
}

dependencies {
    // 💡 디자인 시스템 모듈 연결 (Color, Typography 등을 위해 필요)
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:model"))

    // 💡 Compose 필수 라이브러리 추가
    implementation(libs.androidx.core.ktx)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)

}
