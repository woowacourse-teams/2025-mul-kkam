package com.mulkkam.data.remote.model.response.members

import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.members.MemberInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembersResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("weight")
    val weight: Double?,
    @SerialName("gender")
    val gender: String?,
    @SerialName("targetAmount")
    val targetAmount: Int,
)

fun MembersResponse.toDomain(): MemberInfo =
    MemberInfo(
        nickname = nickname,
        weight = weight?.toInt()?.let { BioWeight(it) },
        gender = gender?.let { Gender.from(it) },
        targetAmount = targetAmount,
    )
