package com.mulkkam.data.repository

import com.mulkkam.data.local.datasource.MembersLocalDataSource
import com.mulkkam.data.remote.datasource.MembersRemoteDataSource
import com.mulkkam.data.remote.model.error.toDomain
import com.mulkkam.data.remote.model.request.members.MarketingNotificationAgreedRequest
import com.mulkkam.data.remote.model.request.members.MemberNicknameRequest
import com.mulkkam.data.remote.model.request.members.MembersPhysicalAtrributesRequest
import com.mulkkam.data.remote.model.request.members.MembersReminderRequest
import com.mulkkam.data.remote.model.request.members.NightNotificationAgreedRequest
import com.mulkkam.data.remote.model.response.members.toDomain
import com.mulkkam.data.remote.model.response.notifications.toDomain
import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.members.MemberInfo
import com.mulkkam.domain.model.members.MemberSearchResult
import com.mulkkam.domain.model.members.NotificationAgreedInfo
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.model.result.toMulKkamResult
import com.mulkkam.domain.repository.MembersRepository
import kotlinx.datetime.LocalDate

class MembersRepositoryImpl(
    private val membersRemoteDataSource: MembersRemoteDataSource,
    private val membersLocalDataSource: MembersLocalDataSource,
) : MembersRepository {
    override suspend fun getMembersNicknameValidation(nickname: String): MulKkamResult<Unit> {
        val result = membersRemoteDataSource.getMembersNicknameValidation(nickname)
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun patchMembersNickname(nickname: String): MulKkamResult<Unit> {
        val result = membersRemoteDataSource.patchMembersNickname(MemberNicknameRequest(nickname))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getMembersNickname(): MulKkamResult<String> {
        val result = membersRemoteDataSource.getMembersNickname()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.memberNickname) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getMembers(): MulKkamResult<MemberInfo> {
        val result = membersRemoteDataSource.getMembers()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun postMembersPhysicalAttributes(
        gender: Gender,
        weight: BioWeight,
    ): MulKkamResult<Unit> {
        val result =
            membersRemoteDataSource.postMembersPhysicalAttributes(
                MembersPhysicalAtrributesRequest(
                    gender.name,
                    weight.value.toDouble(),
                ),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getMembersProgressInfo(date: LocalDate): MulKkamResult<TodayProgressInfo> {
        val result = membersRemoteDataSource.getMembersProgressInfo(date.toString())
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun patchMembersNotificationNight(isNightNotificationAgreed: Boolean): MulKkamResult<Unit> {
        val result =
            membersRemoteDataSource.patchMembersNotificationNight(
                NightNotificationAgreedRequest(
                    isNightNotificationAgreed,
                ),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun patchMembersNotificationMarketing(isMarketingNotificationAgreed: Boolean): MulKkamResult<Unit> {
        val result =
            membersRemoteDataSource.patchMembersNotificationMarketing(
                MarketingNotificationAgreedRequest(
                    isMarketingNotificationAgreed,
                ),
            )
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getMembersNotificationSettings(): MulKkamResult<NotificationAgreedInfo> {
        val result = membersRemoteDataSource.getMembersNotificationSettings()
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun deleteMembers(): MulKkamResult<Unit> {
        val result = membersRemoteDataSource.deleteMembers()
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getIsFirstLaunch(): MulKkamResult<Boolean> =
        runCatching {
            membersLocalDataSource.isFirstLaunch
        }.toMulKkamResult()

    override suspend fun saveIsFirstLaunch(): MulKkamResult<Unit> =
        runCatching {
            membersLocalDataSource.saveIsFirstLaunch()
        }.toMulKkamResult()

    override suspend fun patchMembersReminder(enabled: Boolean): MulKkamResult<Unit> {
        val result = membersRemoteDataSource.patchMembersReminder(MembersReminderRequest(enabled))
        return result.fold(
            onSuccess = { MulKkamResult() },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }

    override suspend fun getMembersSearch(
        word: String,
        lastId: Long?,
        size: Int,
    ): MulKkamResult<MemberSearchResult> {
        val result = membersRemoteDataSource.getMembersSearch(word, lastId, size)
        return result.fold(
            onSuccess = { MulKkamResult(data = it.toDomain()) },
            onFailure = { MulKkamResult(error = it.toDomain()) },
        )
    }
}
