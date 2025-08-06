package com.mulkkam.domain

enum class Gender {
    MALE,
    FEMALE,
    ;

    companion object {
        fun from(asdf: String): Gender =
            when (asdf) {
                MALE.name -> MALE
                FEMALE.name -> FEMALE
                else -> throw IllegalArgumentException()
            }
    }
}
