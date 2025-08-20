package backend.mulkkam.cup.domain;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.mulkkam.common.exception.CommonException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class IntakeTypeTest {

    @DisplayName("이름을 통해 음용 타입을 찾는 경우")
    @Nested
    class FindByName {

        private static Stream<Arguments> testCasesForSuccess_ignoreCasesOfName() {
            return Stream.of(
                    Arguments.of("WATER", IntakeType.WATER),
                    Arguments.of("water", IntakeType.WATER),
                    Arguments.of("COFFEE", IntakeType.COFFEE),
                    Arguments.of("coffee", IntakeType.COFFEE)
            );
        }

        @DisplayName("대소문자 구분 없이 적절한 타입을 찾는다")
        @ParameterizedTest
        @MethodSource("testCasesForSuccess_ignoreCasesOfName")
        void success_ignoreCasesOfName(
                String input,
                IntakeType expected
        ) {
            // when
            IntakeType actual = IntakeType.findByName(input);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("존재하지 않는 값에 대해 예외를 던진다")
        @Test
        void error_notExistedName() {
            // when
            assertThatThrownBy(() -> IntakeType.findByName("not_existed_value"))
                    .isInstanceOf(CommonException.class).hasMessage(NOT_FOUND_INTAKE_TYPE.name());
        }
    }

    @DisplayName("타입에 따른 음용량을 계산할 때")
    @Nested
    class CalculateHydration {

        @DisplayName("입력한 섭취량에 맞추어 수분량이 계산된다")
        @ParameterizedTest(name = "타입={0}, 음용량={1}ml → 예상 수분량={2}ml")
        @CsvSource({
                "WATER, 250, 250",
                "COFFEE, 300, 285",
                "COFFEE, 150, 143"
        })
        void success_calculatedDependingOnTheType(String typeName, int intakeAmount, int expectedHydration) {
            // given
            IntakeType intakeType = IntakeType.findByName(typeName);

            // when
            int actualHydration = intakeType.calculateHydration(intakeAmount);

            // then
            assertThat(actualHydration).isEqualTo(expectedHydration);
        }
    }
}
