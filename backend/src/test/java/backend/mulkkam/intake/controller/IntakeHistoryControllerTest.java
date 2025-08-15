package backend.mulkkam.intake.controller;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.intake.dto.request.IntakeDetailCreateRequest;
import backend.mulkkam.intake.dto.response.IntakeHistorySummaryResponse;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.intake.repository.TargetAmountSnapshotRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.DatabaseCleaner;
import backend.mulkkam.support.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import backend.mulkkam.support.TargetAmountSnapshotFixtureBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class IntakeHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private IntakeHistoryDetailRepository intakeHistoryDetailRepository;

    @Autowired
    private TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    private Member member;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();
        member = MemberFixtureBuilder
                .builder()
                .weight(70.0)
                .targetAmount(new TargetAmount(1500))
                .build();
        memberRepository.save(member);
        OauthAccount oauthAccount = new OauthAccount(member, "testId", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);
        token = oauthJwtTokenHandler.createAccessToken(oauthAccount);
    }

    @DisplayName("음용 세부 기록을 생성할 때에")
    @Nested
    class CreateIntakeHistoryDetail {

        @DisplayName("음용 기록이 없으면 음용 기록을 생성된다")
        @Test
        void success_whenCreateIntakeHistoryDetail() throws Exception {

            LocalDate from = LocalDate.of(2025, 7, 15);
            LocalDate to = LocalDate.of(2025, 7, 15);

            TargetAmountSnapshot targetAmountSnapshot = TargetAmountSnapshotFixtureBuilder
                    .withMember(member)
                    .targetAmount(new TargetAmount(1000))
                    .updatedAt(LocalDate.of(2025, 6, 15))
                    .build();
            targetAmountSnapshotRepository.save(targetAmountSnapshot);

            String beforeJson = mockMvc.perform(get("/intake/history")
                            .param("from", from.toString())
                            .param("to", to.toString())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            IntakeDetailCreateRequest intakeDetailCreateRequest = new IntakeDetailCreateRequest(
                    LocalDateTime.of(2025, 7, 15, 10, 0), 1000);

            mockMvc.perform(post("/intake/history")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(intakeDetailCreateRequest)))
                    .andExpect(status().isOk());

            String afterJson = mockMvc.perform(get("/intake/history")
                            .param("from", from.toString())
                            .param("to", to.toString())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<IntakeHistorySummaryResponse> beforeIntakeHistorySummaries = objectMapper.readValue(beforeJson,
                    new TypeReference<List<IntakeHistorySummaryResponse>>() {
                    });

            List<IntakeHistorySummaryResponse> afterIntakeHistorySummaries = objectMapper.readValue(afterJson,
                    new TypeReference<List<IntakeHistorySummaryResponse>>() {
                    });

            assertSoftly(softly -> {
                softly.assertThat(beforeIntakeHistorySummaries.getFirst().intakeDetails().size()).isEqualTo(0);
                softly.assertThat(beforeIntakeHistorySummaries.getFirst().targetAmount()).isEqualTo(1000);
                softly.assertThat(beforeIntakeHistorySummaries.getFirst().totalIntakeAmount()).isEqualTo(0);
                softly.assertThat(beforeIntakeHistorySummaries.getFirst().achievementRate()).isEqualTo(0.0);

                softly.assertThat(afterIntakeHistorySummaries.getFirst().intakeDetails().size()).isEqualTo(1);
                softly.assertThat(afterIntakeHistorySummaries.getFirst().targetAmount()).isEqualTo(1500);
                softly.assertThat(afterIntakeHistorySummaries.getFirst().totalIntakeAmount()).isEqualTo(1000);
                softly.assertThat(afterIntakeHistorySummaries.getFirst().achievementRate())
                        .isCloseTo(66.6, within(0.1));
            });
        }

        @DisplayName("전 날에 세부 기록이 없다면 스트릭은 1일부터 시작한다")
        @Test
        void success_streakIsOneWhenThereIsNotYesterdayIntakeHistory() throws Exception {

            LocalDate from = LocalDate.of(2025, 7, 15);
            LocalDate to = LocalDate.of(2025, 7, 15);

            IntakeDetailCreateRequest intakeDetailCreateRequest = new IntakeDetailCreateRequest(
                    LocalDateTime.of(2025, 7, 15, 10, 0), 1000);

            mockMvc.perform(post("/intake/history")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(intakeDetailCreateRequest)))
                    .andExpect(status().isOk());

            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(member);

            assertSoftly(softly -> {
                softly.assertThat(intakeHistories.getFirst().getStreak()).isEqualTo(1);
            });
        }

        @DisplayName("전 날에 세부 기록이 없다면 스트릭은 1일부터 시작한다")
        @Test
        void success_streakIsPlusYesterdayStreakWhenThereIsYesterdayIntakeHistory() throws Exception {

            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(LocalDate.of(2025, 7, 14))
                    .streak(45)
                    .build();

            intakeHistoryRepository.save(intakeHistory);

            IntakeDetailCreateRequest intakeDetailCreateRequest = new IntakeDetailCreateRequest(
                    LocalDateTime.of(2025, 7, 15, 10, 0), 1000);

            mockMvc.perform(post("/intake/history")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(intakeDetailCreateRequest)))
                    .andExpect(status().isOk());

            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(member);

            assertSoftly(softly -> {
                softly.assertThat(intakeHistories.getFirst().getStreak()).isEqualTo(45);
                softly.assertThat(intakeHistories.get(1).getStreak()).isEqualTo(46);
            });
        }
    }

    @DisplayName("음용 기록을 조회할 때에")
    @Nested
    class ReadSummaryOfIntakeHistories {

        @DisplayName("주어진 날짜를 기준으로 조회한다")
        @Test
        void success_isValidDateRange() throws Exception {
            LocalDate from = LocalDate.of(2025, 7, 15);
            LocalDate to = LocalDate.of(2025, 7, 16);
            IntakeHistory intakeHistory1 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(from)
                    .build();
            IntakeHistory intakeHistory2 = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(to)
                    .build();
            IntakeHistoryDetail intakeHistoryDetail1 = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory1)
                    .build();
            IntakeHistoryDetail intakeHistoryDetail2 = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory2)
                    .build();
            intakeHistoryRepository.saveAll(List.of(intakeHistory1, intakeHistory2));
            intakeHistoryDetailRepository.saveAll(List.of(intakeHistoryDetail1, intakeHistoryDetail2));

            String json = mockMvc.perform(get("/intake/history")
                            .param("from", from.toString())
                            .param("to", to.toString())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<IntakeHistorySummaryResponse> actual = objectMapper.readValue(json,
                    new TypeReference<List<IntakeHistorySummaryResponse>>() {
                    });

            assertSoftly(softly -> {
                softly.assertThat(actual.size()).isEqualTo(2);
                softly.assertThat(actual.getFirst().date()).isEqualTo(LocalDate.of(2025, 7, 15));
                softly.assertThat(actual.getFirst().intakeDetails().size()).isEqualTo(1);
            });
        }

        @DisplayName("조회한 날짜가 없어도 세부 기록이 없는 빈 객체로 온다")
        @Test
        void success_whenThereAreNoRecords() throws Exception {
            LocalDate from = LocalDate.of(2025, 7, 15);
            LocalDate to = LocalDate.of(2025, 7, 16);

            String json = mockMvc.perform(get("/intake/history")
                            .param("from", from.toString())
                            .param("to", to.toString())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<IntakeHistorySummaryResponse> actual = objectMapper.readValue(json,
                    new TypeReference<List<IntakeHistorySummaryResponse>>() {
                    });

            assertSoftly(softly -> {
                softly.assertThat(actual.size()).isEqualTo(2);
                softly.assertThat(actual.getFirst().date()).isEqualTo(LocalDate.of(2025, 7, 15));
                softly.assertThat(actual.getFirst().intakeDetails().size()).isEqualTo(0);
            });
        }

        @DisplayName("조회한 날짜가 없어도 세부 기록이 없는 빈 객체로 올 때, 오늘이라면 목표 음용량이 멤버의 목표 음용량으로 결정된다")
        @Test
        void success_whenThereAreNoRecordsAndThenTargetAmountEqualToTargetAmountOfMember() throws Exception {
            String json = mockMvc.perform(get("/intake/history")
                            .param("from", LocalDate.now().toString())
                            .param("to", LocalDate.now().toString())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<IntakeHistorySummaryResponse> actual = objectMapper.readValue(json,
                    new TypeReference<List<IntakeHistorySummaryResponse>>() {
                    });

            assertSoftly(softly -> {
                softly.assertThat(actual.size()).isEqualTo(1);
                softly.assertThat(actual.getFirst().date()).isEqualTo(LocalDate.now());
                softly.assertThat(actual.getFirst().intakeDetails().size()).isEqualTo(0);
                softly.assertThat(actual.getFirst().targetAmount()).isEqualTo(1500);
            });
        }
    }

    @DisplayName("스냅샷을 가져올 때에")
    @Nested
    class GetTargetAmountSnapshot {

        @DisplayName("데이터 없이 빈 객체로 올 때, 목표 음용량을 최근 스냅샷을 기준으로 정한다")
        @Test
        void success_whenThereAreNoRecords() throws Exception {
            LocalDate from = LocalDate.of(2025, 7, 14);
            LocalDate to = LocalDate.of(2025, 7, 16);

            TargetAmountSnapshot targetAmountSnapshot1 = TargetAmountSnapshotFixtureBuilder
                    .withMember(member)
                    .targetAmount(new TargetAmount(1500))
                    .updatedAt(LocalDate.of(2025, 7, 13))
                    .build();

            TargetAmountSnapshot targetAmountSnapshot2 = TargetAmountSnapshotFixtureBuilder
                    .withMember(member)
                    .targetAmount(new TargetAmount(2000))
                    .updatedAt(LocalDate.of(2025, 7, 15))
                    .build();

            targetAmountSnapshotRepository.saveAll(List.of(targetAmountSnapshot1, targetAmountSnapshot2));

            String json = mockMvc.perform(get("/intake/history")
                            .param("from", from.toString())
                            .param("to", to.toString())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<IntakeHistorySummaryResponse> actual = objectMapper.readValue(json,
                    new TypeReference<List<IntakeHistorySummaryResponse>>() {
                    });

            assertSoftly(softly -> {
                softly.assertThat(actual.size()).isEqualTo(3);
                softly.assertThat(actual.getFirst().date()).isEqualTo(LocalDate.of(2025, 7, 14));
                softly.assertThat(actual.getFirst().intakeDetails().size()).isEqualTo(0);
                softly.assertThat(actual.getFirst().targetAmount()).isEqualTo(1500);
                softly.assertThat(actual.get(1).targetAmount()).isEqualTo(1500);
                softly.assertThat(actual.get(2).targetAmount()).isEqualTo(2000);
                softly.assertThat(actual.getFirst().intakeDetails()).hasSize(0);
                softly.assertThat(actual.get(1).intakeDetails()).hasSize(0);
                softly.assertThat(actual.get(2).intakeDetails()).hasSize(0);
            });
        }
    }

    @DisplayName("음용 세부 기록을 삭제할 때")
    @Nested
    class DeleteIntakeHistoryDetail {

        @DisplayName("삭제해도 음용 세부 기록이 남아있는 경우 음용 기록을 남긴 채로 세부 기록만 삭제한다")
        @Test
        void deleteIntakeDetail_keepsHistory_whenNotAllDetailsDeleted() throws Exception {
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .targetIntakeAmount(new TargetAmount(2000))
                    .date(LocalDate.now())
                    .build();

            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail1 = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(10, 0))
                    .build();

            IntakeHistoryDetail intakeHistoryDetail2 = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(10, 0))
                    .build();

            intakeHistoryDetailRepository.saveAll(List.of(intakeHistoryDetail1, intakeHistoryDetail2));

            mockMvc.perform(delete("/intake/history/details/{id}", intakeHistoryDetail2.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());

            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(member);

            assertSoftly(softly -> {
                softly.assertThat(intakeHistories.size()).isEqualTo(1);
                softly.assertThat(intakeHistories.getFirst().getTargetAmount().value()).isEqualTo(2000);
            });
        }

        @DisplayName("음용 세부 기록이 남아있지 않은 경우 음용 기록을 삭제한다")
        @Test
        void deleteIntakeDetail_deleteHistory_whenAllDetailsDeleted() throws Exception {
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .targetIntakeAmount(new TargetAmount(2000))
                    .date(LocalDate.now())
                    .build();

            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail1 = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .time(LocalTime.of(10, 0))
                    .build();

            intakeHistoryDetailRepository.save(intakeHistoryDetail1);

            mockMvc.perform(delete("/intake/history/details/{id}", intakeHistoryDetail1.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());

            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMember(member);

            assertSoftly(softly -> {
                softly.assertThat(intakeHistories.size()).isEqualTo(0);
            });
        }
    }
}
