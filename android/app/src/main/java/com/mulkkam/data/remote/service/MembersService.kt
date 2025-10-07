package com.mulkkam.data.remote.service

import com.mulkkam.data.remote.model.request.members.MarketingNotificationAgreedRequest
import com.mulkkam.data.remote.model.request.members.MemberNicknameRequest
import com.mulkkam.data.remote.model.request.members.MembersReminderRequest
import com.mulkkam.data.remote.model.request.members.MembersPhysicalAtrributesRequest
import com.mulkkam.data.remote.model.request.members.NightNotificationAgreedRequest
import com.mulkkam.data.remote.model.response.members.MemberNicknameResponse
import com.mulkkam.data.remote.model.response.members.MembersProgressInfoResponse
import com.mulkkam.data.remote.model.response.members.MembersResponse
import com.mulkkam.data.remote.model.response.notifications.NotificationAgreedResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface MembersService {
    @GET("/members/nickname/validation")
    suspend fun getMembersNicknameValidation(
        @Query("nickname") nickname: String,
    ): Result<Unit>

    @GET("/members/nickname")
    suspend fun getMembersNickname(): Result<MemberNicknameResponse>

    @PATCH("/members/nickname")
    suspend fun patchMembersNickname(
        @Body nickname: MemberNicknameRequest,
    ): Result<Unit>

    @GET("/members")
    suspend fun getMembers(): Result<MembersResponse>

    @POST("/members/physical-attributes")
    suspend fun postMembersPhysicalAttributes(
        @Body physicalAttributes: MembersPhysicalAtrributesRequest,
    ): Result<Unit>

    @GET("/members/progress-info")
    suspend fun getMembersProgressInfo(
        @Query("date") date: String,
    ): Result<MembersProgressInfoResponse>

    @PATCH("/members/notifications/night")
    suspend fun patchMembersNotificationNight(
        @Body nightNotificationAgreedRequest: NightNotificationAgreedRequest,
    ): Result<Unit>

    @PATCH("/members/notifications/marketing")
    suspend fun patchMembersNotificationMarketing(
        @Body marketingNotificationAgreedRequest: MarketingNotificationAgreedRequest,
    ): Result<Unit>

    @GET("/members/notifications/settings")
    suspend fun getMembersNotificationSettings(): Result<NotificationAgreedResponse>

    @DELETE("/members")
    suspend fun deleteMembers(): Result<Unit>

    @PATCH("/members/reminder")
    suspend fun patchMembersReminder(
        @Body memberReminderRequest: MembersReminderRequest,
    ): Result<Unit>
}
