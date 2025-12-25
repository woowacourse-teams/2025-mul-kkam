package com.mulkkam.domain.model.cups

import com.mulkkam.domain.model.result.MulKkamError
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class CupAmount(
    val value: Int,
) {
    init {
        check(value in MIN_ML..MAX_ML) { throw MulKkamError.SettingCupsError.InvalidAmount }
    }

    companion object {
        const val MIN_ML: Int = 1
        const val MAX_ML: Int = 2000
    }
}
