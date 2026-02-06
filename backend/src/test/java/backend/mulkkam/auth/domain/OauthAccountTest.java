package backend.mulkkam.auth.domain;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.MEMBER_ALREADY_EXIST_IN_OAUTH_ACCOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OauthAccountTest {

    @DisplayName("멤버 필드를 수정할 때")
    @Nested
    class ModifyMember {

        @DisplayName("이미 멤버가 존재하는 경우 예외를 던진다")
        @Test
        void error_memberIsAlreadyExistedInOauthAccount() {
            // given
            Member member = MemberFixtureBuilder.builder()
                    .build();

            OauthAccount oauthAccount = new OauthAccount(
                    member,
                    "temp",
                    OauthProvider.KAKAO
            );

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> oauthAccount.modifyMember(member));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(MEMBER_ALREADY_EXIST_IN_OAUTH_ACCOUNT);
        }
    }
}
