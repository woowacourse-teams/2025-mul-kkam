package com.mulkkam.domain.model

import com.mulkkam.domain.model.result.MulKkamError

@JvmInline
value class Nickname(
    val name: String,
) {
    init {
        check(name.length in NICKNAME_LENGTH_MIN..NICKNAME_LENGTH_MAX) {
            throw MulKkamError.NicknameError.InvalidLength
        }
        check(name.all { it.isLetterOrDigit() }) {
            throw MulKkamError.NicknameError.InvalidCharacters
        }
    }

    companion object {
        private const val NICKNAME_LENGTH_MIN: Int = 2
        private const val NICKNAME_LENGTH_MAX: Int = 10
    }
}
