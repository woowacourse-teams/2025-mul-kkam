package com.mulkkam.domain.model.intake

import com.mulkkam.fixture.FULL_INTAKE_HISTORY
import com.mulkkam.fixture.getWeeklyIntakeHistories
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class IntakeHistorySummariesTest {
    private val monday = LocalDate.of(2025, 8, 11)
    private val mockedIntakeHistories = getWeeklyIntakeHistories(monday)

    @Test
    fun `음용 기록 요약의 첫번째 날짜를 반환한다`() {
        // given & when
        val actual = mockedIntakeHistories.firstDay
        val expected = monday

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `음용 기록 요약의 마지막 날짜를 반환한다`() {
        // given & when
        val actual = mockedIntakeHistories.lastDay
        val expected = monday.plusDays(LAST_INDEX)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `현재 날짜의 연도와 다른 연도가 있으면 다른 연도의 기록으로 판단한다`() {
        // given
        val otherYear = monday.plusYears(1)

        // when
        val actual = mockedIntakeHistories.isCurrentYear(otherYear)

        // then
        assertThat(actual).isFalse()
    }

    @Test
    fun `현재 날짜의 연도와 다른 연도가 없으면 이번 연도의 기록으로 판단한다`() {
        // given & when
        val actual = mockedIntakeHistories.isCurrentYear(monday)

        // then
        assertThat(actual).isTrue()
    }

    @Test
    fun `특정 날짜의 기록을 반환한다`() {
        // given & when
        val actual = mockedIntakeHistories.getByDateOrEmpty(monday)
        val expected = FULL_INTAKE_HISTORY.copy(date = monday)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `특정 인덱스의 기록을 반환한다`() {
        // given & when
        val actual = mockedIntakeHistories.getByIndex(0)
        val expected = FULL_INTAKE_HISTORY.copy(date = monday)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `특정 일수 이후의 기록을 반환한다`() {
        // given
        val offset = 3L

        // when
        val actual = mockedIntakeHistories.getDateByWeekOffset(offset)
        val expected = monday.plusWeeks(offset)

        // then
        assertThat(actual).isEqualTo(expected)
    }

    companion object {
        private const val DAY_IN_A_WEEK = 7
        private const val LAST_INDEX = DAY_IN_A_WEEK - 1L
    }
}
