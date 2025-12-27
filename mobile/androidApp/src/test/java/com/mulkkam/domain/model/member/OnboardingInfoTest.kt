package com.mulkkam.domain.model.member

import com.mulkkam.domain.model.Gender
import com.mulkkam.domain.model.OnboardingInfo
import com.mulkkam.domain.model.bio.BioWeight
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OnboardingInfoTest {
    @Test
    fun `몸무게와 성별 정보가 있으면 신체 정보가 있다고 판별한다`() {
        // given
        val mockedOnboardingInfo = OnboardingInfo(weight = BioWeight(60), gender = Gender.MALE)

        // when
        val actual = mockedOnboardingInfo.hasBioInfo()

        // then
        assertThat(actual).isTrue()
    }

    @Test
    fun `몸무게와 성별 정보가 없으면 신체 정보가 없다고 판별한다`() {
        // given
        val mockedOnboardingInfo = OnboardingInfo()

        // when
        val actual = mockedOnboardingInfo.hasBioInfo()

        // then
        assertThat(actual).isFalse()
    }
}
