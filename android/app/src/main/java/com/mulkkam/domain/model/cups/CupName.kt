package com.mulkkam.domain.model.cups

import com.mulkkam.domain.model.result.MulKkamError

@JvmInline
value class CupName(
    val value: String,
) {
    init {
        val trimmed: String = value.trim()

        check(trimmed.length in CUP_NAME_LENGTH_MIN..CUP_NAME_LENGTH_MAX) {
            throw MulKkamError.SettingCupsError.InvalidNicknameLength
        }
        check(trimmed.all { it.isLetterOrDigit() || it.isWhitespace() }) {
            throw MulKkamError.SettingCupsError.InvalidNicknameCharacters
        }
    }

    companion object {
        const val CUP_NAME_LENGTH_MIN = 1
        const val CUP_NAME_LENGTH_MAX = 10
    }
}
