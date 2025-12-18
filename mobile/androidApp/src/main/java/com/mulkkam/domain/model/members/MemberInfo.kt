package com.mulkkam.domain.model.members

import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.bio.BioWeight

data class MemberInfo(
    val nickname: Nickname,
    val weight: BioWeight?,
    val gender: Gender?,
    val targetAmount: Int,
)
