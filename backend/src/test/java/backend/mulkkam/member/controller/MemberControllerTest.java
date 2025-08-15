package backend.mulkkam.member.controller;

import static backend.mulkkam.auth.domain.OauthProvider.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.Gender;
import backend.mulkkam.member.dto.CreateMemberRequest;
import backend.mulkkam.member.dto.request.ModifyIsMarketingNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.ModifyIsNightNotificationAgreedRequest;
import backend.mulkkam.member.dto.response.NotificationSettingsResponse;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.AccountRefreshTokenFixtureBuilder;
import backend.mulkkam.support.CupFixtureBuilder;
import backend.mulkkam.support.DatabaseCleaner;
import backend.mulkkam.support.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Autowired
    CupRepository cupRepository;

    @Autowired
    IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    private Member member;
    private String token;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();

        member = MemberFixtureBuilder
                .builder()
                .isNightNotificationAgreed(true)
                .isMarketingNotificationAgreed(true)
                .build();
        memberRepository.save(member);
        OauthAccount oauthAccount = new OauthAccount(member, "test", KAKAO);
        oauthAccountRepository.save(oauthAccount);

        token = oauthJwtTokenHandler.createAccessToken(oauthAccount);
    }

    @DisplayName("멤버를 생성할 때에")
    @Nested
    class Create {

        @DisplayName("기본 컵 3개도 저장된다.")
        @Test
        void success_whenMemberSavedThenBeginningCupsSaved() throws Exception {
            // given
            databaseCleaner.clean();

            OauthAccount oauthAccount = new OauthAccount("test", KAKAO);
            oauthAccountRepository.save(oauthAccount);
            token = oauthJwtTokenHandler.createAccessToken(oauthAccount);
            CreateMemberRequest createMemberRequest = new CreateMemberRequest("test2", 50.0, Gender.MALE, 1500, true,
                    true);

            // when
            mockMvc.perform(post("/members")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createMemberRequest)))
                    .andExpect(status().isOk());

            OauthAccount foundOauthAccount = oauthAccountRepository.findByOauthId("test").orElseThrow();
            Member foundMember = foundOauthAccount.getMember();
            List<Cup> cups = cupRepository.findAllByMember(foundMember);

            // then
            assertSoftly(softly -> {
                softly.assertThat(cups.size()).isEqualTo(3);
                softly.assertThat(cups.getFirst().getNickname().value()).isEqualTo("종이컵");
                softly.assertThat(cups.get(1).getNickname().value()).isEqualTo("스타벅스 톨");
                softly.assertThat(cups.get(2).getNickname().value()).isEqualTo("스타벅스 그란데");
            });
        }
    }

    @DisplayName("멤버의 정보를 수정할 때에")
    @Nested
    class Modify {

        @DisplayName("야간 알림을 수정한다.")
        @Test
        void success_whenModifyIsNightNotificationAgreed() throws Exception {
            // given
            ModifyIsNightNotificationAgreedRequest modifyIsNightNotificationAgreedRequest = new ModifyIsNightNotificationAgreedRequest(
                    false);

            // when
            mockMvc.perform(patch("/members/notifications/night")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyIsNightNotificationAgreedRequest)))
                    .andExpect(status().isOk());
            Member foundMember = memberRepository.findById(member.getId()).orElseThrow();

            // then
            assertSoftly(softly ->
                    softly.assertThat(foundMember.isNightNotificationAgreed()).isFalse()
            );
        }

        @DisplayName("야간 알림을 수정한다.")
        @Test
        void success_whenModifyIsMarketingNotificationAgreed() throws Exception {
            // given
            ModifyIsMarketingNotificationAgreedRequest modifyIsMarketingNotificationAgreedRequest = new ModifyIsMarketingNotificationAgreedRequest(
                    false);

            // when
            mockMvc.perform(patch("/members/notifications/marketing")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyIsMarketingNotificationAgreedRequest)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();
            Member foundMember = memberRepository.findById(member.getId()).orElseThrow();

            // then
            assertSoftly(softly ->
                    softly.assertThat(foundMember.isMarketingNotificationAgreed()).isFalse()
            );
        }
    }

    @DisplayName("멤버의 정보를 조회할 때에")
    @Nested
    class Get {

        @DisplayName("야간 알림과 마케팅 수신 동의 세팅을 가져온다.")
        @Test
        void success_whenModifyIsNightNotificationAgreed() throws Exception {
            // when
            String json = mockMvc.perform(get("/members/notifications/settings")
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            NotificationSettingsResponse actual = objectMapper.readValue(json, NotificationSettingsResponse.class);

            //then
            assertSoftly(softly -> {
                softly.assertThat(actual.isNightNotificationAgreed()).isTrue();
                softly.assertThat(actual.isMarketingNotificationAgreed()).isTrue();
            });
        }
    }

    @DisplayName("회원 탈퇴 시")
    @Nested
    class Delete {

        @BeforeEach
        void setUp() {
            databaseCleaner.clean();
        }

        @DisplayName("유효한 토큰으로 요청하면 정상적으로 멤버가 삭제된다")
        @Test
        void success_withValidToken() throws Exception {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .build();
            Member savedMember = memberRepository.save(member);

            OauthAccount oauthAccount = new OauthAccount(savedMember, "temp", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            AccountRefreshToken accountRefreshToken = AccountRefreshTokenFixtureBuilder
                    .withOauthAccount(oauthAccount)
                    .build();
            accountRefreshTokenRepository.save(accountRefreshToken);

            String token = oauthJwtTokenHandler.createAccessToken(oauthAccount);

            Cup cup = CupFixtureBuilder
                    .withMember(member)
                    .build();
            cupRepository.save(cup);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .build();
            intakeHistoryDetailRepository.save(intakeHistoryDetail);

            // when
            mockMvc.perform(delete("/members")
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());

            // then
            assertSoftly(softAssertions -> {
                assertThat(memberRepository.findAll()).isEmpty();
                assertThat(oauthAccountRepository.findAll()).isEmpty();
                assertThat(accountRefreshTokenRepository.findAll()).isEmpty();
                assertThat(cupRepository.findAll()).isEmpty();
                assertThat(intakeHistoryRepository.findAll()).isEmpty();
                assertThat(intakeHistoryDetailRepository.findAll()).isEmpty();
            });
        }
    }
}
