package backend.mulkkam.cup.domain;

import backend.mulkkam.common.exception.CommonException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_INTAKE_TYPE.name());
        }

    }
}
