package backend.mulkkam.intake.controller;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_TARGET_AMOUNT;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.intake.dto.RecommendedIntakeAmountResponse;
import backend.mulkkam.intake.dto.request.IntakeTargetAmountModifyRequest;
import backend.mulkkam.intake.dto.response.IntakeTargetAmountResponse;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.TargetAmount;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
class IntakeAmountControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final Member member = MemberFixtureBuilder
            .builder()
            .weight(70.0)
            .targetAmount(new TargetAmount(1500))
            .build();
    ;

    private final OauthAccount oauthAccount = new OauthAccount(member, "testId", OauthProvider.KAKAO);

    private String token;

    @BeforeEach
    void setUp() {
        memberRepository.save(member);
        oauthAccountRepository.save(oauthAccount);
        String deviceUuid = "deviceUuid";
        token = oauthJwtTokenHandler.createAccessToken(oauthAccount, deviceUuid);
    }

    @DisplayName("목표 음용량을 추천받을 때에")
    @Nested
    class GetRecommended {

        @DisplayName("몸무게가 올바르게 설정되었을 경우 몸무게 * 30을 추천한다")
        @Test
        void success_whenGivenValidMemberWeight() throws Exception {
            // when
            String json = mockMvc.perform(get("/intake/amount/recommended")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            RecommendedIntakeAmountResponse actual = objectMapper.readValue(json,
                    RecommendedIntakeAmountResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(2100);
            });
        }

        @DisplayName("몸무게가 null일 경우 기본 값인 1800을 추천한다")
        @Test
        void success_whenGivenNullMemberWeight() throws Exception {
            // given
            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("test2"))
                    .weight(null)
                    .build();
            memberRepository.save(member);
            OauthAccount oauthAccount = new OauthAccount(member, "testId2", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);
            String deviceUuid = "deviceUuid";

            token = oauthJwtTokenHandler.createAccessToken(oauthAccount, deviceUuid);

            // when
            String json = mockMvc.perform(get("/intake/amount/recommended")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            RecommendedIntakeAmountResponse actual = objectMapper.readValue(json,
                    RecommendedIntakeAmountResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(1800);
            });
        }

        @DisplayName("입력받은 몸무게 * 30을 추천한다")
        @Test
        void success_whenGivenValidWeight() throws Exception {
            // when
            String json = mockMvc.perform(get("/intake/amount/recommended")
                            .param("weight", "70")
                            .param("GENDER", "FEMALE")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            RecommendedIntakeAmountResponse actual = objectMapper.readValue(json,
                    RecommendedIntakeAmountResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(2100);
            });
        }

        @DisplayName("입력받은 몸무게가 null 이라면 60 * 30을 추천한다")
        @Test
        void success_whenGivenNullWeight() throws Exception {
            // when
            String json = mockMvc.perform(get("/intake/amount/target/recommended")
                            .param("weight", (String) null)
                            .param("GENDER", "FEMALE")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            RecommendedIntakeAmountResponse actual = objectMapper.readValue(json,
                    RecommendedIntakeAmountResponse.class);

            // then
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
            // given
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(2500);

            // when
            mockMvc.perform(patch("/intake/amount/target")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(intakeTargetAmountModifyRequest)))
                    .andExpect(status().isOk());

            Member foundMember = memberRepository.findById(member.getId()).orElseThrow();

            // then
            assertSoftly(softly -> {
                softly.assertThat(foundMember.getTargetAmount().value()).isEqualTo(2500);
            });
        }

        @DisplayName("목표 음용량이 0 이하라면 400 에러가 발생한다")
        @Test
        void error_whenAmountIsLessThanOrEqualToZero() throws Exception {
            // given
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(0);

            // when
            String json = mockMvc.perform(patch("/intake/amount/target")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(intakeTargetAmountModifyRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(INVALID_TARGET_AMOUNT.name());
            });
        }

        @DisplayName("목표 음용량이 5000 초과라면 400 에러가 발생한다")
        @Test
        void error_whenAmountIsMoreThanOrEqualTo5000() throws Exception {
            // given
            IntakeTargetAmountModifyRequest intakeTargetAmountModifyRequest = new IntakeTargetAmountModifyRequest(
                    5_001);

            // when
            String json = mockMvc.perform(patch("/intake/amount/target")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(intakeTargetAmountModifyRequest)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(INVALID_TARGET_AMOUNT.name());
            });
        }
    }

    @DisplayName("멤버의 목표 음용량을 조회할 때에")
    @Nested
    class GetTarget {

        @DisplayName("멤버의 음용량을 통해 값을 얻는다")
        @Test
        void success_isValidData() throws Exception {
            // when
            String json = mockMvc.perform(get("/intake/amount/target")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            IntakeTargetAmountResponse actual = objectMapper.readValue(json,
                    IntakeTargetAmountResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.amount()).isEqualTo(1500);
            });
        }
    }
}
