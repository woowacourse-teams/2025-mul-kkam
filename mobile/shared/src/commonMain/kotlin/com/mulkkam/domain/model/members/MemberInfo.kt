package com.mulkkam.domain.model.members

import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.bio.BioWeight
import kotlinx.serialization.Serializable

@Serializable
data class MemberInfo(
    val nickname: Nickname,
    val weight: BioWeight?,
    val gender: Gender?,
    val targetAmount: Int,
)
