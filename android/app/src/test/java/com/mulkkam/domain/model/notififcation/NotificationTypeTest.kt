package com.mulkkam.domain.model.notififcation

import com.mulkkam.domain.model.notification.NotificationType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NotificationTypeTest {
    @Test
    fun `SUGGESTION 문자열이 들어오면 알림 타입을 SUGGESTION으로 반환한다`() {
        // given
        val input = "SUGGESTION"

        // when
        val actual = NotificationType.from(input)
        val expected = NotificationType.SUGGESTION

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `REMIND 문자열이 들어오면 알림 타입을 REMIND으로 반환한다`() {
        // given
        val input = "REMIND"

        // when
        val actual = NotificationType.from(input)
        val expected = NotificationType.REMIND

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `NOTICE 문자열이 들어오면 알림 타입을 NOTICE으로 반환한다`() {
        // given
        val input = "NOTICE"

        // when
        val actual = NotificationType.from(input)
        val expected = NotificationType.NOTICE

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun `이외의 문자열이 들어오면 에러가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            NotificationType.from("hwannow")
        }
    }
}
