package backend.mulkkam.intake.controller;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.vo.CupAmount;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.cup.CupFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class IntakeHistoryControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    @Autowired
    private TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @Autowired
    private CupRepository cupRepository;

    @Autowired
    private CupEmojiRepository cupEmojiRepository;

    private final AtomicLong oauthIdCounter = new AtomicLong(1);

    record TestContext(Member member, String token, Cup cup) {}

    private TestContext createTestContext() {
        Member member = memberRepository.save(MemberFixtureBuilder
                .builder()
                .weight(70.0)
                .targetAmount(1500)
                .build());

        OauthAccount oauthAccount = oauthAccountRepository.save(
                new OauthAccount(member, "oauthId" + oauthIdCounter.getAndIncrement(), OauthProvider.KAKAO)
        );
        String deviceUuid = "deviceUuid";
        String token = oauthJwtTokenHandler.createAccessToken(oauthAccount, deviceUuid);

        CupEmoji cupEmoji = cupEmojiRepository.save(new CupEmoji("http://example.com"));

        Cup cup = cupRepository.save(CupFixtureBuilder
                .withMemberAndCupEmoji(member, cupEmoji)
                .cupAmount(new CupAmount(1000))
                .build());

        return new TestContext(member, token, cup);
    }

    @DisplayName("음용 기록을 조회할 때에")
    @Nested
    class ReadSummaryOfIntakeHistories {

        @DisplayName("조회한 날짜가 없어도 세부 기록이 없는 빈 객체로 온다")
        @Test
        void success_returns_empty_details_when_no_records() throws Exception {
            // given
            TestContext ctx = createTestContext();
            LocalDate from = LocalDate.of(2025, 7, 15);
            LocalDate to = LocalDate.of(2025, 7, 16);

            // when
            String json = mockMvc.perform(get("/intake/history")
                            .param("from", from.toString())
                            .param("to", to.toString())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ctx.token()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<IntakeHistorySummaryResponse> actual = objectMapper.readValue(json,
                    new TypeReference<>() {
                    });

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.size()).isEqualTo(2);
                softly.assertThat(actual.getFirst().date()).isEqualTo(LocalDate.of(2025, 7, 15));
                softly.assertThat(actual.getFirst().intakeDetails().size()).isEqualTo(0);
            });
        }

        @DisplayName("조회한 날짜가 없어도 세부 기록이 없는 빈 객체로 올 때, 오늘이라면 목표 음용량이 멤버의 목표 음용량으로 결정된다")
        @Test
        void success_returns_member_target_amount_when_no_records_for_today() throws Exception {
            // given
            TestContext ctx = createTestContext();

            // when
            String json = mockMvc.perform(get("/intake/history")
                            .param("from", LocalDate.now().toString())
                            .param("to", LocalDate.now().toString())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + ctx.token()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<IntakeHistorySummaryResponse> actual = objectMapper.readValue(json,
                    new TypeReference<>() {
                    });

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.size()).isEqualTo(1);
                softly.assertThat(actual.getFirst().date()).isEqualTo(LocalDate.now());
                softly.assertThat(actual.getFirst().intakeDetails().size()).isEqualTo(0);
                softly.assertThat(actual.getFirst().targetAmount()).isEqualTo(1500);
            });
        }
    }
}
