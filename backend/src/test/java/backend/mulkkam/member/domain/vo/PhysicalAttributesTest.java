package backend.mulkkam.member.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_MEMBER_WEIGHT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.mulkkam.common.exception.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PhysicalAttributesTest {

    @DisplayName("생성자 검증 시에")
    @Nested
    class NewPhysicalAttributes {

        @DisplayName("올바른 몸무게를 설정할 수 있다.")
        @ParameterizedTest
        @ValueSource(doubles = {10.0, 50.0, 100.0, 250.0})
        void success_validValue(double input) {
            // when & then
            assertThatCode(() -> {
                new PhysicalAttributes(null, input);
            }).doesNotThrowAnyException();
        }

        @DisplayName("범위를 벗어난 몸무게는 입력할 수 없다.")
        @ParameterizedTest
        @ValueSource(doubles = {-1.0, 0.0, 9.0, 251.0, 1_000.0})
        void error_invalidValue(double input) {
            // when & then
            assertThatThrownBy(() -> new PhysicalAttributes(null, input))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_MEMBER_WEIGHT.name());
        }
    }
}
