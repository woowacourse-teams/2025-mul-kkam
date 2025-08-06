package com.mulkkam.domain.logger

import com.mulkkam.domain.model.LogEntry
import com.mulkkam.domain.model.LogEvent
import com.mulkkam.domain.model.LogLevel

/**
 * MulKkam 애플리케이션 전역 로깅 인터페이스.
 *
 * ---
 * ## 목적
 * - **안정성 확보**: 앱 동작 중 발생하는 오류 및 경고를 수집
 * - **문제 원인 분석**: Crashlytics, Analytics 등을 통한 장애 원인 추적
 * - **서비스 개선**: 주요 사용자 행동 및 네트워크 상태를 기반으로 UX 개선
 *
 * ---
 * ## 로깅 레벨 정의
 * - [LogLevel.ERROR] : 치명적 오류. 즉시 조치 필요
 *   예) 앱 크래시, NullPointerException, ANR
 *
 * - [LogLevel.WARN] : 잠재적 오류. 추후 개선 필요
 *   예) 네트워크 지연, 불완전한 API 응답
 *
 * - [LogLevel.INFO] : 주요 사용자 이벤트
 *   예) 로그인 성공, 결제 완료, 온보딩 완료
 *
 * - [LogLevel.DEBUG] : 개발용 상세 로그
 *   예) 변수 값, 함수 실행 흐름, 조건 분기 결과
 *
 * ---
 * ## 사용 예시
 * ```kotlin
 * logger.error(LogEvent.NETWORK, "API 응답 실패: timeout")
 * logger.info(LogEvent.USER_AUTH, "로그인 성공", userId = "1234")
 * logger.debug(LogEvent.USER_ACTION, "결제 버튼 클릭")
 * ```
 *
 * ---
 * ## 구현체 예시
 * - Firebase + Timber 기반: `FirebaseTimberLogger`
 * - 로컬 파일 저장 기반: `FileLogger`
 *
 * UI, Data 계층에서는 이 인터페이스만 참조하며,
 * 실제 구현체는 DI를 통해 주입받아야 함.
 */

interface Logger {
    /**
     * 로그를 기록하는 기본 메서드.
     *
     * @param entry 기록할 [LogEntry] 객체
     */
    fun log(entry: LogEntry)

    /**
     * 치명적 오류를 기록합니다.
     */
    fun error(
        event: LogEvent,
        message: String = "",
        userId: String? = null,
    ) = log(LogEntry(LogLevel.ERROR, event, message, userId))

    /**
     * 잠재적 오류를 기록합니다.
     */
    fun warn(
        event: LogEvent,
        message: String = "",
        userId: String? = null,
    ) = log(LogEntry(LogLevel.WARN, event, message, userId))

    /**
     * 주요 사용자 이벤트를 기록합니다.
     */
    fun info(
        event: LogEvent,
        message: String = "",
        userId: String? = null,
    ) = log(LogEntry(LogLevel.INFO, event, message, userId))

    /**
     * 개발용 상세 로그를 기록합니다.
     */
    fun debug(
        event: LogEvent,
        message: String = "",
        userId: String? = null,
    ) = log(LogEntry(LogLevel.DEBUG, event, message, userId))
}
