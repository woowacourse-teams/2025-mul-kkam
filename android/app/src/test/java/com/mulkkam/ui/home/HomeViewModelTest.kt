package com.mulkkam.ui.home

import com.mulkkam.di.RepositoryInjection
import com.mulkkam.di.RepositoryInjection.membersRepository
import com.mulkkam.domain.model.result.MulKkamError
import com.mulkkam.domain.model.result.MulKkamResult
import com.mulkkam.domain.repository.CupsRepository
import com.mulkkam.domain.repository.IntakeRepository
import com.mulkkam.domain.repository.MembersRepository
import com.mulkkam.domain.repository.NotificationRepository
import com.mulkkam.ui.fixture.createFakeSuccessProgressInfo
import com.mulkkam.ui.model.MulKkamUiState
import com.mulkkam.ui.util.CoroutinesTestExtension
import com.mulkkam.ui.util.InstantTaskExecutorExtension
import com.mulkkam.ui.util.getOrAwaitValue
import com.mulkkam.ui.util.toSuccessDataOrNull
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class HomeViewModelTest {
    private lateinit var fakeMembersRepository: MembersRepository
    private lateinit var fakeCupsRepository: CupsRepository
    private lateinit var fakeNotificationRepository: NotificationRepository
    private lateinit var fakeIntakeRepository: IntakeRepository
    private lateinit var homeViewModel: HomeViewModel

    @BeforeEach
    fun setup() {
        mockkObject(RepositoryInjection)
        fakeMembersRepository = mockk(relaxed = true)
        fakeCupsRepository = mockk(relaxed = true)
        fakeNotificationRepository = mockk(relaxed = true)
        fakeIntakeRepository = mockk(relaxed = true)
        every { RepositoryInjection.membersRepository } returns fakeMembersRepository
        every { RepositoryInjection.cupsRepository } returns fakeCupsRepository
        every { RepositoryInjection.notificationRepository } returns fakeNotificationRepository
        every { RepositoryInjection.intakeRepository } returns fakeIntakeRepository
    }

    @Test
    @DisplayName("오늘의 진행 상황을 성공적으로 불러온다")
    fun loadTodayProgressInfo_Success() {
        // given
        val expectedProgress = createFakeSuccessProgressInfo()
        coEvery { membersRepository.getMembersProgressInfo(any()) } returns MulKkamResult(data = expectedProgress)

        // when
        homeViewModel = HomeViewModel()
        homeViewModel.loadTodayProgressInfo()
        val actualData = homeViewModel.todayProgressInfoUiState.getOrAwaitValue().toSuccessDataOrNull()

        // then
        assertThat(actualData).isEqualTo(expectedProgress)

        coVerify(exactly = 2) { membersRepository.getMembersProgressInfo(any()) }
    }

    @Test
    @DisplayName("오늘의 진행상황을 불러오기 실패하면 Failure 상태가 된다")
    fun loadTodayProgressInfo_Failure() {
        // given
        val error = MulKkamError.NetworkUnavailable
        coEvery { membersRepository.getMembersProgressInfo(any()) } returns MulKkamResult(error = error)

        // when
        homeViewModel = HomeViewModel()
        homeViewModel.loadTodayProgressInfo()
        val actual = homeViewModel.todayProgressInfoUiState.getOrAwaitValue()

        // then
        assertThat(actual).isInstanceOf(MulKkamUiState.Failure::class.java)
        assertThat((actual as MulKkamUiState.Failure).error).isEqualTo(error)
    }
}
