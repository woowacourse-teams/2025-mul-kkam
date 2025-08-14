package com.mulkkam.data.repository

import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.error.toResponseError
import com.mulkkam.data.remote.model.request.members.MarketingNotificationAgreedRequest
import com.mulkkam.data.remote.model.request.members.MemberNicknameRequest
import com.mulkkam.data.remote.model.request.members.MembersPhysicalAtrributesRequest
import com.mulkkam.data.remote.model.request.members.NightNotificationAgreedRequest
import com.mulkkam.data.remote.model.request.members.toData
import com.mulkkam.data.remote.model.response.members.toDomain
import com.mulkkam.data.remote.model.response.notification.toDomain
import com.mulkkam.data.remote.service.MembersService
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.members.MemberInfo
import com.mulkkam.domain.model.members.NotificationAgreedInfo
import com.mulkkam.domain.model.members.OnboardingInfo
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.ui.model.UserAuthState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MembersRepositoryImpl(
    private val membersService: MembersService,
) : MembersRepository {
    override suspend fun postMembers(onboardingInfo: OnboardingInfo): MulKkamResult<Unit> {
        val result = membersService.postMembers(onboardingInfo.toData())
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { throw it.toResponseError().toDomain() },
        )
    }

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

    override suspend fun getMembersCheckOnboarding(): MulKkamResult<UserAuthState> {
        val result = membersService.getMembersCheckOnboarding()
        return result.fold(
            onSuccess = { MulKkamResult(data = UserAuthState.from(it.finishedOnboarding)) },
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
}
