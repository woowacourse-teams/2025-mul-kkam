package backend.mulkkam.cup.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CupNicknameTest {

    @Nested
    @DisplayName("생성자 검증")
    class NewCupNickname {

        @DisplayName("1 ~ 5글자의 닉네임을 설정할 수 있다.")
        @ParameterizedTest
        @ValueSource(strings = {"1", "22", "333", "4444", "55555"})
        void success_nameLengthLessThan5(String input) {
            // given

            // when & then
            assertThatCode(() -> {
                new CupNickname(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 닉네임은 설정할 수 없다.")
        @ParameterizedTest
        @ValueSource(strings = {"", "666666"})
        void error_nameLengthOutOfRange(String input) {
            // given

            // when & then
            assertThatThrownBy(() -> {
                new CupNickname(input);
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
