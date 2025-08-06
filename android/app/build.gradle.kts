import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.junit5)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.firebase.crashlytics)
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

        buildConfigField(
            "String",
            "BASE_URL",
            gradleLocalProperties(rootDir, providers).getProperty("base.url"),
        )

        val kakaoKey =
            gradleLocalProperties(rootDir, providers).getProperty("key.kakao") ?: ""
        buildConfigField("String", "KEY_KAKAO", "\"$kakaoKey\"")

        manifestPlaceholders["kakaoKey"] = kakaoKey
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
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockk)

    // 이미지 로딩 및 캐싱 라이브러리
    implementation(libs.glide)
    implementation(libs.lottie)

    // 로그인
    implementation(libs.kakao.v2.user)

    // 파이어베이스
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    // 로깅
    implementation(libs.timber)

    // 헬스 커넥트
    implementation(libs.androidx.health.connect)

    // 워크 매니저
    implementation(libs.androidx.work.runtime.ktx)
}
