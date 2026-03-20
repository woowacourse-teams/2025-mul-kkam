package com.mulkkam.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class Gender {
    MALE,
    FEMALE,
    ;

    companion object {
        fun from(gender: String): Gender =
            when (gender) {
                MALE.name -> MALE
                FEMALE.name -> FEMALE
                else -> throw IllegalArgumentException("Unknown gender: $gender")
            }
    }
}
