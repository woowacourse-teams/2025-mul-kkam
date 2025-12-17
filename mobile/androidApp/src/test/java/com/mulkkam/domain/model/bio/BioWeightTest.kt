package com.mulkkam.domain.model.bio

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BioWeightTest {
    @Test
    fun `최소 몸무게보다 작은 몸무게가 들어올 경우 에러가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            BioWeight(BioWeight.WEIGHT_MIN - 1)
        }
    }

    @Test
    fun `최대 몸무게보다 높은 몸무게가 들어올 경우 에러가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            BioWeight(BioWeight.WEIGHT_MAX + 1)
        }
    }
}
