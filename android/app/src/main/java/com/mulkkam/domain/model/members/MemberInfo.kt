package com.mulkkam.domain.model.members

import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender

data class MemberInfo(
    val nickname: Nickname,
    val weight: BioWeight?,
    val gender: Gender?,
    val targetAmount: Int,
)
