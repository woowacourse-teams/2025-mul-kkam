package com.mulkkam.domain.model.friends

data class FriendsRequestResult(
    val friendsRequestInfos: List<FriendsRequestInfo>,
    val nextId: Long?,
)
