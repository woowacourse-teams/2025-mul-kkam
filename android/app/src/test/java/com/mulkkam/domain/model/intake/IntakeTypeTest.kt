package com.mulkkam.domain.model.intake

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IntakeTypeTest {
    @Test
    fun `WATER 문자열이 들어오면 물을 액체 타입으로 반환한다`() {
        // given
        val input = "WATER"

        // when
        val actual = IntakeType.from(input)
        val expected = IntakeType.WATER

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `COFFEE 문자열이 들어오면 커피을 액체 타입으로 반환한다`() {
        // given
        val input = "COFFEE"

        // when
        val actual = IntakeType.from(input)
        val expected = IntakeType.COFFEE

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `이외의 문자열이 들어오면 Unknown을 액체 타입으로 반환한다`() {
        // given
        val input = "CheChe"

        // when
        val actual = IntakeType.from(input)
        val expected = IntakeType.UNKNOWN

        // then
        assertEquals(expected, actual)
    }
}
