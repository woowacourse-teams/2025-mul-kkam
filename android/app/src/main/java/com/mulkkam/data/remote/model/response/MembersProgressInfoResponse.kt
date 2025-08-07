package com.mulkkam.data.remote.model.response

import com.mulkkam.domain.model.MembersProgressInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembersProgressInfoResponse(
    @SerialName("memberNickname")
    val memberNickname: String,
    @SerialName("streak")
    val streak: Int,
    @SerialName("achievementRate")
    val achievementRate: Double,
    @SerialName("targetAmount")
    val targetAmount: Int,
    @SerialName("totalAmount")
    val totalAmount: Int,
    @SerialName("comment")
    val comment: String,
)

fun MembersProgressInfoResponse.toDomain(): MembersProgressInfo =
    MembersProgressInfo(
        nickname = memberNickname,
        streak = streak,
        achievementRate = achievementRate.toFloat(),
        targetAmount = targetAmount,
        totalAmount = totalAmount,
        comment = comment,
    )
