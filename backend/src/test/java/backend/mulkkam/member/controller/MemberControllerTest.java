package backend.mulkkam.member.controller;

import static backend.mulkkam.auth.domain.OauthProvider.KAKAO;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.AccountRefreshTokenRepository;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupEmojiUrl;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.request.ModifyIsMarketingNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.ModifyIsNightNotificationAgreedRequest;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.dto.response.NotificationSettingsResponse;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.AccountRefreshTokenFixtureBuilder;
import backend.mulkkam.support.fixture.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.fixture.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.fixture.cup.CupFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccountRefreshTokenRepository accountRefreshTokenRepository;

    @Autowired
    private CupRepository cupRepository;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    @Autowired
    private CupEmojiRepository cupEmojiRepository;

    private final Member member = MemberFixtureBuilder
            .builder()
            .isNightNotificationAgreed(true)
            .isMarketingNotificationAgreed(true)
            .weight(null)
            .gender(null)
            .build();
    private CupEmoji cupEmoji;

    private OauthAccount oauthAccount;

    private String token;
    private Cup savedCup;

    @BeforeEach
    void setUp() {
        memberRepository.save(member);

        oauthAccount = new OauthAccount(member, "test", KAKAO);
        oauthAccountRepository.save(oauthAccount);

        token = oauthJwtTokenHandler.createAccessToken(oauthAccount);

        saveDefaultCupEmojis();

        cupEmoji = cupEmojiRepository.findAll().getFirst();

        Cup cup = CupFixtureBuilder
                .withMemberAndCupEmoji(member, cupEmoji)
                .build();
        savedCup = cupRepository.save(cup);
    }

    private void saveDefaultCupEmojis() {
        for (IntakeType intakeType : IntakeType.values()) {
            CupEmojiUrl url = CupEmojiUrl.getDefaultByType(intakeType);
            cupEmojiRepository.save(new CupEmoji(url));
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

        @DisplayName("마케팅 알림을 수정한다.")
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

        @DisplayName("몸무게 및 성별이 null이라면 null로 반환한다.")
        @Test
        void success_whenWeightAndGenderCanBeNull() throws Exception {
            // when
            String json = mockMvc.perform(get("/members")
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            MemberResponse actual = objectMapper.readValue(json, MemberResponse.class);

            //then
            assertSoftly(softly -> {
                softly.assertThat(actual.weight()).isNull();
                softly.assertThat(actual.gender()).isNull();
            });
        }
    }

    @DisplayName("회원 탈퇴 시")
    @Nested
    class Delete {

        @DisplayName("유효한 토큰으로 요청하면 정상적으로 멤버가 삭제된다")
        @Test
        void success_withValidToken() throws Exception {
            // given
            AccountRefreshToken accountRefreshToken = AccountRefreshTokenFixtureBuilder
                    .withOauthAccount(oauthAccount)
                    .build();
            accountRefreshTokenRepository.save(accountRefreshToken);

            String token = oauthJwtTokenHandler.createAccessToken(oauthAccount);

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .build();
            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .buildWithCup(savedCup);
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

        @DisplayName("삭제된 멤버의 닉네임으로 저장이 가능하다.")
        @Test
        void success_whenNicknameCanBeNicknameOfDeletedMember() throws Exception {
            // given
            AccountRefreshToken accountRefreshToken = AccountRefreshTokenFixtureBuilder
                    .withOauthAccount(oauthAccount)
                    .build();
            accountRefreshTokenRepository.save(accountRefreshToken);

            // when
            mockMvc.perform(delete("/members")
                            .header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());

            Member otherMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("체체"))
                    .build();

            // then
            assertSoftly(softAssertions -> {
                assertThatCode(() -> memberRepository.save(otherMember))
                        .doesNotThrowAnyException();
            });
        }
    }

    @DisplayName("회원 닉네임 중복 검사 할 때에")
    @Nested
    class CheckForDuplicates {

        @DisplayName("닉네임 중복 검사하려는 멤버의 기존 닉네임과 중복된다면 예외가 발생한다")
        @Test
        void error_whenModifyNicknameIsSameAsBeforeNickname() throws Exception {
            // when
            String json = mockMvc.perform(get("/members/nickname/validation")
                            .param("nickname", "히로")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            // then
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(SAME_AS_BEFORE_NICKNAME.name());
            });
        }

        @DisplayName("멤버의 닉네임이 이미 존재하는 닉네임이라면 예외가 발생한다")
        @Test
        void error_existingNickname() throws Exception {
            // given
            Member otherMember = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("체체"))
                    .build();
            memberRepository.save(otherMember);

            // when
            String json = mockMvc.perform(get("/members/nickname/validation")
                            .param("nickname", "체체")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            // then
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(DUPLICATE_MEMBER_NICKNAME.name());
            });
        }
    }
}
