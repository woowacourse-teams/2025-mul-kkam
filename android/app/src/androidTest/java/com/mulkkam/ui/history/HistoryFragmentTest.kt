package com.mulkkam.ui.history

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.mulkkam.R
import com.mulkkam.data.repository.IntakeRepository
import com.mulkkam.di.RepositoryInjection
import com.mulkkam.domain.IntakeHistory
import com.mulkkam.domain.IntakeHistorySummary
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import java.time.LocalDate
import java.time.LocalDateTime

class HistoryFragmentTest {
    private lateinit var fakeIntakeRepository: IntakeRepository

    @Before
    fun setup() {
        launchFragmentInContainer<HistoryFragment>(themeResId = R.style.Theme_MulKkam)

        mockkObject(RepositoryInjection)
        fakeIntakeRepository = mockk(relaxed = true)
        every { RepositoryInjection.intakeRepository } returns fakeIntakeRepository
    }

    @Test
    @DisplayName("화면에 제목과 부제목이 보인다")
    fun displayLabel() {
        onView(withId(R.id.tv_view_label)).check(matches(withText("깜지")))
        onView(withId(R.id.tv_view_sub_label)).check(matches(withText("깜빡하지 않은 물 일지")))
    }

    @Test
    @DisplayName("해당 날짜의 기록이 보인다")
    fun displayHistories() {
        coEvery { fakeIntakeRepository.getIntakeHistory(any(), any()) } returns
            listOf(
                IntakeHistorySummary(
                    date = LocalDate.of(2025, 7, 22),
                    totalIntakeAmount = 1200,
                    targetAmount = 600,
                    achievementRate = 50f,
                    intakeHistories =
                        listOf(
                            IntakeHistory(
                                id = 1,
                                dateTime = LocalDateTime.of(2025, 7, 22, 10, 0),
                                intakeAmount = 600,
                            ),
                        ),
                ),
            )
        onView(withId(R.id.include_chart_tue))
            .perform(click())

        onView(withId(R.id.tv_daily_chart_label)).check(matches(withText("7월 22일 (화)의 깜지")))
    }
}
