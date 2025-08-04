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
            String token = oauthJwtTokenHandler.createToken(oauthAccount);
            Long actual = oauthJwtTokenHandler.getSubject(token);

            // then
            assertThat(actual).isEqualTo(oauthAccount.getId());
        }
    }
}
