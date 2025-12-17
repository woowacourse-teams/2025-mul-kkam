package com.mulkkam.domain.model.bio

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExerciseCalorieTest {
    @Test
    fun `운동 임계값 이상의 칼로리를 가질 시에는 운동을 한 것으로 판단한다`() {
        // given
        val value = ExerciseCalorie.EXERCISE_DETECTION_THRESHOLD

        // when
        val model = ExerciseCalorie(value)

        // then
        assertThat(model.exercised).isTrue()
    }

    @Test
    fun `운동 임계값 미만의 칼로리를 가질 시에는 운동을 안한 것으로 판단한다`() {
        // given
        val value = ExerciseCalorie.EXERCISE_DETECTION_THRESHOLD - 1

        // when
        val model = ExerciseCalorie(value)

        // then
        assertThat(model.exercised).isFalse()
    }
}
