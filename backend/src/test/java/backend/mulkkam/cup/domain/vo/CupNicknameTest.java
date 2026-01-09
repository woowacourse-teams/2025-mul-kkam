package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("CupNickname 도메인")
class CupNicknameTest {

    @DisplayName("닉네임이 2~10글자이면 생성에 성공한다")
    @ParameterizedTest(name = "닉네임 길이 = {0}글자")
    @ValueSource(strings = {"22", "333", "4444", "55555", "666666", "7777777", "88888888", "999999999", "1010101010"})
    void nickname_with_valid_length_is_created(String value) {
        // when & then
        assertThatCode(() -> new CupNickname(value))
                .doesNotThrowAnyException();
    }

    @DisplayName("닉네임이 2글자 미만이면 생성에 실패한다")
    @ParameterizedTest(name = "닉네임 = \"{0}\"")
    @ValueSource(strings = {"", "1"})
    void nickname_shorter_than_min_length_is_invalid(String value) {
        // when
        CommonException ex = assertThrows(CommonException.class,
                () -> new CupNickname(value));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_NICKNAME);
    }

    @DisplayName("닉네임이 10글자 초과이면 생성에 실패한다")
    @ParameterizedTest(name = "닉네임 = \"{0}\"")
    @ValueSource(strings = {"12345678901", "123456789012"})
    void nickname_longer_than_max_length_is_invalid(String value) {
        // when
        CommonException ex = assertThrows(CommonException.class,
                () -> new CupNickname(value));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(INVALID_CUP_NICKNAME);
    }
}
