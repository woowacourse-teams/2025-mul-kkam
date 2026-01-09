package backend.mulkkam.cup.domain;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("IntakeType 도메인")
class IntakeTypeTest {

    @DisplayName("음용 타입 조회")
    @Nested
    class FindByName {

        private static Stream<Arguments> validIntakeTypes() {
            return Stream.of(
                    Arguments.of("WATER", IntakeType.WATER),
                    Arguments.of("water", IntakeType.WATER),
                    Arguments.of("Water", IntakeType.WATER),
                    Arguments.of("COFFEE", IntakeType.COFFEE),
                    Arguments.of("coffee", IntakeType.COFFEE)
            );
        }

        @DisplayName("대소문자 구분 없이 음용 타입을 찾을 수 있다")
        @ParameterizedTest(name = "\"{0}\" → {1}")
        @MethodSource("validIntakeTypes")
        void intake_type_is_found_case_insensitively(String input, IntakeType expected) {
            // when
            IntakeType result = IntakeType.findByName(input);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @DisplayName("존재하지 않는 타입명으로 조회하면 실패한다")
        @Test
        void non_existent_type_name_throws_exception() {
            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> IntakeType.findByName("JUICE"));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_INTAKE_TYPE);
        }
    }

    @DisplayName("수분량 계산")
    @Nested
    class CalculateHydration {

        @DisplayName("음용 타입별 수분 환산 비율에 따라 수분량이 계산된다")
        @ParameterizedTest(name = "{0} {1}ml → 수분량 {2}ml")
        @CsvSource({
                "WATER, 250, 250",   // 100%
                "WATER, 500, 500",   // 100%
                "COFFEE, 300, 285",  // 95%
                "COFFEE, 150, 143"   // 95%
        })
        void hydration_is_calculated_by_type_ratio(String typeName, int intakeAmount, int expectedHydration) {
            // given
            IntakeType sut = IntakeType.findByName(typeName);

            // when
            int result = sut.calculateHydration(intakeAmount);

            // then
            assertThat(result).isEqualTo(expectedHydration);
        }
    }
}
