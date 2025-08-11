package com.mulkkam.ui.history

import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.MulKkamResult
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.ui.fixture.FULL_INTAKE_HISTORY
import com.mulkkam.ui.fixture.getWeeklyIntakeHistories
import com.mulkkam.ui.util.CoroutinesTestExtension
import com.mulkkam.ui.util.InstantTaskExecutorExtension
import com.mulkkam.ui.util.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.DayOfWeek
import java.time.LocalDate

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class HistoryViewModelTest {
    private val today: LocalDate = LocalDate.of(2025, 9, 10)
    private val past: LocalDate = LocalDate.of(2024, 9, 10)
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
    @DisplayName("주간 음용량을 조회한다")
    fun loadIntakeHistories() {
        // given
        coEvery {
            fakeIntakeRepository.getIntakeHistory(
                any(),
                any(),
            )
        } returns MulKkamResult(data = getWeeklyIntakeHistories(today))
        val expected = getWeeklyIntakeHistories(today)

        // when
        historyViewModel.loadIntakeHistories()
        val actual = historyViewModel.weeklyIntakeHistories.getOrAwaitValue()

        // then
        assertThat(actual).isEqualTo(expected)

        coVerify { fakeIntakeRepository.getIntakeHistory(any(), any()) }
    }

    @Test
    @DisplayName("일간 음용 데이터를 업데이트한다")
    fun updateDailyIntakeHistories() {
        // given
        val mockedHistory = FULL_INTAKE_HISTORY

        // when
        historyViewModel.updateDailyIntakeHistories(mockedHistory, LocalDate.now())
        val actual = historyViewModel.dailyIntakeHistories.getOrAwaitValue()

        // then
        assertThat(actual).isEqualTo(mockedHistory)
    }

    @Test
    @DisplayName("특정 id를 가진 음용 기록을 삭제한다")
    fun deleteIntakeHistory() {
        // given
        val capturedId = slot<Int>()
        coEvery {
            fakeIntakeRepository.deleteIntakeHistoryDetails(capture(capturedId))
        } returns MulKkamResult(data = Unit)
        val expected = FULL_INTAKE_HISTORY.intakeHistories.first()

        // when
        historyViewModel.deleteIntakeHistory(expected)
        val actual = historyViewModel.deleteSuccess.getOrAwaitValue()

        // then
        assertThat(actual).isTrue
        assertThat(capturedId.captured).isEqualTo(expected.id)

        coVerify(exactly = 1) { fakeIntakeRepository.deleteIntakeHistoryDetails(any()) }
    }

    @Test
    @DisplayName("주간 기록에 오늘 날짜가 포함되어 있지 않다면 이번 주가 아닌지 판단한다")
    fun decideNotCurrentWeek() {
        // given
        coEvery {
            fakeIntakeRepository.getIntakeHistory(
                any(),
                any(),
            )
        } returns MulKkamResult(data = getWeeklyIntakeHistories(past))

        // when
        historyViewModel.loadIntakeHistories(
            referenceDate = past,
            currentDate = today,
        )
        val actual = historyViewModel.isNotCurrentWeek.getOrAwaitValue()

        // then
        assertThat(actual).isTrue
    }

    @Test
    @DisplayName("주간 기록에 오늘 날짜가 포함되어 있다면 이번 주인지 판단한다")
    fun decideCurrentWeek() {
        // given
        coEvery {
            fakeIntakeRepository.getIntakeHistory(
                any(),
                any(),
            )
        } returns MulKkamResult(data = getWeeklyIntakeHistories(today))

        // when
        historyViewModel.loadIntakeHistories(
            referenceDate = today,
            currentDate = today,
        )
        val actual = historyViewModel.isNotCurrentWeek.getOrAwaitValue()

        // then
        assertThat(actual).isFalse
    }

    @Test
    @DisplayName("주간 기록에 오늘이 포함되면 일간 기록은 오늘 날짜로 설정된다")
    fun setDailySummaryToToday_WhenTodayInWeeklyRecords() {
        // given
        coEvery {
            fakeIntakeRepository.getIntakeHistory(
                any(),
                any(),
            )
        } returns MulKkamResult(data = getWeeklyIntakeHistories(today))

        // when
        historyViewModel.loadIntakeHistories(
            referenceDate = today,
            currentDate = today,
        )
        val actual = historyViewModel.dailyIntakeHistories.getOrAwaitValue().date

        // then
        assertThat(actual).isEqualTo(today)
    }

    @Test
    @DisplayName("주간 기록에 오늘이 없으면 일간 기록은 월요일로 설정된다")
    fun setDailySummaryToMonday_WhenTodayNotInWeeklyRecords() {
        // given
        coEvery {
            fakeIntakeRepository.getIntakeHistory(
                any(),
                any(),
            )
        } returns MulKkamResult(data = getWeeklyIntakeHistories(past))

        // when
        historyViewModel.loadIntakeHistories(
            referenceDate = past,
            currentDate = today,
        )
        val actual = historyViewModel.dailyIntakeHistories.getOrAwaitValue().date

        // then
        assertThat(actual).isNotEqualTo(today)
        assertThat(actual.dayOfWeek).isEqualTo(DayOfWeek.MONDAY)
    }
}
