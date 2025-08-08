package com.mulkkam.domain

enum class Alarm {
    SUGGESTION,
    REMIND,
    NOTICE,
    ;

    companion object {
        fun from(alarm: String): Alarm =
            when (alarm) {
                SUGGESTION.name -> SUGGESTION
                REMIND.name -> REMIND
                NOTICE.name -> NOTICE
                else -> throw IllegalArgumentException()
            }
    }
}
