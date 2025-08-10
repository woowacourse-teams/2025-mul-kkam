package backend.mulkkam.intake.controller;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_AMOUNT;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.intake.dto.RecommendedIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.request.ModifyIntakeTargetAmountByRecommendRequest;
import backend.mulkkam.intake.dto.response.IntakeTargetAmountResponse;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.DatabaseCleaner;
import backend.mulkkam.support.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class IntakeAmountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private IntakeHistoryDetailRepository intakeHistoryDetailRepository;

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
                .targetAmount(new Amount(1500))
                .build();
        memberRepository.save(member);
        OauthAccount oauthAccount = new OauthAccount(member, "testId", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);
        token = oauthJwtTokenHandler.createToken(oauthAccount);
    }

    @DisplayName("Filter 검증")
    @Nested
    class AuthFilter {

        @DisplayName("GET /intake/amount/recommended 요청을 보낼 때")
        @Nested
        class GetRecommended {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() throws Exception {
                mockMvc.perform(get("/intake/amount/recommended")
                                .header("header", "Basic token"))
                        .andExpect(status().isUnauthorized());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() throws Exception {
                mockMvc.perform(get("/intake/amount/recommended")
                                .header(HttpHeaders.AUTHORIZATION, "Basic token"))
                        .andExpect(status().isUnauthorized());
            }
        }

        @DisplayName("GET /intake/amount/target 요청을 보낼 때")
        @Nested
        class GetTarget {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() throws Exception {
                mockMvc.perform(get("/intake/amount/target")
                                .header("header", "Basic token"))
                        .andExpect(status().isUnauthorized());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() throws Exception {
                mockMvc.perform(get("/intake/amount/target")
                                .header(HttpHeaders.AUTHORIZATION, "Basic token"))
                        .andExpect(status().isUnauthorized());
            }
        }

        @DisplayName("PATCH /intake/amount/target 요청을 보낼 때")
        @Nested
        class PatchTarget {

            @DisplayName("인증 헤더가 존재하지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_withoutAuthorizationHeader() throws Exception {
                mockMvc.perform(patch("/intake/amount/target")
                                .header("header", "Basic token"))
                        .andExpect(status().isUnauthorized());
            }

            @DisplayName("인증 헤더 형식이 올바르지 않는 경우, 401 에러가 발생한다.")
            @Test
            void error_invalidAuthorizationHeader() throws Exception {
                mockMvc.perform(patch("/intake/amount/target")
                                .header(HttpHeaders.AUTHORIZATION, "Basic token"))
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @DisplayName("목표 음용량을 추천받는다")
    @Nested
    class GetRecommended {

        @DisplayName("몸무게가 올바르게 설정되었을 경우 몸무게 * 30을 추천한다")
        @Test
        void success_whenGivenValidMemberWeight() throws Exception {
            String json = mockMvc.perform(get("/intake/amount/recommended")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            RecommendedIntakeAmountResponse actual = objectMapper.readValue(json,
                    RecommendedIntakeAmountResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(2100);
            });
        }

        @DisplayName("몸무게가 null일 경우 기본 값인 1800을 추천한다")
        @Test
        void success_whenGivenNullMemberWeight() throws Exception {
            databaseCleaner.clean();
            Member member = MemberFixtureBuilder
                    .builder()
                    .weight(null)
                    .build();
            memberRepository.save(member);
            OauthAccount oauthAccount = new OauthAccount(member, "testId2", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);
            token = oauthJwtTokenHandler.createToken(oauthAccount);

            String json = mockMvc.perform(get("/intake/amount/recommended")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            RecommendedIntakeAmountResponse actual = objectMapper.readValue(json,
                    RecommendedIntakeAmountResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(1800);
            });
        }

        @DisplayName("입력받은 몸무게 * 30을 추천한다")
        @Test
        void success_whenGivenValidWeight() throws Exception {
            String json = mockMvc.perform(get("/intake/amount/recommended")
                            .param("weight", "70")
                            .param("GENDER", "FEMALE")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            RecommendedIntakeAmountResponse actual = objectMapper.readValue(json,
                    RecommendedIntakeAmountResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(2100);
            });
        }

        @DisplayName("입력받은 몸무게가 null 이라면 60 * 30을 추천한다")
        @Test
        void success_whenGivenNullWeight() throws Exception {
            String json = mockMvc.perform(get("/intake/amount/target/recommended")
                            .param("weight", (String) null)
                            .param("GENDER", "FEMALE")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            RecommendedIntakeAmountResponse actual = objectMapper.readValue(json,
                    RecommendedIntakeAmountResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(1800);
            });

        }
    }

    @DisplayName("멤버의 음용량을 수정한다")
    @Nested
    class ModifyMemberTarget {

        @DisplayName("멤버의 음용량이 수정된다")
        @Test
        void success_whenIsValidAmount() throws Exception {
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(2500);

            mockMvc.perform(patch("/intake/amount/target")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(intakeTargetAmountModifyRequest)))
                    .andExpect(status().isOk());

            Member foundMember = memberRepository.findById(member.getId()).orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(foundMember.getTargetAmount().value()).isEqualTo(2500);
            });
        }

        @DisplayName("목표 음용량이 0 이하라면 400 에러가 발생한다")
        @Test
        void error_whenAmountIsLessThanOrEqualToZero() throws Exception {
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(0);

            String json = mockMvc.perform(patch("/intake/amount/target")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(intakeTargetAmountModifyRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(INVALID_AMOUNT.name());
            });
        }

        @DisplayName("목표 음용량이 10000 이상이라면 400 에러가 발생한다")
        @Test
        void error_whenAmountIsMoreThanOrEqualTo10000() throws Exception {
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(
                    10000);

            String json = mockMvc.perform(patch("/intake/amount/target")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(intakeTargetAmountModifyRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(INVALID_AMOUNT.name());
            });

        }
    }

    @DisplayName("음용 기록의 목표 음용량을 수정한다")
    @Nested
    class ModifyIntakeHistoryTarget {

        @DisplayName("음용기록의 하루 목표 음용량이 수정된다")
        @Test
        void success_whenIsValidSuggestedAmount() throws Exception {
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .targetIntakeAmount(new Amount(1000))
                    .date(LocalDate.now())
                    .build();

            intakeHistoryRepository.save(intakeHistory);

            IntakeHistoryDetail intakeHistoryDetail = IntakeHistoryDetailFixtureBuilder
                    .withIntakeHistory(intakeHistory)
                    .build();

            intakeHistoryDetailRepository.save(intakeHistoryDetail);

            ModifyIntakeTargetAmountByRecommendRequest modifyIntakeTargetAmountByRecommendRequest = new ModifyIntakeTargetAmountByRecommendRequest(
                    2500);

            mockMvc.perform(patch("/intake/amount/target/suggested")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyIntakeTargetAmountByRecommendRequest)))
                    .andExpect(status().isOk());

            Member foundMember = memberRepository.findById(member.getId())
                    .orElseThrow();

            List<IntakeHistory> foundIntakeHistories = intakeHistoryRepository.findAllByMemberAndHistoryDateBetween(
                    member, LocalDate.now(), LocalDate.now());

            assertSoftly(softly -> {
                softly.assertThat(foundMember.getTargetAmount().value()).isEqualTo(1500);
                softly.assertThat(foundIntakeHistories.getFirst().getTargetAmount().value()).isEqualTo(2500);
            });
        }

        @DisplayName("목표 음용량이 0 이하라면 400 에러가 발생한다")
        @Test
        void error_whenSuggestedAmountIsLessThanOrEqualToZero() throws Exception {
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .targetIntakeAmount(new Amount(1000))
                    .date(LocalDate.now())
                    .build();

            intakeHistoryRepository.save(intakeHistory);

            ModifyIntakeTargetAmountByRecommendRequest modifyIntakeTargetAmountByRecommendRequest = new ModifyIntakeTargetAmountByRecommendRequest(
                    0);

            String json = mockMvc.perform(patch("/intake/amount/target/suggested")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyIntakeTargetAmountByRecommendRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(INVALID_AMOUNT.name());
            });
        }

        @DisplayName("목표 음용량이 10000 이상이라면 400 에러가 발생한다")
        @Test
        void error_whenSuggestedAmountIsMoreThanOrEqualTo10000() throws Exception {
            IntakeHistory intakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .targetIntakeAmount(new Amount(1000))
                    .date(LocalDate.now())
                    .build();

            ModifyIntakeTargetAmountByRecommendRequest modifyIntakeTargetAmountByRecommendRequest = new ModifyIntakeTargetAmountByRecommendRequest(
                    10000);

            intakeHistoryRepository.save(intakeHistory);
            String json = mockMvc.perform(patch("/intake/amount/target/suggested")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyIntakeTargetAmountByRecommendRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(INVALID_AMOUNT.name());
            });
        }

        @DisplayName("그 날의 음용 기록이 없다면 새로운 음용 기록을 생성한다")
        @Test
        void success_whenIntakeHistoryNotFoundThenCreateIntakeHistory() throws Exception {
            ModifyIntakeTargetAmountByRecommendRequest modifyIntakeTargetAmountByRecommendRequest = new ModifyIntakeTargetAmountByRecommendRequest(
                    4500);

            mockMvc.perform(patch("/intake/amount/target/suggested")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyIntakeTargetAmountByRecommendRequest)))
                    .andExpect(status().isOk());

            List<IntakeHistory> intakeHistories = intakeHistoryRepository.findAllByMemberAndHistoryDateBetween(member,
                    LocalDate.now(), LocalDate.now());
            Member foundMember = memberRepository.findById(member.getId()).orElseThrow();

            assertSoftly(softly -> {
                softly.assertThat(foundMember.getTargetAmount().value()).isEqualTo(1500);
                softly.assertThat(intakeHistories.getFirst().getTargetAmount().value()).isEqualTo(4500);
            });

        }
    }

    @DisplayName("멤버의 목표 음용량을 얻는다")
    @Nested
    class GetTarget {

        @Test
        void success_isValidData() throws Exception {
            String json = mockMvc.perform(get("/intake/amount/target")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            IntakeTargetAmountResponse actual = objectMapper.readValue(json,
                    IntakeTargetAmountResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(1500);
            });

        }
    }
}
