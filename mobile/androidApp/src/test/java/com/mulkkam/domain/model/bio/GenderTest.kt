package com.mulkkam.domain.model.bio

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GenderTest {
    @Test
    fun `MALE 문자열이 들어오면 남자를 성별 타입으로 반환한다`() {
        // given
        val input = "MALE"

        // when
        val actual = Gender.from(input)
        val expected = Gender.MALE

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `FAMALE 문자열이 들어오면 여자를 성별 타입으로 반환한다`() {
        // given
        val input = "FEMALE"

        // when
        val actual = Gender.from(input)
        val expected = Gender.FEMALE

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `잘못된 문자열이 들어오면 에러가 발생한다`() {
        // given
        val input = "HWANNOW"

        // when & then
        assertThrows<IllegalArgumentException> {
            Gender.from(input)
        }
    }
}
