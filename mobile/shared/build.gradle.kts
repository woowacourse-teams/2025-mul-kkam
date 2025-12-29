plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.mulkkam"
        compileSdk = 36
        minSdk = 28

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                // Add KMP dependencies here
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Ktor (네트워크)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)

                // Koin (DI)
                implementation(libs.koin.core)

                // Kotlinx
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)

                // 이미지 로딩 및 캐싱 라이브러리
                implementation(libs.coil)
                implementation(libs.coil.compose)
                implementation(libs.coil.svg)
                implementation(libs.coil.network)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                // Ktor Android 엔진
                implementation(libs.ktor.client.okhttp)

                // Koin Android
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.workmanager)

                // Health Connect
                implementation(libs.androidx.health.connect)

                // WorkManager
                implementation(libs.androidx.work.runtime.ktx)

                // Retrofit (for Android legacy data layer)
                implementation(libs.retrofit)
                implementation(libs.retrofit2.kotlinx.serialization.converter)
                implementation(libs.okhttp)
                implementation(libs.okhttp.logging)

                // Firebase
                // TODO: 버전 카탈로그 이전
                implementation("com.google.firebase:firebase-analytics:22.0.0")
                implementation("com.google.firebase:firebase-crashlytics:19.0.0")

                // Timber logging
                implementation(libs.timber)

                // Coil for image loading
                implementation(libs.coil)
                implementation(libs.coil.svg)
                implementation(libs.coil.network)

                // AndroidX Core
                implementation(libs.androidx.core.ktx)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.

                // Ktor iOS 엔진
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
}

tasks
    .matching { task ->
        task.name.startsWith("ktlint") &&
            task.name.contains("SourceSet")
    }.configureEach {
        enabled = false
    }
