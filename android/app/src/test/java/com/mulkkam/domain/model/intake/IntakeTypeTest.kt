package com.mulkkam.domain.model.intake

import org.assertj.core.api.Assertions.assertThat
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
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `COFFEE 문자열이 들어오면 커피을 액체 타입으로 반환한다`() {
        // given
        val input = "COFFEE"

        // when
        val actual = IntakeType.from(input)
        val expected = IntakeType.COFFEE

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `이외의 문자열이 들어오면 Unknown을 액체 타입으로 반환한다`() {
        // given
        val input = "CheChe"

        // when
        val actual = IntakeType.from(input)
        val expected = IntakeType.UNKNOWN

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
