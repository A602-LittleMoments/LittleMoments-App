plugins {
    alias(libs.plugins.commonproject.android.feature.api)
    alias(libs.plugins.commonproject.android.feature.impl)
    alias(libs.plugins.commonproject.android.library.compose)
    id("kotlin-parcelize") // 데이터를 화면 간에 전달하기 위한 Parcelize 플러그인을 직접 추가합니다.
}

android {
    namespace = "com.a602.commonproject.feature.mypage"
}

dependencies {
    // 💡 디자인 시스템 모듈 연결 (Color, Typography 등을 위해 필요)
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":feature:login")) // 로그인 모듈 의존성 추가

    // 💡 Compose 필수 라이브러리 추가
    implementation(libs.androidx.core.ktx)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // 버전은 프로젝트에 맞게 조절

    implementation(libs.androidx.activity.compose)
    implementation("io.coil-kt:coil-compose:2.7.0") // 라이브러리 실제 이름으로 수정
}
