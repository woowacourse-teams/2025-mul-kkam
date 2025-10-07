package com.mulkkam.domain.repository

import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.members.MemberInfo
import com.mulkkam.domain.model.members.NotificationAgreedInfo
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.result.MulKkamResult
import java.time.LocalDate

interface MembersRepository {
    suspend fun getMembersNicknameValidation(nickname: String): MulKkamResult<Unit>

    suspend fun patchMembersNickname(nickname: String): MulKkamResult<Unit>

    suspend fun getMembersNickname(): MulKkamResult<String>

    suspend fun getMembers(): MulKkamResult<MemberInfo>

    suspend fun postMembersPhysicalAttributes(
        gender: Gender,
        weight: BioWeight,
    ): MulKkamResult<Unit>

    suspend fun getMembersProgressInfo(date: LocalDate): MulKkamResult<TodayProgressInfo>

    suspend fun patchMembersNotificationNight(isNightNotificationAgreed: Boolean): MulKkamResult<Unit>

    suspend fun patchMembersNotificationMarketing(isMarketingNotificationAgreed: Boolean): MulKkamResult<Unit>

    suspend fun getMembersNotificationSettings(): MulKkamResult<NotificationAgreedInfo>

    suspend fun deleteMembers(): MulKkamResult<Unit>

    suspend fun getIsFirstLaunch(): MulKkamResult<Boolean>

    suspend fun saveIsFirstLaunch(): MulKkamResult<Unit>

    suspend fun patchMembersReminder(enabled: Boolean): MulKkamResult<Unit>
}
