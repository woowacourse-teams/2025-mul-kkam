package com.mulkkam.domain.model.member

import com.mulkkam.domain.model.members.Nickname
import com.mulkkam.domain.model.result.MulKkamError
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NicknameTest {
    @Test
    fun `닉네임의 길이가 최소 길이보다 짧으면 에러가 발생한다`() {
        assertThrows<MulKkamError.NicknameError.InvalidLength> {
            Nickname("1")
        }
    }

    @Test
    fun `닉네임의 길이가 최대 길이보다 길면 에러가 발생한다`() {
        assertThrows<MulKkamError.NicknameError.InvalidLength> {
            Nickname("12345678910")
        }
    }

    @Test
    fun `닉네임에 특수 문자가 포함되면 에러가 발생한다`() {
        assertThrows<MulKkamError.NicknameError.InvalidCharacters> {
            Nickname(",,,,")
        }
    }
}
