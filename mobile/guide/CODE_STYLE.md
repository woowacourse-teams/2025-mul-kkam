# 물꼼(MulKkam) 코드 스타일 가이드

이 문서는 물꼼 프로젝트의 코드 작성 규칙과 패턴을 설명합니다.  
팀의 `code-style.md`를 기반으로, 실제 코드베이스에서 발견되는 패턴을 정리했습니다.

---

## 아키텍처 패턴

### 레이어 구조

```
┌─────────────┐
│     UI      │  ← Compose UI, ViewModel
├─────────────┤
│   Domain    │  ← 비즈니스 로직, Model, Repository 인터페이스
├─────────────┤
│    Data     │  ← Repository 구현, DataSource, API
└─────────────┘
```

### 의존성 규칙

- **data → domain**: data는 domain을 참조 가능
- **ui → domain**: ui는 domain을 참조 가능
- **data ↛ ui**: data는 ui를 참조 불가
- **ui ↛ data**: ui는 data를 직접 참조 불가
- **domain → 없음**: domain은 다른 레이어를 참조하지 않음

```kotlin
// Good: Repository 인터페이스는 domain에 정의
// domain/repository/IntakeRepository.kt
interface IntakeRepository {
    suspend fun getIntakeHistory(from: LocalDate, to: LocalDate): MulKkamResult<IntakeHistorySummaries>
}

// Good: Repository 구현체는 data에 정의
// data/repository/IntakeRepositoryImpl.kt
class IntakeRepositoryImpl(
    private val intakeRemoteDataSource: IntakeRemoteDataSource,
) : IntakeRepository {
    // 구현
}
```

---

## 네이밍 컨벤션

### 기본 원칙

- **축약어 사용 금지**: 변수나 함수 이름에 축약어를 사용하지 않는다.
- **의도 드러내기**: 동작 방식보다 의도를 드러내는 이름을 선호한다.
- **Boolean 네이밍**: 상태의 의미가 명확히 드러나야 한다.

```kotlin
// Bad
val amt: Int = 100
val isFlag: Boolean = true
fun proc() {}

// Good
val intakeAmount: Int = 100
val isGoalAchieved: Boolean = true
fun processIntakeHistory() {}
```

### 파일명 규칙

| 종류 | 패턴 | 예시 |
|------|------|------|
| ViewModel | `{Feature}ViewModel.kt` | `HomeViewModel.kt` |
| Repository 인터페이스 | `{Feature}Repository.kt` | `IntakeRepository.kt` |
| Repository 구현 | `{Feature}RepositoryImpl.kt` | `IntakeRepositoryImpl.kt` |
| DataSource | `{Feature}{Type}DataSource.kt` | `IntakeRemoteDataSource.kt` |
| Screen (Composable) | `{Feature}Screen.kt` | `HomeScreen.kt` |
| Route (Composable) | `{Feature}Route.kt` | `HomeRoute.kt` |
| DI 모듈 | `{Feature}Module.kt` | `RepositoryModule.kt` |
| 도메인 모델 | `{ModelName}.kt` | `TargetAmount.kt` |

### 패키지 구조 규칙

화면별로 패키지를 분리하고, 관련 파일들을 함께 배치합니다.

```
ui/home/
├── home/
│   ├── HomeRoute.kt
│   ├── HomeScreen.kt
│   ├── HomeViewModel.kt
│   ├── component/
│   │   └── WaterProgressCard.kt
│   └── model/
│       └── HomeUiStateHolder.kt
├── HomeNavGraph.kt
└── ManualDrinkViewModel.kt
```

---

## Kotlin 코딩 규칙

### 타입 명시

멤버 변수와 함수 반환 타입은 명시적으로 선언합니다. 지역 변수는 추론에 맡겨도 됩니다.

```kotlin
// Good: 멤버 변수 타입 명시
private val _todayProgressInfoUiState: MutableStateFlow<MulKkamUiState<TodayProgressInfo>> =
    MutableStateFlow(MulKkamUiState.Success<TodayProgressInfo>(TodayProgressInfo.EMPTY_TODAY_PROGRESS_INFO))

// Good: 함수 반환 타입 명시
val todayProgressInfoUiState: StateFlow<MulKkamUiState<TodayProgressInfo>> 
    get() = _todayProgressInfoUiState.asStateFlow()

// Good: 지역 변수는 추론 허용
val cups = cupsUiState.value.toSuccessDataOrNull() ?: return
```

### 상수 정의

- 하드코딩된 매직 넘버를 지양합니다.
- 상수는 `const val`로 선언합니다.
- 관련 상수는 `companion object`에 정의합니다.

```kotlin
@JvmInline
@Serializable
value class TargetAmount(
    val value: Int,
) {
    init {
        require(value >= TARGET_AMOUNT_MIN) {
            throw MulKkamError.TargetAmountError.BelowMinimum
        }
        require(value <= TARGET_AMOUNT_MAX) {
            throw MulKkamError.TargetAmountError.AboveMaximum
        }
    }

    companion object {
        const val TARGET_AMOUNT_MIN: Int = 200
        const val TARGET_AMOUNT_MAX: Int = 5000

        val EMPTY_TARGET_AMOUNT: TargetAmount = TargetAmount(TARGET_AMOUNT_MIN)
    }
}
```

### Value Class 활용

도메인의 원시 타입을 래핑하여 타입 안전성을 확보합니다.

```kotlin
@JvmInline
@Serializable
value class CupAmount(val value: Int) {
    init {
        require(value in CUP_AMOUNT_MIN..CUP_AMOUNT_MAX)
    }

    companion object {
        const val CUP_AMOUNT_MIN: Int = 10
        const val CUP_AMOUNT_MAX: Int = 2000
    }
}
```

### Result 타입

커스텀 Result 래퍼를 사용하여 성공/실패를 명시적으로 처리합니다.

```kotlin
// domain/model/result/MulKkamResult.kt
class MulKkamResult<T>(
    val data: T? = null,
    val error: MulKkamError? = null,
) {
    fun getOrError(): T {
        return data ?: throw error ?: MulKkamError.UnknownError
    }
}

// 사용 예시
val result = intakeRepository.getIntakeHistory(from, to)
result.fold(
    onSuccess = { data -> /* 성공 처리 */ },
    onFailure = { error -> /* 실패 처리 */ },
)
```

---

## Compose 스타일

### Route/Screen 패턴

상태 관리와 UI 렌더링을 분리합니다.

- **Route**: 상태 소유, 이벤트 핸들링, 사이드 이펙트 처리
- **Screen**: 순수 UI 렌더링

```kotlin
// HomeRoute.kt - 상태를 소유하고 이벤트를 처리
@Composable
fun HomeRoute(
    padding: PaddingValues,
    navigateToNotification: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    val todayProgressInfoUiState by viewModel.todayProgressInfoUiState.collectAsStateWithLifecycle()

    LaunchedEffect(todayProgressInfoUiState) {
        handleTodayProgressFailure(state = todayProgressInfoUiState, ...)
    }

    HomeScreen(
        padding = padding,
        navigateToNotification = navigateToNotification,
        snackbarHostState = snackbarHostState,
        viewModel = viewModel,
    )
}

// HomeScreen.kt - 상태를 전달받아 그리기만 함
@Composable
fun HomeScreen(
    padding: PaddingValues,
    navigateToNotification: () -> Unit,
    snackbarHostState: SnackbarHostState,
    viewModel: HomeViewModel,
) {
    // UI 렌더링만 담당
}
```

### ViewModel 패턴

StateFlow와 SharedFlow를 사용하여 UI 상태를 관리합니다.

```kotlin
class HomeViewModel(
    private val membersRepository: MembersRepository,
    private val cupsRepository: CupsRepository,
) : ViewModel() {
    
    // StateFlow: 지속적인 상태
    private val _todayProgressInfoUiState: MutableStateFlow<MulKkamUiState<TodayProgressInfo>> =
        MutableStateFlow(MulKkamUiState.Success(TodayProgressInfo.EMPTY_TODAY_PROGRESS_INFO))
    val todayProgressInfoUiState: StateFlow<MulKkamUiState<TodayProgressInfo>> 
        get() = _todayProgressInfoUiState.asStateFlow()

    // SharedFlow: 일회성 이벤트
    private val _drinkUiState: MutableSharedFlow<MulKkamUiState<IntakeInfo>> =
        MutableSharedFlow(replay = 0, extraBufferCapacity = 1)
    val drinkUiState: SharedFlow<MulKkamUiState<IntakeInfo>> 
        get() = _drinkUiState.asSharedFlow()

    init {
        loadTodayProgressInfo()
        loadCups()
    }

    fun loadTodayProgressInfo() {
        if (todayProgressInfoUiState.value is MulKkamUiState.Loading) return
        viewModelScope.launch {
            runCatching {
                _todayProgressInfoUiState.value = MulKkamUiState.Loading
                membersRepository.getMembersProgressInfo(...).getOrError()
            }.onSuccess { data ->
                _todayProgressInfoUiState.value = MulKkamUiState.Success(data)
            }.onFailure { error ->
                _todayProgressInfoUiState.value = MulKkamUiState.Failure(error.toMulKkamError())
            }
        }
    }
}
```

### UI 상태 모델

```kotlin
sealed interface MulKkamUiState<out T> {
    object Idle : MulKkamUiState<Nothing>
    object Loading : MulKkamUiState<Nothing>
    data class Success<T>(val data: T) : MulKkamUiState<T>
    data class Failure(val error: MulKkamError) : MulKkamUiState<Nothing>
}
```

### 사이드 이펙트 처리

```kotlin
// LaunchedEffect: 상태 변화에 따른 일회성 작업
LaunchedEffect(todayProgressInfoUiState) {
    handleTodayProgressFailure(state = todayProgressInfoUiState, ...)
}

// collectLatest: Flow 이벤트 수집
LaunchedEffect(viewModel) {
    viewModel.drinkUiState.collectLatest { state ->
        when (state) {
            is MulKkamUiState.Success -> { /* 성공 처리 */ }
            is MulKkamUiState.Failure -> { /* 실패 처리 */ }
            else -> { /* 무시 */ }
        }
    }
}
```

---

## 의존성 주입 (Koin)

### 모듈 구성

```kotlin
// CommonViewModelModule.kt
val commonViewModelModule: Module = module {
    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { SettingViewModel(get(), get()) }
    // ...
}

// CommonRepositoryModule.kt
val commonRepositoryModule: Module = module {
    single<IntakeRepository> { IntakeRepositoryImpl(get()) }
    single<MembersRepository> { MembersRepositoryImpl(get(), get()) }
    // ...
}
```

### Composable에서 ViewModel 주입

```kotlin
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = koinViewModel(),
) {
    // ...
}
```

---

## Repository 패턴

### 인터페이스 정의 (domain)

```kotlin
interface IntakeRepository {
    suspend fun getIntakeHistory(
        from: LocalDate,
        to: LocalDate,
    ): MulKkamResult<IntakeHistorySummaries>

    suspend fun postIntakeHistoryInput(
        dateTime: LocalDateTime,
        intakeType: IntakeType,
        amount: CupAmount,
    ): MulKkamResult<IntakeHistoryResult>
}
```

### 구현체 (data)

```kotlin
class IntakeRepositoryImpl(
    private val intakeRemoteDataSource: IntakeRemoteDataSource,
) : IntakeRepository {
    
    override suspend fun getIntakeHistory(
        from: LocalDate,
        to: LocalDate,
    ): MulKkamResult<IntakeHistorySummaries> {
        val result = intakeRemoteDataSource.getIntakeHistory(
            from.toString(), 
            to.toString()
        )
        return result.fold(
            onSuccess = { response ->
                MulKkamResult(data = IntakeHistorySummaries(response.map { it.toDomain() }))
            },
            onFailure = { error ->
                MulKkamResult(error = error.toDomain())
            },
        )
    }
}
```

---

## 문자열 리소스

출력용 문자열은 Compose Resources를 사용합니다.

```kotlin
// 문자열 리소스 사용
import mulkkam.shared.generated.resources.Res
import mulkkam.shared.generated.resources.manual_drink_success
import org.jetbrains.compose.resources.getString

// 사용
val message = getString(Res.string.manual_drink_success, formattedAmount)
```

---

## 에러 처리

### 에러 모델

```kotlin
sealed class MulKkamError : Throwable() {
    object UnknownError : MulKkamError()
    
    sealed class AccountError : MulKkamError() {
        object Unauthorized : AccountError()
        object TokenExpired : AccountError()
    }
    
    sealed class TargetAmountError : MulKkamError() {
        object BelowMinimum : TargetAmountError()
        object AboveMaximum : TargetAmountError()
    }
    
    // ...
}
```

### 에러 변환

```kotlin
// API 에러를 도메인 에러로 변환
fun Throwable.toDomain(): MulKkamError {
    return when (this) {
        is MulKkamError -> this
        else -> MulKkamError.UnknownError
    }
}
```

---

## 테스트

### 단위 테스트 설정

```kotlin
// JUnit 5 + Kotest + MockK 사용
testImplementation(libs.kotlinx.coroutines.test)
testImplementation(libs.kotest.runner.junit5)
testImplementation(libs.junit.jupiter)
testImplementation(libs.assertj.core)
testImplementation(libs.mockk)
```

### 테스트 원칙

- 구현이 아니라 행위와 시나리오를 검증한다.
- 내부 구현 변경에 쉽게 깨지지 않아야 한다.
- 단위 테스트로 충분한 로직은 단위 테스트로 남긴다.

---

## 코드 포맷팅

### ktlint

프로젝트는 ktlint를 사용하여 코드 스타일을 강제합니다.

```bash
# 코드 스타일 검사
./gradlew ktlintCheck

# 자동 수정
./gradlew ktlintFormat
```

### 들여쓰기

- 4 스페이스 사용
- 줄 길이 제한 없음 (IDE 자동 줄바꿈 활용)

---

## 참고

- 팀의 코드 스타일 철학은 루트의 `code-style.md` 참조
- 리뷰는 지적이 아니라 대화로, 정답보다 다음 선택을 돕는 것이 목적
