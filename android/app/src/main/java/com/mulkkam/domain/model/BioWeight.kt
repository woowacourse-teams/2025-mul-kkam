package com.mulkkam.domain.model

@JvmInline
value class BioWeight(
    val value: Int,
) {
    companion object {
        const val WEIGHT_MAX: Int = 250
        const val WEIGHT_MIN: Int = 0
        const val WEIGHT_DEFAULT: Int = 60
    }
}
