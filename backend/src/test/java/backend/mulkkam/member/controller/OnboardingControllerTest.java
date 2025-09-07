package backend.mulkkam.member.controller;

import static backend.mulkkam.auth.domain.OauthProvider.KAKAO;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.cup.CupFixtureBuilder;
import backend.mulkkam.support.fixture.cup.dto.CreateCupRequestFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.fixture.member.dto.CreateMemberRequestFixtureBuilder;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OnboardingControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CupRepository cupRepository;

    @Autowired
    private CupEmojiRepository cupEmojiRepository;

    private final Member member = MemberFixtureBuilder
            .builder()
            .isNightNotificationAgreed(true)
            .isMarketingNotificationAgreed(true)
            .weight(null)
            .gender(null)
            .build();
    private final CupEmoji cupEmoji = new CupEmoji("http://cup-emoji.com");

    private OauthAccount oauthAccount;

    private String token;
    private Cup cup;

    @BeforeEach
    void setUp() {
        memberRepository.save(member);

        oauthAccount = new OauthAccount(member, "test", KAKAO);
        oauthAccountRepository.save(oauthAccount);

        token = oauthJwtTokenHandler.createAccessToken(oauthAccount);

        cupEmojiRepository.save(cupEmoji);
        cup = CupFixtureBuilder
                .withMemberAndCupEmoji(member, cupEmoji)
                .build();
        cupRepository.save(cup);
    }


    @DisplayName("멤버를 생성할 때에")
    @Nested
    class Create {

        private static final String ONBOARDING_OAUTH_ID = "test2";
        private final OauthAccount onboardingAccount = new OauthAccount(ONBOARDING_OAUTH_ID, KAKAO);
        private CreateMemberRequest createMemberRequest;
        private List<CreateCupRequest> createCupRequests;

        @BeforeEach
        void setup() {
            oauthAccountRepository.save(onboardingAccount);

            token = oauthJwtTokenHandler.createAccessToken(onboardingAccount);

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

        @DisplayName("몸무게 및 성별이 NULL이여도 저장된다.")
        @Test
        void success_whenWeightAndGenderCanBeNull() throws Exception {
            // given
            CreateMemberRequest createMemberRequest = CreateMemberRequestFixtureBuilder
                    .withCreateCupRequests(createCupRequests)
                    .weight(null)
                    .gender(null)
                    .build();

            // when
            mockMvc.perform(post("/onboarding")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createMemberRequest)))
                    .andExpect(status().isOk());

            // then
            Member member = memberRepository.findById(onboardingAccount.getId()).orElseThrow();
            List<Cup> cups = cupRepository.findAllByMember(member);
            List<Integer> cupRanks = cups.stream()
                    .map(cup -> cup.getCupRank().value())
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(member.getPhysicalAttributes().getGender()).isNull();
                softly.assertThat(member.getPhysicalAttributes().getWeight()).isNull();
                softly.assertThat(member.getMemberNickname().value()).isEqualTo(createMemberRequest.memberNickname());
                softly.assertThat(cups.size()).isEqualTo(createCupRequests.size());
                softly.assertThat(cupRanks).containsAll(List.of(1, 2, 3));
            });
        }

        @DisplayName("온보딩 시 요청한 컵들이 함께 저장된다")
        @Test
        void success_whenMemberSavedThenBeginningCupsSaved() throws Exception {
            // when
            mockMvc.perform(post("/onboarding")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createMemberRequest)))
                    .andExpect(status().isOk());

            OauthAccount foundOauthAccount = oauthAccountRepository.findByOauthId(ONBOARDING_OAUTH_ID).orElseThrow();
            Member foundMember = foundOauthAccount.getMember();
            List<Cup> cups = cupRepository.findAllByMember(foundMember);

            // then
            List<Integer> cupRanks = cups.stream()
                    .map(cup -> cup.getCupRank().value())
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(cups.size()).isEqualTo(createCupRequests.size());
                softly.assertThat(cupRanks).containsAll(List.of(1, 2, 3));
            });
        }
    }
}
