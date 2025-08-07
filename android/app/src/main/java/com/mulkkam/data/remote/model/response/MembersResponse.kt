package com.mulkkam.data.remote.model.response

import com.mulkkam.domain.Gender
import com.mulkkam.domain.model.MemberInfo
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
        weight = weight?.toInt(),
        gender = gender?.let { Gender.from(it) },
        targetAmount = targetAmount,
    )
