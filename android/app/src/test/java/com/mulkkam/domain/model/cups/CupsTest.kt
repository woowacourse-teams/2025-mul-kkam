package com.mulkkam.domain.model.cups

import com.mulkkam.fixture.CHECHE_CUP
import com.mulkkam.fixture.HWANNOW_CUP
import com.mulkkam.fixture.KALI_CUP
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CupsTest {
    private val cups =
        Cups(
            listOf(
                HWANNOW_CUP,
                CHECHE_CUP,
                KALI_CUP,
            ),
        )

    @Test
    fun `컵의 개수가 최대 사이즈인지 확인한다`() {
        // given & when
        val actual = cups.isMaxSize

        // then
        assertThat(actual).isTrue()
    }

    @Test
    fun `아이디를 통해 컵을 찾는다`() {
        // given
        val id = HWANNOW_CUP.id

        // when
        val actual = cups.findCupById(id)

        // then
        assertThat(actual).isEqualTo(HWANNOW_CUP)
    }

    @Test
    fun `순서를 정렬한다`() {
        // given
        val mockedCups =
            Cups(
                listOf(
                    KALI_CUP,
                    CHECHE_CUP,
                    HWANNOW_CUP,
                ),
            )

        // when
        val actual = mockedCups.reorderRanks()

        // then
        actual.cups.forEachIndexed { index, cup ->
            assertThat(cup.rank).isEqualTo(index + 1)
        }
    }
}
