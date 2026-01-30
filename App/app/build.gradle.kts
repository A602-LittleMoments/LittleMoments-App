plugins {
    alias(libs.plugins.commonproject.android.application)
    alias(libs.plugins.commonproject.android.application.compose)
    alias(libs.plugins.commonproject.android.application.firebase)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.commonproject.hilt)
}

android {
    namespace = "com.a602.commonproject"
    defaultConfig {
        applicationId = "com.a602.commonproject"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(projects.sync)
    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.navigation)

    implementation(projects.feature.gallery)
    implementation(projects.feature.memory)
    implementation(projects.feature.mypage)
    implementation(projects.feature.home)
    implementation(projects.feature.login)

    ksp(libs.hilt.compiler)


//    implementation(projects.feature.mypage)
//    implementation(projects.feature.gallery)


    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.hilt.ext.work)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.startup)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.navigation3.ui)
}
