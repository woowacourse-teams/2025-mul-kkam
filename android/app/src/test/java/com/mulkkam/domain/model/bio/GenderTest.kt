package com.mulkkam.domain.model.bio

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GenderTest {
    @Test
    fun `MALE 문자열이 들어오면 남자를 성별 타입으로 반환한다`() {
        // given
        val input = "MALE"

        // when
        val actual = Gender.from(input)
        val expected = Gender.MALE

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `FAMALE 문자열이 들어오면 여자를 성별 타입으로 반환한다`() {
        // given
        val input = "FEMALE"

        // when
        val actual = Gender.from(input)
        val expected = Gender.FEMALE

        // then
        assertEquals(expected, actual)
    }
}
