package com.mulkkam.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class IntakeType {
    WATER,
    COFFEE,
    UNKNOWN,
    ;

    fun toLabel(): String =
        when (this) {
            WATER -> "물"
            COFFEE -> "커피"
            UNKNOWN -> ""
        }

    fun toColorHex(): String =
        when (this) {
            WATER -> "#90E0EF"
            COFFEE -> "#C68760"
            UNKNOWN -> ""
        }

    companion object {
        fun from(intakeType: String): IntakeType =
            when (intakeType) {
                WATER.name -> WATER
                COFFEE.name -> COFFEE
                else -> UNKNOWN
            }
    }
}
