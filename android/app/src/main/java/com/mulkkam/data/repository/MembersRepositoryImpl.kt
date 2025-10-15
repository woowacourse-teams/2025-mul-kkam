package com.mulkkam.data.repository

import com.mulkkam.data.local.preference.MembersPreference
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.members.MarketingNotificationAgreedRequest
import com.mulkkam.data.remote.model.request.members.MemberNicknameRequest
import com.mulkkam.data.remote.model.request.members.MembersPhysicalAtrributesRequest
import com.mulkkam.data.remote.model.request.members.MembersReminderRequest
import com.mulkkam.data.remote.model.request.members.NightNotificationAgreedRequest
import com.mulkkam.data.remote.model.response.members.toDomain
import com.mulkkam.data.remote.model.response.notifications.toDomain
import com.mulkkam.data.remote.service.MembersService
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.members.MemberInfo
import com.mulkkam.domain.model.members.NotificationAgreedInfo
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.model.result.toMulKkamResult
import com.mulkkam.domain.repository.MembersRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MembersRepositoryImpl(
    private val membersService: MembersService,
    private val membersPreference: MembersPreference,
) : MembersRepository {
    override suspend fun getMembersNicknameValidation(nickname: String): MulKkamResult<Unit> {
        val result = membersService.getMembersNicknameValidation(nickname)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun patchMembersNickname(nickname: String): MulKkamResult<Unit> {
        val result = membersService.patchMembersNickname(MemberNicknameRequest(nickname))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getMembersNickname(): MulKkamResult<String> {
        val result = membersService.getMembersNickname()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.memberNickname) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getMembers(): MulKkamResult<MemberInfo> {
        val result = membersService.getMembers()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun postMembersPhysicalAttributes(
        gender: Gender,
        weight: BioWeight,
    ): MulKkamResult<Unit> {
        val result =
            membersService.postMembersPhysicalAttributes(
                MembersPhysicalAtrributesRequest(
                    gender.name,
                    weight.value.toDouble(),
                ),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getMembersProgressInfo(date: LocalDate): MulKkamResult<TodayProgressInfo> {
        val result = membersService.getMembersProgressInfo(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun patchMembersNotificationNight(isNightNotificationAgreed: Boolean): MulKkamResult<Unit> {
        val result =
            membersService.patchMembersNotificationNight(NightNotificationAgreedRequest(isNightNotificationAgreed))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun patchMembersNotificationMarketing(isMarketingNotificationAgreed: Boolean): MulKkamResult<Unit> {
        val result =
            membersService.patchMembersNotificationMarketing(MarketingNotificationAgreedRequest(isMarketingNotificationAgreed))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getMembersNotificationSettings(): MulKkamResult<NotificationAgreedInfo> {
        val result = membersService.getMembersNotificationSettings()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun deleteMembers(): MulKkamResult<Unit> {
        val result = membersService.deleteMembers()
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }

    override suspend fun getIsFirstLaunch(): MulKkamResult<Boolean> =
        runCatching {
            membersPreference.isFirstLaunch
        }.toMulKkamResult()

    override suspend fun saveIsFirstLaunch(): MulKkamResult<Unit> =
        runCatching {
            membersPreference.saveIsFirstLaunch()
        }.toMulKkamResult()

    override suspend fun patchMembersReminder(enabled: Boolean): MulKkamResult<Unit> {
        val result = membersService.patchMembersReminder(MembersReminderRequest(enabled))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toResponseError().toDomain()) },
        )
    }
}
