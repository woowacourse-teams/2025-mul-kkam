# **💧 물깜, 물 깜빡하지 말아요.**

## 서비스 주제

> 물깜(“물 깜빡하지 말아요”)은 **개인 맞춤형 물 섭취 관리**를 돕는 서비스입니다.
>
> 목표 설정 → 기록/알림/위젯 → 통계 확인까지, **하루 습관 형성**에 초점을 맞췄습니다.

[Play Store 이동](https://play.google.com/store/apps/details?id=com.mulkkam)


# 🧱 백엔드
## 인프라 다이어그램
<img width="1368" height="892" alt="image" src="https://github.com/user-attachments/assets/fb610be3-f418-454e-a4c2-fbfb4e575f39" />

# 🤖 안드로이드

## **📌 주요 기능**

- **맞춤 목표 섭취량** 설정 및 진행률 표시
- **원탭 기록**: 홈 화면 **위젯**과 앱 내부 버튼에서 빠르게 기록
- **리마인드 알림**: 사용자 지정 주기로 푸시 알림 · 앱 진입 시 불필요 알림 자동 정리
- **통계 & 히스토리**: 일/주/월 단위 요약
- **선택적 연동**: Health Connect, Kakao 로그인
- **운영 품질**: Firebase Analytics / Crashlytics

---

## **⚙️ 개발 환경**

- **Android Studio**: 최신 Stable (권장: 내장 JDK 사용)
- **JDK**: 21
- **Android 9 (API 28)** 이상 기기/에뮬레이터
- **Gradle**: Version Catalog 기반 종속성 관리

빌드 & 실행:

```
./gradlew installDebug
adb shell am start -n "com.mulkkam/com.mulkkam.ui.splash.SplashActivity"
```

> 일부 기능(Firebase, 외부 API 연동, Kakao 로그인 등)을 위해서는
환경별 설정 값(예: API 키, Base URL)이 필요합니다.
>
> 해당 값은 **개인 로컬 환경 또는 별도 환경 변수 파일**을 통해 주입하세요.

---

## **🏗 아키텍처**

- **Clean Architecture (단일 모듈 내 레이어드 패키징)**
    - **UI**: Activity/Fragment, ViewModel, 위젯, 알림
    - **Data**: Remote + Local + Repository
    - **DI**: object 생성을 통한 수동 의존성 주입 기반 그래프
- **데이터 흐름**: 단방향 데이터 흐름(UDF)
    - 사용자 액션 → ViewModel → Repository → ViewModel 상태 갱신 → UI 반영
- **화면 진입 규약**: 각 Activity는 newIntent(context, …) 팩토리를 제공해, 받는 쪽에서 필요한 데이터를 명시

---

## **🔗 주요 의존성**

- **UI**: Jetpack Compose, Material3, ViewBinding
- **네트워크**: Retrofit, OkHttp, kotlinx.serialization
- **비동기**: Kotlin Coroutines
- **이미지**: Coil 3
- **알림/작업**: WorkManager, Notification API
- **로그인/품질**: Kakao SDK, Firebase Analytics/Crashlytics
- **연동**: Health Connect

(*버전 정보는 libs.versions.toml 참고*)

---

## **🧪 테스트**

- **단위 테스트 중심** (UI/통합 테스트는 진행하지 않음)
- 범위: ViewModel 상태 전이, Repository 변환, Util/규칙 검증
- JUnit5, Kotest, MockK, Coroutines Test 등

실행:

```
./gradlew test
```

---

## **🖼 디자인**

- 앱은 Material 3 가이드를 따릅니다.
- Figma 등 디자인 산출물은 프로젝트 디자인 문서에서 확인 가능합니다.

---

## 💼 컨벤션

- 자세한 컨벤션은 [GitHub Wiki](https://github.com/woowacourse-teams/2025-mul-kkam/wiki/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EC%84%A4%EB%AA%85)를 참고해주세요.

---

## 🪄 이 레포지토리가 도움이 되셨나요?

- 잊지 말고 **스타(⭐️)** 를 눌러주세요!


## 팀원 소개

|<img src="https://github.com/junseo511.png" width="125" />|<img src="https://github.com/hwannow.png" width="125" />|<img src="https://github.com/devfeijoa.png" width="125" />|<img src="https://github.com/CheChe903.png" width="125" />|<img src="https://github.com/2Jin1031.png" width="125" />|<img src="https://github.com/minSsan.png" width="125" />|<img src="https://github.com/Jin409.png" width="125" />|
|:---------:|:---------:|:---------:|:---------:|:---------:|:---------:|:---------:|
|[공백(최준서)](https://github.com/junseo511)|[환노(김은지)](https://github.com/hwannow)|[이든(장은영)](https://github.com/devfeijoa)|[체체(김진영)](https://github.com/CheChe903)|[칼리(이 진)](https://github.com/2Jin1031)|[밍곰(박민선)](https://github.com/minSsan)|[히로(진승희)](https://github.com/Jin409)|
|Android|Android|Android|Backend|Backend|Backend|Backend|
