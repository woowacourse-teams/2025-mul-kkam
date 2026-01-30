# 물꼼(MulKkam) 개발 컨벤션 가이드

이 문서는 물꼼 프로젝트의 개발 컨벤션과 실천적인 가이드라인을 제공합니다.

---

## Git 컨벤션

### 브랜치 전략

```
main                 # 프로덕션 릴리즈
├── develop          # 개발 통합
├── feature/*        # 새로운 기능
├── fix/*            # 버그 수정
├── refactor/*       # 리팩토링
└── hotfix/*         # 긴급 수정
```

### 커밋 메시지

```
<type>: <subject>

<body>
```

**Type 종류**:
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `refactor`: 리팩토링
- `style`: 코드 스타일 변경 (포매팅 등)
- `docs`: 문서 변경
- `test`: 테스트 추가/수정
- `chore`: 빌드, 설정 등 기타 변경

---

## UI 화면 추가 가이드

새로운 화면을 추가할 때 따라야 할 단계입니다.

### 1. 도메인 모델 정의

필요한 도메인 모델을 `domain/model/` 하위에 정의합니다.

```kotlin
// domain/model/example/ExampleData.kt
data class ExampleData(
    val id: Long,
    val name: String,
    val value: Int,
)
```

### 2. Repository 인터페이스 정의

`domain/repository/`에 인터페이스를 정의합니다.

```kotlin
// domain/repository/ExampleRepository.kt
interface ExampleRepository {
    suspend fun getExampleData(): MulKkamResult<ExampleData>
    suspend fun saveExampleData(data: ExampleData): MulKkamResult<Unit>
}
```

### 3. Repository 구현체 작성

`data/repository/`에 구현체를 작성합니다.

```kotlin
// data/repository/ExampleRepositoryImpl.kt
class ExampleRepositoryImpl(
    private val exampleRemoteDataSource: ExampleRemoteDataSource,
) : ExampleRepository {
    override suspend fun getExampleData(): MulKkamResult<ExampleData> {
        val result = exampleRemoteDataSource.fetchExample()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
```

### 4. DI 모듈 등록

```kotlin
// di/CommonRepositoryModule.kt
val commonRepositoryModule: Module = module {
    // 기존 정의들...
    single<ExampleRepository> { ExampleRepositoryImpl(get()) }
}
```

### 5. ViewModel 작성

```kotlin
// ui/example/ExampleViewModel.kt
class ExampleViewModel(
    private val exampleRepository: ExampleRepository,
) : ViewModel() {
    
    private val _uiState: MutableStateFlow<MulKkamUiState<ExampleData>> =
        MutableStateFlow(MulKkamUiState.Idle)
    val uiState: StateFlow<MulKkamUiState<ExampleData>> = _uiState.asStateFlow()

    fun loadData() {
        if (uiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _uiState.value = MulKkamUiState.Loading
                exampleRepository.getExampleData().getOrError()
            }.onSuccess { data ->
                _uiState.value = MulKkamUiState.Success(data)
            }.onFailure { error ->
                _uiState.value = MulKkamUiState.Failure(error.toMulKkamError())
            }
        }
    }
}
```

### 6. ViewModel DI 등록

```kotlin
// di/CommonViewModelModule.kt
val commonViewModelModule: Module = module {
    // 기존 정의들...
    viewModel { ExampleViewModel(get()) }
}
```

### 7. Screen/Route Composable 작성

```kotlin
// ui/example/ExampleRoute.kt
@Composable
fun ExampleRoute(
    onNavigateBack: () -> Unit,
    viewModel: ExampleViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    
    ExampleScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
    )
}

// ui/example/ExampleScreen.kt
@Composable
fun ExampleScreen(
    uiState: MulKkamUiState<ExampleData>,
    onNavigateBack: () -> Unit,
) {
    when (uiState) {
        is MulKkamUiState.Loading -> LoadingIndicator()
        is MulKkamUiState.Success -> ExampleContent(data = uiState.data)
        is MulKkamUiState.Failure -> ErrorContent(error = uiState.error)
        is MulKkamUiState.Idle -> Unit
    }
}
```

### 8. Navigation 등록

```kotlin
// ui/navigation/MainNavHost.kt
NavHost(...) {
    // 기존 destination들...
    composable(route = "example") {
        ExampleRoute(
            onNavigateBack = { navigator.navigateUp() },
        )
    }
}
```

---

## API 연동 가이드

### DataSource 정의

```kotlin
// data/remote/datasource/ExampleRemoteDataSource.kt
class ExampleRemoteDataSource(
    private val httpClient: HttpClient,
) {
    suspend fun fetchExample(): Result<ExampleResponse> {
        return runCatching {
            httpClient.get("api/example").body()
        }
    }
    
    suspend fun postExample(request: ExampleRequest): Result<Unit> {
        return runCatching {
            httpClient.post("api/example") {
                setBody(request)
            }
        }
    }
}
```

### Request/Response 모델

```kotlin
// data/remote/model/request/ExampleRequest.kt
@Serializable
data class ExampleRequest(
    val name: String,
    val value: Int,
)

// data/remote/model/response/ExampleResponse.kt
@Serializable
data class ExampleResponse(
    val id: Long,
    val name: String,
    val value: Int,
) {
    fun toDomain(): ExampleData = ExampleData(
        id = id,
        name = name,
        value = value,
    )
}
```

---

## 공통 컴포넌트 사용

### 디자인 시스템

```kotlin
// 테마 적용
MulKkamTheme {
    // UI 컴포넌트들
}

// 색상 사용
import com.mulkkam.ui.designsystem.White
import com.mulkkam.ui.designsystem.Primary

// 타이포그래피 사용
Text(
    text = "제목",
    style = MulKkamTypography.headlineLarge,
)
```

### 공통 UI 컴포넌트

프로젝트의 공통 컴포넌트는 `ui/component/`에 위치합니다.

```kotlin
// 스낵바 표시
snackbarHostState.showMulKkamSnackbar(
    message = getString(Res.string.error_message),
    iconResource = Res.drawable.ic_alert_circle,
)

// 액션 스낵바 표시
snackbarHostState.showMulKkamActionSnackbar(
    message = getString(Res.string.success_message),
    iconResource = Res.drawable.ic_check,
    actionLabel = getString(Res.string.action_label),
    onActionPerformed = { /* 액션 처리 */ },
)
```

---

## 문자열 리소스 추가

### Compose Resources 사용

문자열 리소스는 `shared/src/commonMain/composeResources/values/`에 정의합니다.

```xml
<!-- values/strings.xml -->
<resources>
    <string name="example_title">예시 제목</string>
    <string name="example_message">%s님, 환영합니다!</string>
</resources>
```

### 사용 방법

```kotlin
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.example_title
import mulkkam.shared.generated.resources.example_message
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

// Composable 내에서
Text(text = stringResource(Res.string.example_title))

// 비 Composable에서
val message = getString(Res.string.example_message, userName)
```

---

## 플랫폼별 구현 (Native Integration)

물꼼 프로젝트에서 네이티브 기능을 연동하는 두 가지 핵심 패턴을 설명합니다.

### 패턴 1: 콜백을 통한 네이티브 기능 연동

**네이티브 플랫폼의 SDK나 API를 Compose UI에서 호출해야 하는 경우** 사용합니다.  
대표적인 예시로 소셜 로그인(카카오, 애플)이 있습니다.

#### 구조 개요

```
┌─────────────────────────────────────────────────────────────────────────┐
│  Native Platform (Activity / ViewController)                            │
│  - 네이티브 SDK 호출 (카카오, 애플 로그인 등)                               │
│  - 콜백으로 결과 전달                                                     │
└─────────────────────────────────────────────────────────────────────────┘
           │
           │ onLogin 콜백 전달
           ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  MulKkamApp (Composable)                                                │
│  - 앱의 Compose 진입점                                                   │
│  - 콜백을 NavHost로 전달                                                 │
└─────────────────────────────────────────────────────────────────────────┘
           │
           │ 콜백 전달
           ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  MainNavHost → AuthNavGraph → LoginRoute                                │
│  - 최종적으로 LoginScreen에서 콜백 호출                                   │
│  - ViewModel에서 결과 처리                                               │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 실제 구현 예시: 카카오 로그인

**1단계: 콜백 타입 정의 (commonMain)**

```kotlin
// ui/auth/login/model/AuthPlatform.kt
enum class AuthPlatform {
    KAKAO,
    APPLE,
}
```

**2단계: 앱 진입점에서 콜백 파라미터 정의 (commonMain)**

```kotlin
// MulKkamApp.kt
@Composable
fun MulKkamApp(
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
) {
    val navigator = rememberMainNavigator()
    
    MulKkamTheme {
        Scaffold(...) { innerPadding ->
            MainNavHost(
                navigator = navigator,
                padding = innerPadding,
                onLogin = onLogin,  // 콜백 전달
            )
        }
    }
}
```

**3단계: 콜백을 Navigation Graph로 전달 (commonMain)**

```kotlin
// ui/navigation/MainNavHost.kt
@Composable
fun MainNavHost(
    navigator: MainNavigator,
    padding: PaddingValues,
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
) {
    NavDisplay(
        backStack = navigator.backStack,
        entryProvider = { route ->
            when (route) {
                is AuthRoute -> {
                    AuthNavGraph.entryProvider(
                        route = route,
                        padding = padding,
                        navigator = navigator,
                        onLogin = onLogin,  // AuthNavGraph로 전달
                    )
                }
                // ...
            }
        },
    )
}
```

**4단계: Android에서 콜백 구현 (androidApp)**

```kotlin
// androidApp/.../ui/splash/SplashActivity.kt
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MulKkamApp(onLogin = ::login)  // 콜백 연결
        }
    }

    // 네이티브 로그인 로직 구현
    fun login(
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        when (authPlatform) {
            AuthPlatform.KAKAO -> loginWithKakao(onSuccess, onError)
            else -> Unit
        }
    }

    private fun loginWithKakao(
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            loginWithKakaoTalk(onSuccess, onError)
        } else {
            loginWithKakaoAccount(onSuccess, onError)
        }
    }

    private fun loginWithKakaoTalk(
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) {
        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            when {
                error is ClientError && error.reason == ClientErrorCause.Cancelled -> Unit
                error != null -> loginWithKakaoAccount(onSuccess, onError)
                else -> token?.let { onSuccess(it.accessToken) }
            }
        }
    }
}
```

**5단계: iOS에서 콜백 구현 (iosMain)**

```kotlin
// shared/src/iosMain/.../MainViewController.kt
@Suppress("FunctionName")
fun MainViewController(
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
) = ComposeUIViewController { MulKkamApp(onLogin) }
```

iOS 네이티브 코드(Swift)에서 `MainViewController`를 호출할 때 `onLogin` 콜백을 전달합니다:

```swift
// iOS 앱의 진입점 (Swift)
let viewController = MainViewControllerKt.MainViewController(
    onLogin: { authPlatform, onSuccess, onError in
        switch authPlatform {
        case .kakao:
            KakaoLoginManager.login { token in
                onSuccess(token)
            } onError: { error in
                onError(error.localizedDescription)
            }
        case .apple:
            // Apple 로그인 구현
        }
    }
)
```

---

### 패턴 2: expect/actual을 통한 플랫폼별 Composable 구현

**플랫폼별로 다른 UI 구현이 필요한 경우** 사용합니다.  
같은 화면이지만 Android와 iOS에서 다르게 동작해야 하는 경우에 적합합니다.

#### 구조 개요

```
commonMain/                          androidMain/                    iosMain/
┌─────────────────────┐              ┌─────────────────────┐        ┌─────────────────────┐
│ LoginRoute.kt       │              │ LoginRoute.android.kt│        │ LoginRoute.ios.kt   │
│ (expect 선언)        │     →       │ (actual 구현)        │        │ (actual 구현)        │
└─────────────────────┘              └─────────────────────┘        └─────────────────────┘
```

#### 실제 구현 예시: LoginRoute

**commonMain에서 expect 선언**

```kotlin
// shared/src/commonMain/.../ui/auth/login/LoginRoute.kt
package com.mulkkam.ui.auth.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.mulkkam.ui.auth.login.model.AuthPlatform
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun LoginRoute(
    padding: PaddingValues,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMain: () -> Unit,
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
)
```

**androidMain에서 actual 구현**

```kotlin
// shared/src/androidMain/.../ui/auth/login/LoginRoute.android.kt
package com.mulkkam.ui.auth.login

@Composable
actual fun LoginRoute(
    padding: PaddingValues,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMain: () -> Unit,
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    viewModel: LoginViewModel,
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()

    LaunchedEffect(authUiState) {
        when (val state = authUiState) {
            is MulKkamUiState.Success<UserAuthState> -> {
                navigateToNextScreen(state.data, onNavigateToOnboarding, onNavigateToMain)
            }
            is MulKkamUiState.Failure -> {
                snackbarHostState.showMulKkamSnackbar(...)
            }
            else -> Unit
        }
    }

    LoginScreen(
        padding = padding,
        onLoginClick = { authPlatform ->
            val onSuccess: (token: String) -> Unit = when (authPlatform) {
                AuthPlatform.KAKAO -> viewModel::loginWithKakao
                else -> { _ -> }
            }
            onLogin(authPlatform, onSuccess, { error -> /* 에러 로깅 */ })
        },
        snackbarHostState = snackbarHostState,
        isLoginLoading = authUiState is MulKkamUiState.Loading,
    )
}
```

**iosMain에서 actual 구현**

```kotlin
// shared/src/iosMain/.../ui/auth/login/LoginRoute.ios.kt
package com.mulkkam.ui.auth.login

@Composable
actual fun LoginRoute(
    padding: PaddingValues,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMain: () -> Unit,
    onLogin: (
        authPlatform: AuthPlatform,
        onSuccess: (token: String) -> Unit,
        onError: (errorMessage: String) -> Unit,
    ) -> Unit,
    viewModel: LoginViewModel,
) {
    // iOS 전용 구현
    // - Apple 로그인 버튼 표시
    // - iOS 특화 UI 처리
}
```

#### 파일 명명 규칙

| Source Set | 파일명 패턴 | 예시 |
|------------|------------|------|
| commonMain | `{Name}.kt` | `LoginRoute.kt` |
| androidMain | `{Name}.android.kt` | `LoginRoute.android.kt` |
| iosMain | `{Name}.ios.kt` | `LoginRoute.ios.kt` |

---

### 패턴 3: 플랫폼별 DI 모듈

플랫폼별로 다른 구현체를 주입해야 하는 경우입니다.

#### 예시: HTTP Client Engine

```kotlin
// androidMain - HttpClientEngineModule.kt
val httpClientEngineModule = module {
    single { OkHttp.create() }  // Android: OkHttp 엔진
}

// iosMain - HttpClientEngineModule.kt
val httpClientEngineModule = module {
    single { Darwin.create() }  // iOS: Darwin 엔진
}
```

#### 예시: Logger 구현

```kotlin
// commonMain - Logger 인터페이스
// domain/logger/Logger.kt
interface Logger {
    fun info(event: LogEvent, message: String)
    fun error(event: LogEvent, message: String)
}

// androidMain - Timber 기반 구현
// data/logger/TimberLogger.kt
class TimberLogger : Logger {
    override fun info(event: LogEvent, message: String) {
        Timber.tag(event.name).i(message)
    }
    // ...
}

// di/LoggingModule.kt
val loggingModule = module {
    single<Logger> { TimberLogger() }
}
```

---

### 패턴 4: 플랫폼 분기 처리

간단한 플랫폼 분기가 필요한 경우 `expect`/`actual` 속성을 사용합니다.

```kotlin
// commonMain/kotlin/com/mulkkam/Platform.kt
expect val platform: String

// androidMain/kotlin/com/mulkkam/Platform.android.kt
actual val platform: String = "Android"

// iosMain/kotlin/com/mulkkam/Platform.ios.kt
actual val platform: String = "iOS"
```

---

### 언제 어떤 패턴을 사용해야 하는가?

| 상황 | 권장 패턴 |
|------|----------|
| 네이티브 SDK 호출 필요 (카카오, 애플 로그인 등) | **패턴 1: 콜백** |
| 같은 화면이지만 플랫폼별 UI가 다름 | **패턴 2: expect/actual Composable** |
| 플랫폼별 라이브러리 구현체가 다름 | **패턴 3: 플랫폼별 DI 모듈** |
| 단순 값이나 설정 분기 | **패턴 4: expect/actual 속성** |

---

### 새로운 네이티브 기능 추가 시 체크리스트

1. [ ] 네이티브 SDK가 필요한가? → 콜백 패턴 사용
2. [ ] 공통 인터페이스 정의 (commonMain)
3. [ ] Android 구현 (androidMain 또는 androidApp)
4. [ ] iOS 구현 (iosMain 또는 Swift)
5. [ ] 콜백/구현체 연결 확인
6. [ ] 에러 처리 및 로깅 추가

---

## 테스트 작성 가이드

### 단위 테스트 위치

```
shared/src/
├── androidHostTest/kotlin/    # Android 호스트에서 실행되는 테스트
├── androidDeviceTest/kotlin/  # Android 디바이스에서 실행되는 테스트
└── commonTest/kotlin/         # 공통 테��트
```

### 테스트 작성 예시

```kotlin
class IntakeRepositoryTest {
    
    private val mockDataSource: IntakeRemoteDataSource = mockk()
    private val repository = IntakeRepositoryImpl(mockDataSource)
    
    @Test
    fun `섭취 기록 조회 성공 시 데이터를 반환한다`() = runTest {
        // given
        val from = LocalDate(2024, 1, 1)
        val to = LocalDate(2024, 1, 7)
        coEvery { mockDataSource.getIntakeHistory(any(), any()) } returns Result.success(listOf())
        
        // when
        val result = repository.getIntakeHistory(from, to)
        
        // then
        assertThat(result.data).isNotNull()
    }
}
```

---

## 빌드 및 실행

### Android 빌드

```bash
# 디버그 빌드
./gradlew :androidApp:assembleDebug

# 릴리즈 빌드
./gradlew :androidApp:assembleRelease

# 테스트 실행
./gradlew :androidApp:testDebugUnitTest
```

### 공유 모듈 빌드

```bash
# 공유 모듈 빌드
./gradlew :shared:build

# 공유 모듈 테스트
./gradlew :shared:allTests
```

### iOS 빌드

Xcode에서 `iosApp.xcodeproj`를 열고 빌드합니다.

---

## 코드 리뷰 체크리스트

### 아키텍처

- [ ] 레이어 간 의존성 규칙을 준수하는가?
- [ ] Repository 패턴이 올바르게 적용되었는가?
- [ ] DI 모듈에 적절히 등록되었는가?

### 코드 품질

- [ ] 네이밍이 의도를 명확히 드러내는가?
- [ ] 타입이 명시적으로 선언되었는가?
- [ ] 매직 넘버가 상수로 분리되었는가?
- [ ] 문자열이 리소스로 분리되었는가?

### Compose UI

- [ ] Route/Screen 패턴이 적용되었는가?
- [ ] 상태 관리가 적절한가?
- [ ] 사이드 이펙트가 올바르게 처리되었는가?
- [ ] Preview가 제공되는가?

### 에러 처리

- [ ] 모든 에러 케이스가 처리되었는가?
- [ ] 사용자에게 적절한 피드백이 제공되는가?
- [ ] 에러 로깅이 되어 있는가?

---

## 문제 해결

### 빌드 오류

```bash
# Gradle 캐시 삭제
./gradlew clean

# Kotlin 캐시 삭제
rm -rf ~/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlin/

# 프로젝트 동기화
./gradlew --refresh-dependencies
```

### KMP 관련 이슈

- iOS 빌드 실패 시: Xcode에서 Clean Build Folder 실행
- 리소스 생성 실패 시: `./gradlew generateComposeResClass` 실행

---

## 유용한 링크

- [Kotlin Multiplatform 문서](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform 문서](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin 문서](https://insert-koin.io/docs/reference/koin-mp/kmp/)
- [Ktor 문서](https://ktor.io/docs/welcome.html)
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- [Kotlinx Coroutines](https://kotlinlang.org/docs/coroutines-guide.html)
