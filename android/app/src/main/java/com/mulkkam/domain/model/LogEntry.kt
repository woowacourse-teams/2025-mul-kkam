package com.mulkkam.domain.model

import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * MulKkam 애플리케이션의 로그 데이터 모델.
 *
 * ---
 * ## 필드 설명
 * @property level 로그 레벨 ([LogLevel])
 * @property event 로그 이벤트 유형 ([LogEvent])
 * @property message 사람이 읽을 수 있는 설명
 * @property userId 이벤트 발생 시점의 사용자 ID (게스트일 경우 null)
 * @property timestamp 로그 생성 시각 (UTC ISO-8601 형식)
 * ---
 */
data class LogEntry(
    val level: LogLevel,
    val event: LogEvent,
    val message: String,
    val userId: String? = null,
    val timestamp: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
)
