package com.mulkkam.domain.model.intake

import com.mulkkam.fixture.FULL_INTAKE_HISTORY
import com.mulkkam.fixture.HALF_INTAKE_HISTORY
import com.mulkkam.fixture.SAMPLE_INTAKE_HISTORY
import com.mulkkam.fixture.ZERO_INTAKE_HISTORY
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class IntakeHistorySummaryTest {
    private val pastDay = LocalDate.of(2025, 1, 1)
    private val currentDay = LocalDate.of(2025, 2, 2)
    private val futureDay = LocalDate.of(2025, 3, 3)

    @Test
    fun `기록의 날짜가 과거이고, 기록이 없다면 Past의 NoRecord 상태이다`() {
        // given
        val summary = ZERO_INTAKE_HISTORY.copy(date = pastDay)

        // when
        val state = summary.determineWaterIntakeState(currentDay)

        // then
        assert(state is WaterIntakeState.Past.NoRecord)
    }

    @Test
    fun `기록의 날짜가 과거이고, 기록이 있지만 목표 음용량을 달성하지 못했다면 Past의 Partial 상태이다`() {
        // given
        val summary = HALF_INTAKE_HISTORY.copy(date = pastDay)

        // when
        val state = summary.determineWaterIntakeState(currentDay)

        // then
        assert(state is WaterIntakeState.Past.Partial)
    }

    @Test
    fun `기록의 날짜가 과거이고, 목표 음용량을 달성했다면 Past의 Full 상태이다`() {
        // given
        val summary = FULL_INTAKE_HISTORY.copy(date = pastDay)

        // when
        val state = summary.determineWaterIntakeState(currentDay)

        // then
        assert(state is WaterIntakeState.Past.Full)
    }

    @Test
    fun `기록의 날짜가 현재이고, 목표 음용량을 달성하지 못했다면 Present의 NotFull 상태이다`() {
        // given
        val summary = HALF_INTAKE_HISTORY.copy(date = currentDay)

        // when
        val state = summary.determineWaterIntakeState(currentDay)

        // then
        assert(state is WaterIntakeState.Present.NotFull)
    }

    @Test
    fun `기록의 날짜가 현재이고, 목표 음용량을 달성했다면 Present의 Full 상태이다`() {
        // given
        val summary = FULL_INTAKE_HISTORY.copy(date = currentDay)

        // when
        val state = summary.determineWaterIntakeState(currentDay)

        // then
        assert(state is WaterIntakeState.Present.Full)
    }

    @Test
    fun `기록의 날짜가 미래이면 Future 상태이다`() {
        // given
        val summary = ZERO_INTAKE_HISTORY.copy(date = futureDay)

        // when
        val state = summary.determineWaterIntakeState(currentDay)

        // then
        assert(state is WaterIntakeState.Future)
    }

    @Test
    fun `특정 음용 기록을 삭제한다`() {
        // given
        val summary = FULL_INTAKE_HISTORY

        // when
        val actual = summary.afterDeleteHistory(SAMPLE_INTAKE_HISTORY).intakeHistories
        val expected = summary.intakeHistories.filterNot { it == SAMPLE_INTAKE_HISTORY }

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
