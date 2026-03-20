package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.model.request.members.MarketingNotificationAgreedRequest
import com.mulkkam.data.remote.model.request.members.MemberNicknameRequest
import com.mulkkam.data.remote.model.request.members.MembersPhysicalAtrributesRequest
import com.mulkkam.data.remote.model.request.members.MembersReminderRequest
import com.mulkkam.data.remote.model.request.members.NightNotificationAgreedRequest
import com.mulkkam.data.remote.model.response.members.MemberNicknameResponse
import com.mulkkam.data.remote.model.response.members.MembersProgressInfoResponse
import com.mulkkam.data.remote.model.response.members.MembersResponse
import com.mulkkam.data.remote.model.response.members.MembersSearchResponse
import com.mulkkam.data.remote.model.response.notifications.NotificationAgreedResponse

interface MembersRemoteDataSource {
    suspend fun getMembersNicknameValidation(nickname: String): Result<Unit>

    suspend fun getMembersNickname(): Result<MemberNicknameResponse>

    suspend fun patchMembersNickname(nickname: MemberNicknameRequest): Result<Unit>

    suspend fun getMembers(): Result<MembersResponse>

    suspend fun postMembersPhysicalAttributes(physicalAttributes: MembersPhysicalAtrributesRequest): Result<Unit>

    suspend fun getMembersProgressInfo(date: String): Result<MembersProgressInfoResponse>

    suspend fun patchMembersNotificationNight(nightNotificationAgreedRequest: NightNotificationAgreedRequest): Result<Unit>

    suspend fun patchMembersNotificationMarketing(marketingNotificationAgreedRequest: MarketingNotificationAgreedRequest): Result<Unit>

    suspend fun getMembersNotificationSettings(): Result<NotificationAgreedResponse>

    suspend fun deleteMembers(): Result<Unit>

    suspend fun patchMembersReminder(memberReminderRequest: MembersReminderRequest): Result<Unit>

    suspend fun getMembersSearch(
        word: String,
        lastId: Long?,
        size: Int,
    ): Result<MembersSearchResponse>
}
