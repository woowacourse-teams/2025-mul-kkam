package com.mulkkam.domain.model.bio

import com.mulkkam.domain.model.result.MulKkamError

@JvmInline
value class CalorieBurn(
    val kcal: Double,
) {
    init {
        require(kcal >= MIN_CALORIE) { throw MulKkamError.CalorieError.LowCalorie }
    }

    companion object {
        const val MIN_CALORIE: Double = 100.0
    }
}
