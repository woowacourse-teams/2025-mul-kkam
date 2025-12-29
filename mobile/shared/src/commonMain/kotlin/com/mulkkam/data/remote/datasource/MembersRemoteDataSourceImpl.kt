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

// TODO: DataSource 구현 필요
class MembersRemoteDataSourceImpl : MembersRemoteDataSource {
    override suspend fun getMembersNicknameValidation(nickname: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getMembersNickname(): Result<MemberNicknameResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun patchMembersNickname(nickname: MemberNicknameRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getMembers(): Result<MembersResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun postMembersPhysicalAttributes(physicalAttributes: MembersPhysicalAtrributesRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getMembersProgressInfo(date: String): Result<MembersProgressInfoResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun patchMembersNotificationNight(nightNotificationAgreedRequest: NightNotificationAgreedRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun patchMembersNotificationMarketing(
        marketingNotificationAgreedRequest: MarketingNotificationAgreedRequest,
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getMembersNotificationSettings(): Result<NotificationAgreedResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMembers(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun patchMembersReminder(memberReminderRequest: MembersReminderRequest): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getMembersSearch(
        word: String,
        lastId: Long?,
        size: Int,
    ): Result<MembersSearchResponse> {
        TODO("Not yet implemented")
    }
}
