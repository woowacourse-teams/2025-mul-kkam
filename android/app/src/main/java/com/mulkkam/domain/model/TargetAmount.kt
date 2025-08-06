package com.mulkkam.domain.model

import com.mulkkam.domain.MulKkamError

@JvmInline
value class TargetAmount(
    val amount: Int,
) {
    init {
        require(amount > 0 && amount < 10000) { throw MulKkamError.TargetAmountError.InvalidTargetAmount }
    }
}
