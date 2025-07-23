package backend.mulkkam.cup.domain.vo;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CupRankTest {

    @Nested
    @DisplayName("생성자 검증 시에")
    class NewCupNickname {

        @DisplayName("컵의 갯수는 3개 이하면 가능하다")
        @ParameterizedTest
        @ValueSource(strings = {"0", "1", "2", "3"})
        void success_nameLengthBetween2And10(Integer input) {
            // when & then
            assertThatCode(() -> {
                new CupRank(input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 갯수는 설정할 수 없다")
        @ParameterizedTest
        @ValueSource(strings = {"4", "-1", "5"})
        void error_nameLengthOutOfRange(Integer input) {
            // when & then
            assertThatThrownBy(() -> {
                new CupRank(input);
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
