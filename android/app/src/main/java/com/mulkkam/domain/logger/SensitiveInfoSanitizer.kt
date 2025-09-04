package com.mulkkam.domain.logger

/**
 * 로그 메시지에 포함될 수 있는 민감정보를 마스킹합니다.
 */
fun interface SensitiveInfoSanitizer {
    fun sanitize(input: String): String
}
