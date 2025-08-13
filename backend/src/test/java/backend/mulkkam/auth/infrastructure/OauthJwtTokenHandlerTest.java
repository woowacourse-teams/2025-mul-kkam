package backend.mulkkam.auth.infrastructure;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.support.ServiceIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OauthJwtTokenHandlerTest extends ServiceIntegrationTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @DisplayName("토큰 생성을 요청하면")
    @Nested
    class CreateToken {

        @DisplayName("생성된 토큰에서 account id 값을 subject로 추출할 수 있다.")
        @Test
        void success_withAccount() {
            // given
            OauthAccount oauthAccount = new OauthAccount("testId", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            // when
            String token = oauthJwtTokenHandler.createAccessToken(oauthAccount);
            Long actual = oauthJwtTokenHandler.getSubject(token);

            // then
            assertThat(actual).isEqualTo(oauthAccount.getId());
        }
    }

    @DisplayName("토큰의 subject를 요청할 때")
    @Nested
    class GetSubject {

        @DisplayName("직접 생성한 토큰에 대해서는 OauthAccount 엔티티의 id 값을 올바르게 반환한다.")
        @Test
        void success_createdToken() {
            // given
            OauthAccount oauthAccount = new OauthAccount("testId", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);
            String token = oauthJwtTokenHandler.createAccessToken(oauthAccount);

            // when
            Long actual = oauthJwtTokenHandler.getSubject(token);

            // then
            assertThat(actual).isEqualTo(oauthAccount.getId());
        }

        @DisplayName("올바르지 않은 토큰은 예외가 발생한다.")
        @Test
        void error_withInvalidToken() {
            // given
            String invalidToken = "invalidToken";

            // when & then
            assertThatThrownBy(() -> oauthJwtTokenHandler.getSubject(invalidToken))
                    .isInstanceOf(IllegalArgumentException.class); // TODO: Custom Exception으로 변경 후 반영
        }
    }
}
