package com.mulkkam.domain.model.friends

import kotlinx.serialization.Serializable

@Serializable
data class FriendsRequestResult(
    val friendsRequestInfos: List<FriendsRequestInfo>,
    val nextId: Long?,
)
