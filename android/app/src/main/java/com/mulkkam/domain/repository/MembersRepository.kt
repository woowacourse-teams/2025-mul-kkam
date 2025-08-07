package com.mulkkam.domain.repository

import com.mulkkam.domain.Gender
import com.mulkkam.domain.MulKkamResult
import com.mulkkam.domain.model.MemberInfo
import com.mulkkam.domain.model.MembersProgressInfo
import com.mulkkam.domain.model.OnboardingInfo
import java.time.LocalDate

interface MembersRepository {
    suspend fun postMembers(onboardingInfo: OnboardingInfo): MulKkamResult<Unit>

    suspend fun getMembersNicknameValidation(nickname: String): MulKkamResult<Unit>

    suspend fun patchMembersNickname(nickname: String): MulKkamResult<Unit>

    suspend fun getMembersNickname(): MulKkamResult<String>

    suspend fun getMembers(): MulKkamResult<MemberInfo>

    suspend fun postMembersPhysicalAttributes(
        gender: Gender,
        weight: Int,
    ): MulKkamResult<Unit>

    suspend fun getMembersCheckOnboarding(): MulKkamResult<Boolean>

    suspend fun getMembersProgressInfo(date: LocalDate): MulKkamResult<MembersProgressInfo>
}
