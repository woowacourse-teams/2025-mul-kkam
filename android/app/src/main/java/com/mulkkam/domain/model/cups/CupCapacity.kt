package com.mulkkam.domain.model.cups

import com.mulkkam.domain.model.result.MulKkamError

@JvmInline
value class CupCapacity(
    val value: Int,
) {
    init {
        check(value in MIN_ML..MAX_ML) { throw MulKkamError.SettingCupsError.InvalidAmount }
    }

    companion object {
        const val MIN_ML = 1
        const val MAX_ML = 2000
    }
}
