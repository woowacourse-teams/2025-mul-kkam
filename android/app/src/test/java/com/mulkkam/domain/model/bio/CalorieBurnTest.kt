package com.mulkkam.domain.model.bio

import com.mulkkam.domain.model.result.MulKkamError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CalorieBurnTest {
    @Test
    fun `MIN_CALORIE이 값이면 칼로리 생성에 성공한다`() {
        // given
        val value = CalorieBurn.MIN_CALORIE

        // when
        val model = CalorieBurn(value)

        // then
        assertThat(model.kcal).isEqualTo(value)
    }

    @Test
    fun `MIN_CALORIE 미만이면 LowCalorie 예외가 발생한다`() {
        // given
        val belowMin = CalorieBurn.MIN_CALORIE - 0.001

        // when & then
        assertThrows(MulKkamError.CalorieError.LowCalorie::class.java) {
            CalorieBurn(belowMin)
        }
    }
}
