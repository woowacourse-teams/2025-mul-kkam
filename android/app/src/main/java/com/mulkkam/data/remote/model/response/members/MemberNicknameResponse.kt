package com.mulkkam.data.remote.model.response.members

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberNicknameResponse(
    @SerialName("memberNickname")
    val memberNickname: String,
)
