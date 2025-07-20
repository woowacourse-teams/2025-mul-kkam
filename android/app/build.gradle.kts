import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.junit5)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mulkkam"
    compileSdk =
        libs.versions.targetSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.mulkkam"
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.targetSdk
                .get()
                .toInt()
        versionCode =
            libs.versions.versionCode
                .get()
                .toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] =
            "de.mannodermaus.junit5.AndroidJUnit5Builder"

        buildConfigField("String", "BASE_URL", gradleLocalProperties(rootDir, providers).getProperty("base.url"))
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    // Android Core 및 UI 라이브러리
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)

    // 네트워크 통신 관련 라이브러리
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    // Kotlin 및 비동기 처리 관련 라이브러리
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    // 단위 테스트 의존성
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.junit.jupiter)

    // Instrumented 테스트 의존성 (Android 기기에서 실행)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.kotest.runner.junit5)
    androidTestImplementation(libs.mannodermaus.junit5.core)
    androidTestImplementation(libs.androidx.espresso.core)

    // Instrumented 테스트 런타임 전용 의존성
    androidTestRuntimeOnly(libs.mannodermaus.junit5.runner)

    // 이미지 로딩 및 캐싱 라이브러리
    implementation(libs.glide)

    // 차트 라이브러리
    implementation(libs.mpandroidchart)
}
