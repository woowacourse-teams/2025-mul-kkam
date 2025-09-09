package backend.mulkkam.member.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_MEMBER_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_TARGET_AMOUNT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.OauthAccountDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.cup.dto.CreateCupRequestFixtureBuilder;
import backend.mulkkam.support.fixture.member.dto.CreateMemberRequestFixtureBuilder;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class OnboardingServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private CupEmojiRepository cupEmojiRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private OnboardingService onboardingService;

    @Autowired
    private MemberRepository memberRepository;

    private final CupEmoji cupEmoji = new CupEmoji("http://example.com");

    @DisplayName("온보딩 시에")
    @Nested
    class Create {

        private List<CreateCupRequest> createCupRequests;
        private CreateMemberRequest createMemberRequest;

        @BeforeEach
        void setUP() {
            CupEmoji savedCupEmoji = cupEmojiRepository.save(cupEmoji);

            createCupRequests = List.of(
                    CreateCupRequestFixtureBuilder
                            .withCupEmojiId(savedCupEmoji.getId())
                            .cupRank(1)
                            .build()
                    ,
                    CreateCupRequestFixtureBuilder
                            .withCupEmojiId(savedCupEmoji.getId())
                            .cupRank(2)
                            .build()
                    ,
                    CreateCupRequestFixtureBuilder
                            .withCupEmojiId(savedCupEmoji.getId())
                            .cupRank(3)
                            .build()
            );
            createMemberRequest = CreateMemberRequestFixtureBuilder
                    .withCreateCupRequests(createCupRequests)
                    .build();
        }

        @DisplayName("정상적으로 회원이 저장된다")
        @Test
        void success_validData() {
            // given
            OauthAccount oauthAccount = new OauthAccount("temp", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);
            cupEmojiRepository.saveAll(
                    List.of(
                            new CupEmoji("http://example1.com"),
                            new CupEmoji("http://example2.com")
                    )
            );

            // when
            onboardingService.create(new OauthAccountDetails(oauthAccount.getId()), createMemberRequest);

            // then
            List<Member> savedMembers = memberRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(savedMembers.size()).isEqualTo(1);
                softly.assertThat(savedMembers.getFirst().getMemberNickname())
                        .isEqualTo(new MemberNickname(createMemberRequest.memberNickname()));
                softly.assertThat(savedMembers.getFirst().getPhysicalAttributes().getWeight()).isEqualTo(createMemberRequest.weight());
                softly.assertThat(savedMembers.getFirst().getPhysicalAttributes().getGender()).isEqualTo(createMemberRequest.gender());
                softly.assertThat(savedMembers.getFirst().getTargetAmount())
                        .isEqualTo(new TargetAmount(createMemberRequest.targetIntakeAmount()));
            });
        }

        @DisplayName("유효하지 않은 닉네임을 사용할 경우 예외를 반환한다")
        @ParameterizedTest
        @ValueSource(strings = {"1", " ", "", "1234567891011"})
        void error_invalidNickname(String invalidNickname) {
            // given
            CreateMemberRequest createMemberRequest = CreateMemberRequestFixtureBuilder
                    .withCreateCupRequests(createCupRequests)
                    .memberNickname(invalidNickname)
                    .build();

            OauthAccount oauthAccount = new OauthAccount("temp", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            // when & then
            assertThatThrownBy(
                    () -> onboardingService.create(new OauthAccountDetails(oauthAccount.getId()), createMemberRequest))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_MEMBER_NICKNAME.name());
        }

        @DisplayName("유효하지 않은 목표 음용량을 사용할 경우 예외를 반환한다")
        @ParameterizedTest
        @ValueSource(ints = {0, -1, -3})
        void error_invalidTargetAmount(int invalidIntakeAmount) {
            // given
            CreateMemberRequest createMemberRequest = CreateMemberRequestFixtureBuilder
                    .withCreateCupRequests(createCupRequests)
                    .targetAmount(invalidIntakeAmount)
                    .build();

            OauthAccount oauthAccount = new OauthAccount("temp", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            // when & then
            assertThatThrownBy(
                    () -> onboardingService.create(new OauthAccountDetails(oauthAccount.getId()), createMemberRequest))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(INVALID_TARGET_AMOUNT.name());
        }
    }
}
