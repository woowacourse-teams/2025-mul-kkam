package com.mulkkam.data.remote.model.request.members

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberNicknameRequest(
    @SerialName("memberNickname")
    val memberNickname: String,
)
