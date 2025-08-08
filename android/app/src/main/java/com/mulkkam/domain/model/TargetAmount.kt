package com.mulkkam.domain.model

@JvmInline
value class TargetAmount(
    val amount: Int,
) {
    init {
        require(amount > TARGET_AMOUNT_MIN && amount < TARGET_AMOUNT_MAX) { throw MulKkamError.TargetAmountError.InvalidTargetAmount }
    }

    companion object {
        private const val TARGET_AMOUNT_MIN: Int = 0
        private const val TARGET_AMOUNT_MAX: Int = 10000
    }
}
