package com.mulkkam.data.remote.datasource

import com.mulkkam.data.remote.api.safeApiCall
import com.mulkkam.data.remote.api.safeApiCallUnit
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
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class MembersRemoteDataSourceImpl(
    private val httpClient: HttpClient,
) : MembersRemoteDataSource {
    override suspend fun getMembersNicknameValidation(nickname: String): Result<Unit> =
        safeApiCallUnit {
            httpClient.get("/members/nickname/validation") {
                parameter("nickname", nickname)
            }
        }

    override suspend fun getMembersNickname(): Result<MemberNicknameResponse> =
        safeApiCall {
            httpClient.get("/members/nickname")
        }

    override suspend fun patchMembersNickname(nickname: MemberNicknameRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.patch("/members/nickname") {
                setBody(nickname)
            }
        }

    override suspend fun getMembers(): Result<MembersResponse> =
        safeApiCall {
            httpClient.get("/members")
        }

    override suspend fun postMembersPhysicalAttributes(physicalAttributes: MembersPhysicalAtrributesRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.post("/members/physical-attributes") {
                setBody(physicalAttributes)
            }
        }

    override suspend fun getMembersProgressInfo(date: String): Result<MembersProgressInfoResponse> =
        safeApiCall {
            httpClient.get("/members/progress-info") {
                parameter("date", date)
            }
        }

    override suspend fun patchMembersNotificationNight(nightNotificationAgreedRequest: NightNotificationAgreedRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.patch("/members/notifications/night") {
                setBody(nightNotificationAgreedRequest)
            }
        }

    override suspend fun patchMembersNotificationMarketing(
        marketingNotificationAgreedRequest: MarketingNotificationAgreedRequest,
    ): Result<Unit> =
        safeApiCallUnit {
            httpClient.patch("/members/notifications/marketing") {
                setBody(marketingNotificationAgreedRequest)
            }
        }

    override suspend fun getMembersNotificationSettings(): Result<NotificationAgreedResponse> =
        safeApiCall {
            httpClient.get("/members/notifications/settings")
        }

    override suspend fun deleteMembers(): Result<Unit> =
        safeApiCallUnit {
            httpClient.delete("/members")
        }

    override suspend fun patchMembersReminder(memberReminderRequest: MembersReminderRequest): Result<Unit> =
        safeApiCallUnit {
            httpClient.patch("/members/reminder") {
                setBody(memberReminderRequest)
            }
        }

    override suspend fun getMembersSearch(
        word: String,
        lastId: Long?,
        size: Int,
    ): Result<MembersSearchResponse> =
        safeApiCall {
            httpClient.get("/members/search") {
                parameter("word", word)
                lastId?.let { parameter("lastId", it) }
                parameter("size", size)
            }
        }
}
