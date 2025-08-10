package com.mulkkam.ui.history

import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.model.MulKkamResult
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.ui.fixture.FULL_INTAKE_HISTORY
import com.mulkkam.ui.fixture.WEEKLY_EMPTY_INTAKE_HISTORIES
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
import java.time.LocalDate

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
    @DisplayName("주간 음용량을 조회한다")
    fun loadIntakeHistories_noHistory() {
        // given
        coEvery {
            fakeIntakeRepository.getIntakeHistory(
                any(),
                any(),
            )
        } returns MulKkamResult(data = WEEKLY_EMPTY_INTAKE_HISTORIES)
        val expected = WEEKLY_EMPTY_INTAKE_HISTORIES

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
        historyViewModel.updateDailyIntakeHistories(mockedHistory, LocalDate.now())
        val actual = historyViewModel.dailyIntakeHistories.getOrAwaitValue()

        // then
        assertThat(actual).isEqualTo(mockedHistory)
    }
}
