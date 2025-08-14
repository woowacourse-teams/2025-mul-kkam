package com.mulkkam.domain.model.cups

import com.mulkkam.domain.model.result.MulKkamError

@JvmInline
value class CupName(
    val value: String,
) {
    init {
        check(value.trim().length in CUP_NAME_LENGTH_MIN..CUP_NAME_LENGTH_MAX) {
            throw MulKkamError.SettingCupsError.InvalidNickname
        }
        check(value.trim().all { it.isLetterOrDigit() }) {
            throw MulKkamError.SettingCupsError.InvalidNickname
        }
    }

    companion object {
        const val CUP_NAME_LENGTH_MIN = 1
        const val CUP_NAME_LENGTH_MAX = 10
    }
}
