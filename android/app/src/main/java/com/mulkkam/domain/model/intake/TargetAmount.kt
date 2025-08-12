package com.mulkkam.domain.model.intake

import com.mulkkam.domain.model.result.MulKkamError

@JvmInline
value class TargetAmount(
    val amount: Int,
) {
    init {
        require(amount in TARGET_AMOUNT_MIN..TARGET_AMOUNT_MAX) { throw MulKkamError.TargetAmountError.InvalidTargetAmount }
    }

    companion object {
        private const val TARGET_AMOUNT_MIN: Int = 200
        private const val TARGET_AMOUNT_MAX: Int = 5000
    }
}
