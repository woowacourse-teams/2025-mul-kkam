package com.mulkkam.domain.model

data class MemberInfo(
    val nickname: String,
    val weight: BioWeight?,
    val gender: Gender?,
    val targetAmount: Int,
)
