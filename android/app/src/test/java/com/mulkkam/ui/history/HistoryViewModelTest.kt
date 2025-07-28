package com.mulkkam.ui.history

import com.mulkkam.data.repository.IntakeRepository
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.IntakeHistorySummary
import com.mulkkam.ui.fixture.FULL_INTAKE_HISTORY
import com.mulkkam.ui.util.CoroutinesTestExtension
import com.mulkkam.ui.util.InstantTaskExecutorExtension
import com.mulkkam.ui.util.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class HistoryViewModelTest {
    private lateinit var fakeIntakeRepository: IntakeRepository
    private lateinit var historyViewModel: HistoryViewModel

    @BeforeEach
    fun setup() {
        mockkObject(RepositoryInjection)
        fakeIntakeRepository = mockk(relaxed = true)
        every { RepositoryInjection.intakeRepository } returns fakeIntakeRepository

        historyViewModel = HistoryViewModel()
    }

    @Test
    @DisplayName("오늘을 기준으로 주간 음용 데이터를 가져와 저장한다")
    fun loadIntakeHistories() {
        // given
        val mockedHistories = listOf(FULL_INTAKE_HISTORY)
        coEvery {
            fakeIntakeRepository.getIntakeHistory(any(), any())
        } returns mockedHistories

        // when
        historyViewModel.loadIntakeHistories()
        val actual = historyViewModel.weeklyIntakeHistories.getOrAwaitValue()

        // then
        assertThat(actual).containsAll(mockedHistories)
    }

    @Test
    @DisplayName("기록이 없는 날짜는 빈 기록을 생성한다")
    fun loadIntakeHistories_noHistory() {
        // given
        coEvery { fakeIntakeRepository.getIntakeHistory(any(), any()) } returns emptyList()
        val monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val expected =
            List(7) { IntakeHistorySummary.EMPTY_DAILY_WATER_INTAKE.copy(date = monday.plusDays(it.toLong())) }

        // when
        historyViewModel.loadIntakeHistories()
        val actual = historyViewModel.weeklyIntakeHistories.getOrAwaitValue()

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    @DisplayName("일간 음용 데이터를 업데이트한다")
    fun updateDailyIntakeHistories() {
        // given
        val mockedHistory = FULL_INTAKE_HISTORY

        // when
        historyViewModel.updateDailyIntakeHistories(mockedHistory)
        val actual = historyViewModel.dailyIntakeHistories.getOrAwaitValue()

        // then
        assertThat(actual).isEqualTo(mockedHistory)
    }
}
