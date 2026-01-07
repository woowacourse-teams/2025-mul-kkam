package com.mulkkam.domain.model.intake

import com.mulkkam.domain.model.result.MulKkamError

@JvmInline
value class TargetAmount(
    val value: Int,
) {
    init {
        require(value >= TARGET_AMOUNT_MIN) {
            throw MulKkamError.TargetAmountError.BelowMinimum
        }
        require(value <= TARGET_AMOUNT_MAX) {
            throw MulKkamError.TargetAmountError.AboveMaximum
        }
    }

    companion object {
        const val TARGET_AMOUNT_MIN: Int = 200
        const val TARGET_AMOUNT_MAX: Int = 5000

        val EMPTY_TARGET_AMOUNT: TargetAmount = TargetAmount(TARGET_AMOUNT_MIN)
    }
}
