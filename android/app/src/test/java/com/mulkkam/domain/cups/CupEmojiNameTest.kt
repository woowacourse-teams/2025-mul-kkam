package com.mulkkam.domain.cups

import com.mulkkam.domain.model.cups.CupName
import com.mulkkam.domain.model.result.MulKkamError
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CupEmojiNameTest {
    @Test
    fun `정상적인 이름은 잘 만들어진다`() {
        // given
        val name = "스타벅스 텀블러"

        // when
        val model = CupName(name)

        // then
        assertThat(model.value).isEqualTo(name)
    }

    @Test
    fun `이름이 공백만 있으면 허용되지 않는다`() {
        // given
        val name = "     "

        // when & then
        assertThrows(MulKkamError.SettingCupsError.InvalidNicknameLength::class.java) {
            CupName(name)
        }
    }

    @Test
    fun `이름에 특수문자가 들어가면 허용되지 않는다`() {
        // given
        val name = "환노!"

        // when & then
        assertThrows(MulKkamError.SettingCupsError.InvalidNicknameCharacters::class.java) {
            CupName(name)
        }
    }

    @Test
    fun `이름이 너무 짧으면 허용되지 않는다`() {
        // given
        val name = ""

        // when & then
        assertThrows(MulKkamError.SettingCupsError.InvalidNicknameLength::class.java) {
            CupName(name)
        }
    }

    @Test
    fun `이름이 너무 길면 허용되지 않는다`() {
        // given
        val name = "01234567890"

        // when & then
        assertThrows(MulKkamError.SettingCupsError.InvalidNicknameLength::class.java) {
            CupName(name)
        }
    }
}
