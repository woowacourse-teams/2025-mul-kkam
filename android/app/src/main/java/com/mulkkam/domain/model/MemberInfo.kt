package com.mulkkam.domain.model

import com.mulkkam.domain.model.Gender

data class MemberInfo(
    val nickname: String,
    val weight: Int?,
    val gender: Gender?,
    val targetAmount: Int,
)
