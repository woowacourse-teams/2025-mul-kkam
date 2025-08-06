package com.mulkkam.domain.model

enum class IntakeType {
    WATER,
    COFFEE,
    UNKNOWN,
    ;

    companion object {
        fun from(intakeType: String): IntakeType =
            when (intakeType) {
                "WATER" -> WATER
                "COFFEE" -> COFFEE
                else -> UNKNOWN
            }
    }
}
