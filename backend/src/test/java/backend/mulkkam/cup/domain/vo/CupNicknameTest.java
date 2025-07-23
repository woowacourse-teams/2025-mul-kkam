package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.BadRequestErrorCode.INVALID_CUP_NICKNAME;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CupNicknameTest {

    @DisplayName("생성자 검증 시에")
    @Nested
    class NewCupNickname {

        @DisplayName("2 ~ 10글자의 닉네임을 설정할 수 있다.")
        @ParameterizedTest
        @ValueSource(strings = {"22", "333", "4444", "55555", "666666", "7777777", "88888888", "999999999",
                "10101010"})
        void success_nameLengthBetween2And10(String input) {
            // when & then
            assertThatCode(() -> {
                new CupNickname(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 닉네임은 설정할 수 없다.")
        @ParameterizedTest
        @ValueSource(strings = {"", "1", "66_666_666_666"})
        void error_nameLengthOutOfRange(String input) {
            // when & then
            CommonException ex = assertThrows(CommonException.class,
                    () -> new CupNickname(input));
            assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_NICKNAME);
        }
    }
}
