# 물꼼(MulKkam) 프로젝트 구조 가이드

이 문서는 물꼼 모바일 앱의 프로젝트 구조를 설명합니다.

## 프로젝트 개요

물꼼은 **Kotlin Multiplatform (KMP)** 기반의 물 섭취 추적 앱입니다.

- **플랫폼**: Android, iOS
- **UI 프레임워크**: Compose Multiplatform
- **아키텍처**: 클린 아키텍처 (data → domain ← ui)
- **DI**: Koin
- **네트워크**: Ktor (공통), Retrofit (Android 레거시)
- **빌드 시스템**: Gradle + Version Catalog

---

## 최상위 디렉토리 구조

```
mobile/
├── androidApp/           # Android 애플리케이션 모듈
├── iosApp/               # iOS 애플리케이션 (Xcode 프로젝트)
├── shared/               # KMP 공유 모듈 (핵심 비즈니스 로직)
├── gradle/               # Gradle 설정 및 버전 카탈로그
├── guide/                # 프로젝트 가이드 문서
├── release-notes/        # 릴리즈 노트
├── build.gradle.kts      # 루트 Gradle 빌드 파일
├── settings.gradle.kts   # Gradle 설정
├── gradle.properties     # Gradle 속성
└── code-style.md         # 팀 코드 스타일 가이드
```

---

## shared 모듈 구조

KMP 공유 모듈은 Android와 iOS에서 공통으로 사용하는 코드를 포함합니다.

### Source Sets

```
shared/src/
├── commonMain/           # 플랫폼 공통 코드
├── commonTest/           # 공통 테스트
├── androidMain/          # Android 전용 구현
├── androidDeviceTest/    # Android 디바이스 테스트
├── androidHostTest/      # Android 호스트 테스트
└── iosMain/              # iOS 전용 구현
```

### commonMain 패키지 구조

```
com.mulkkam/
├── data/                 # 데이터 레이어
│   ├── local/            # 로컬 데이터 소스
│   ├── remote/           # 원격 데이터 소스 (API)
│   ├── repository/       # Repository 구현체
│   └── logger/           # 로깅 구현
│
├── domain/               # 도메인 레이어
│   ├── model/            # 도메인 모델 (엔티티, Value Object)
│   │   ├── auth/         # 인증 관련 모델
│   │   ├── bio/          # 생체 정보 모델
│   │   ├── cups/         # 컵 관련 모델
│   │   ├── friend/       # 친구 관련 모델
│   │   ├── intake/       # 섭취 기록 모델
│   │   ├── members/      # 회원 정보 모델
│   │   ├── notification/ # 알림 모델
│   │   ├── reminder/     # 리마인더 모델
│   │   └── result/       # 결과 래퍼 (MulKkamResult, MulKkamError)
│   ├── repository/       # Repository 인터페이스
│   ├── checker/          # 검증 로직 인터페이스
│   └── logger/           # Logger 인터페이스
│
├── ui/                   # UI 레이어
│   ├── auth/             # 인증 (로그인, 스플래시)
│   ├── home/             # 홈 화면
│   ├── history/          # 기록 화면
│   ├── friends/          # 친구 화면
│   ├── setting/          # 설정 화면
│   ├── onboarding/       # 온보딩 플로우
│   ├── notification/     # 알림 화면
│   ├── component/        # 공통 UI 컴포넌트
│   ├── designsystem/     # 디자인 시스템 (Color, Theme, Typography)
│   ├── model/            # UI 상태 모델 (MulKkamUiState)
│   ├── navigation/       # 네비게이션 설정
│   └── util/             # UI 유틸리티
│
├── di/                   # 의존성 주입 모듈
│   ├── CommonDateSourceModule.kt
│   ├── CommonNetworkModule.kt
│   ├── CommonRepositoryModule.kt
│   └── CommonViewModelModule.kt
│
├── MulKkamApp.kt         # 앱 진입점 (Composable)
└── Platform.kt           # 플랫폼 추상화
```

### androidMain 패키지 구조

Android 전용 구현을 포함합니다.

```
com.mulkkam/
├── data/
│   ├── checker/          # Android 전용 검증 구현
│   ├── local/            # SharedPreferences 등
│   ├── remote/           # Retrofit 데이터 소스
│   ├── repository/       # Android 전용 Repository
│   ├── work/             # WorkManager 작업
│   └── logger/           # Timber 로깅 구현
│
├── di/                   # Android 전용 DI 모듈
│   ├── AndroidSharedModule.kt
│   ├── CheckerModule.kt
│   ├── HealthPlatformModule.kt
│   ├── HttpClientEngineModule.kt   # OkHttp 엔진
│   ├── LocalDataSourceModule.kt
│   ├── LoggingModule.kt
│   ├── PreferenceModule.kt
│   ├── RemoteDataSourceModule.kt
│   ├── RepositoryModule.kt
│   ├── WorkerModule.kt
│   └── WorkMangerModule.kt
│
├── ui/                   # Android 전용 UI
│   ├── auth/             # 카카오 로그인 등
│   ├── designsystem/     # Android 전용 디자인
│   ├── home/             # Android 전용 홈 컴포넌트
│   └── util/             # Android UI 유틸리티
│
├── util/
│   └── logger/           # Timber 래퍼
│
└── Platform.android.kt   # Android Platform 구현
```

### iosMain 패키지 구조

iOS 전용 구현을 포함합니다.

```
com.mulkkam/
├── MainViewController.kt   # iOS 메인 뷰 컨트롤러
└── Platform.ios.kt         # iOS Platform 구현
```

---

## androidApp 모듈 구조

Android 앱 모듈은 Android 전용 기능과 UI를 포함합니다.

```
androidApp/src/main/
├── java/com/mulkkam/
│   ├── ui/
│   │   ├── component/            # Android 전용 컴포넌트
│   │   ├── custom/               # 커스텀 뷰
│   │   ├── dialog/               # 다이얼로그
│   │   ├── encyclopedia/         # 백과사전 화면
│   │   ├── friends/              # 친구 화면 (Android)
│   │   ├── history/              # 기록 화면 (Android)
│   │   ├── home/                 # 홈 화면 (Android)
│   │   ├── main/                 # 메인 액티비티
│   │   ├── notification/         # 알림 화면 (Android)
│   │   ├── onboarding/           # 온보딩 (Android)
│   │   ├── pendingfriends/       # 친구 요청 대기
│   │   ├── searchmembers/        # 멤버 검색
│   │   ├── service/              # 백그라운드 서비스
│   │   ├── setting/              # 설정 화면 (Android)
│   │   ├── setting*/             # 세부 설정 화면들
│   │   ├── splash/               # 스플래시 화면
│   │   ├── util/                 # Android UI 유틸리티
│   │   └── widget/               # 앱 위젯
│   │
│   └── MulKkamApplication.kt     # Application 클래스
│
├── res/                          # Android 리소스
└── AndroidManifest.xml           # 매니페스트
```

---

## iosApp 구조

```
iosApp/
├── iosApp/                       # iOS 앱 소스
├── iosApp.xcodeproj/             # Xcode 프로젝트
└── Configuration/                # 빌드 설정
```

---

## 의존성 관리

### Version Catalog (libs.versions.toml)

모든 의존성 버전은 `gradle/libs.versions.toml`에서 중앙 관리합니다.

```toml
[versions]
kotlin = "2.2.0"
ktor = "3.3.3"
koin-core = "4.1.1"
compose-multiplatform = "1.9.3"
# ...

[libraries]
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin-core" }
# ...

[plugins]
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
# ...
```

---

## 주요 기술 스택

| 분야 | 기술 |
|------|------|
| **언어** | Kotlin 2.2.0 |
| **멀티플랫폼** | Kotlin Multiplatform (KMP) |
| **UI** | Compose Multiplatform 1.9.3 |
| **DI** | Koin 4.1.1 |
| **네트워크** | Ktor 3.3.3 (공통), Retrofit (Android) |
| **직렬화** | Kotlinx Serialization |
| **비동기** | Kotlinx Coroutines |
| **날짜/시간** | Kotlinx Datetime |
| **이미지** | Coil 3.x, Lottie |
| **로깅** | Timber (Android) |
| **헬스** | Health Connect (Android) |
| **빌드** | Gradle 8.x, KSP |
| **코드 스타일** | ktlint |

---

## 빌드 설정

### SDK 버전

- **minSdk**: 28 (Android 9.0 Pie)
- **targetSdk**: 36 (Android 16)
- **compileSdk**: 36

### JVM Target

- JVM 21

### 빌드 타입

- **debug**: 디버그 빌드, applicationIdSuffix ".dev"
- **release**: 난독화 및 리소스 축소 활성화

---

## 리소스 관리

### Compose Resources (공통)

```
shared/src/commonMain/composeResources/
├── drawable/         # 이미지, 아이콘
├── values/           # 문자열, 색상 등
└── ...
```

### Android Resources

```
androidApp/src/main/res/
├── drawable/
├── values/
├── layout/
└── ...
```

---

## 참고 문서

- [Kotlin Multiplatform 공식 문서](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin 공식 문서](https://insert-koin.io/docs/reference/koin-mp/kmp/)
- [Ktor 공식 문서](https://ktor.io/docs/welcome.html)
