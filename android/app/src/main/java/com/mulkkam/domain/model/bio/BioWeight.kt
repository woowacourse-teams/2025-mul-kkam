package com.mulkkam.domain.model.bio

import java.io.Serializable

@JvmInline
value class BioWeight(
    val value: Int = WEIGHT_DEFAULT,
) : Serializable {
    init {
        require(value in WEIGHT_MIN..WEIGHT_MAX) { throw IllegalArgumentException() }
    }

    companion object {
        const val WEIGHT_MAX: Int = 250
        const val WEIGHT_MIN: Int = 10
        const val WEIGHT_DEFAULT: Int = 60
    }
}
