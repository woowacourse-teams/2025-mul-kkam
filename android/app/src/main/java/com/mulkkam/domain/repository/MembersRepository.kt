package com.mulkkam.domain.repository

import com.mulkkam.domain.model.bio.BioWeight
import com.mulkkam.domain.model.bio.Gender
import com.mulkkam.domain.model.members.MemberInfo
import com.mulkkam.domain.model.members.OnboardingInfo
import com.mulkkam.domain.model.members.TodayProgressInfo
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.ui.model.UserAuthState
import java.time.LocalDate

interface MembersRepository {
    suspend fun postMembers(onboardingInfo: OnboardingInfo): MulKkamResult<Unit>

    suspend fun getMembersNicknameValidation(nickname: String): MulKkamResult<Unit>

    suspend fun patchMembersNickname(nickname: String): MulKkamResult<Unit>

    suspend fun getMembersNickname(): MulKkamResult<String>

    suspend fun getMembers(): MulKkamResult<MemberInfo>

    suspend fun postMembersPhysicalAttributes(
        gender: Gender,
        weight: BioWeight,
    ): MulKkamResult<Unit>

    suspend fun getMembersCheckOnboarding(): MulKkamResult<UserAuthState>

    suspend fun getMembersProgressInfo(date: LocalDate): MulKkamResult<TodayProgressInfo>

    suspend fun deleteMembers(): MulKkamResult<Unit>
}
