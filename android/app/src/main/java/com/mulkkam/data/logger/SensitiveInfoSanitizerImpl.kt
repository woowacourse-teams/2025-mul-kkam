package com.mulkkam.data.logger

import com.mulkkam.domain.logger.LogSanitizer

/**
 * token, access_token, refresh_token, deviceId, Authorization: Bearer ... 형태를 마스킹합니다.
 */
class SensitiveInfoSanitizerImpl(
    private val mask: String = DEFAULT_MASK,
) : LogSanitizer {
    override fun sanitize(input: String): String {
        var out = input

        // JSON 스타일: "token": "....", "deviceId": "...."
        out =
            JSON_TOKEN_REGEX.replace(out) { mr ->
                "\"${mr.groupValues[1]}\": \"$mask\""
            }

        // key=value 또는 key: value (따옴표 없는 값, URL 쿼리에도 적용)
        out =
            KEY_VALUE_REGEX.replace(out) { mr ->
                "${mr.groupValues[1]}=$mask"
            }

        // Authorization: Bearer <...>
        out =
            BEARER_REGEX.replace(out) { mr ->
                "${mr.groupValues[1]}: Bearer $mask"
            }

        // JWT-like 토큰 (eyJ로 시작)
        out =
            JWT_LIKE_REGEX.replace(out) { _ ->
                mask
            }

        // IPv4 주소 (예: 127.0.0.1)
        out =
            IPV4_REGEX.replace(out) { _ ->
                mask
            }

        return out
    }

    companion object {
        private const val DEFAULT_MASK = "***"
        private const val SENSITIVE_KEYS_ALT = "token|access_token|refresh_token|deviceid"

        private val JSON_TOKEN_REGEX =
            Regex(
                pattern = """(?i)"($SENSITIVE_KEYS_ALT)"\s*:\s*"([^"]+)"""",
            )

        private val KEY_VALUE_REGEX =
            Regex(
                pattern = """(?i)\b($SENSITIVE_KEYS_ALT)\b\s*[=:]\s*([^\s&"]+)""",
            )

        private val BEARER_REGEX =
            Regex(
                pattern = """(?i)\b(authorization)\b\s*:\s*bearer\s+([^\s"]+)""",
            )

        private val JWT_LIKE_REGEX =
            Regex(
                pattern = """(?i)\beyJ[\w\-_.]+""",
            )

        private val IPV4_REGEX =
            Regex(
                pattern = """\b(?:(?:25[0-5]|2[0-4]\d|1?\d{1,2})\.){3}(?:25[0-5]|2[0-4]\d|1?\d{1,2})\b""",
            )
    }
}
