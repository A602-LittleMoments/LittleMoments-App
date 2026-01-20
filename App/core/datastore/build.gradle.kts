plugins {
    alias(libs.plugins.commonproject.android.library)
    alias(libs.plugins.commonproject.hilt)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.a602.commonproject.datastore"
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                // register 대신 create 사용
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    api(libs.androidx.dataStore)
    api(projects.core.model)
    api(libs.protobuf.kotlin.lite)

    implementation(projects.core.common)

    testImplementation(libs.kotlinx.coroutines.test)
}
