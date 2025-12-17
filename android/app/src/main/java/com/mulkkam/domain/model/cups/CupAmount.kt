package com.mulkkam.domain.model.cups

import com.mulkkam.domain.model.result.MulKkamError
import java.io.Serializable

@JvmInline
value class CupAmount(
    val value: Int,
) : Serializable {
    init {
        check(value in MIN_ML..MAX_ML) { throw MulKkamError.SettingCupsError.InvalidAmount }
    }

    companion object {
        const val MIN_ML = 1
        const val MAX_ML = 2000
    }
}
