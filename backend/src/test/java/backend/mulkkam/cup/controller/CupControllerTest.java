package backend.mulkkam.cup.controller;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_METHOD_ARGUMENT;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.NOT_ALL_MEMBER_CUPS_INCLUDED;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_CUP_RANKS;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_CUP;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_INTAKE_TYPE;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.domain.vo.CupRank;
import backend.mulkkam.cup.dto.CupRankDto;
import backend.mulkkam.cup.dto.request.CreateCupRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRequest;
import backend.mulkkam.cup.dto.response.CupsRanksResponse;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.CupFixtureBuilder;
import backend.mulkkam.support.DatabaseCleaner;
import backend.mulkkam.support.MemberFixtureBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private CupRepository cupRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ObjectMapper objectMapper;

    private Member savedMember;

    private String token;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();

        Member member = MemberFixtureBuilder
                .builder().build();
        savedMember = memberRepository.save(member);

        OauthAccount oauthAccount = new OauthAccount(member, "testId", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccount);

        token = oauthJwtTokenHandler.createAccessToken(oauthAccount);
    }

    @DisplayName("컵을 생성한다")
    @Nested
    class Create {

        @DisplayName("요청하는 컵 데이터가 올바르게 들어왔을 때 컵을 생성한다")
        @Test
        void success_validInput() throws Exception {
            // given
            CreateCupRequest createCupRequest = new CreateCupRequest("머그컵", 350, "WATER", "☕");

            // when & then
            mockMvc.perform(post("/cups")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(createCupRequest)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("음용 타입이 잘못 들어왔을 때 예외를 던진다")
        @Test
        void error_invalidIntakeType() throws Exception {
            // given
            CreateCupRequest createCupRequest = new CreateCupRequest("머그컵", 350, "CAR", "☕");

            // when & then
            String json = mockMvc.perform(post("/cups")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(createCupRequest)))
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(NOT_FOUND_INTAKE_TYPE.name());
            });
        }
    }

    @DisplayName("컵의 랭크들을 변경한다")
    @Nested
    class UpdateRanks {

        @BeforeEach
        void setUp() {
            List<Cup> cups = List.of(
                    CupFixtureBuilder.withMember(savedMember)
                            .cupNickname(new CupNickname("cup1"))
                            .cupRank(new CupRank(1))
                            .build(),
                    CupFixtureBuilder.withMember(savedMember)
                            .cupNickname(new CupNickname("cup2"))
                            .cupRank(new CupRank(2))
                            .build(),
                    CupFixtureBuilder.withMember(savedMember)
                            .cupNickname(new CupNickname("cup3"))
                            .cupRank(new CupRank(3))
                            .build()
            );
            cupRepository.saveAll(cups);
        }

        @DisplayName("요청하는 데이터가 올바르게 들어왔을 때 컵의 랭크들을 수정한다")
        @Test
        void success_validInput() throws Exception {
            // given
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(List.of(
                    new CupRankDto(1L, 3),
                    new CupRankDto(2L, 2),
                    new CupRankDto(3L, 1)
            ));

            // when & then
            String json = mockMvc.perform(put("/cups/ranks")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            CupsRanksResponse actual = objectMapper.readValue(json, CupsRanksResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.cups().size()).isEqualTo(3);
                softly.assertThat(actual.cups().get(0).rank())
                        .isEqualTo(3);
                softly.assertThat(actual.cups().get(1).rank())
                        .isEqualTo(2);
                softly.assertThat(actual.cups().get(2).rank())
                        .isEqualTo(1);
            });
        }

        @DisplayName("멤버의 모든 컵이 아닌 일부만을 데이터로 요청 보냈을 때 예외를 발생시킨다")
        @Test
        void error_notAllMemberCupsIncluded() throws Exception {
            // given
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(List.of(
                    new CupRankDto(1L, 1),
                    new CupRankDto(2L, 2)
            ));

            // when & then
            String json = mockMvc.perform(put("/cups/ranks")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(NOT_ALL_MEMBER_CUPS_INCLUDED.name());
            });
        }

        @DisplayName("랭크에서 하나가 null이 들어왔을 때 예외가 발생한다")
        @Test
        void error_rankIsNull() throws Exception {
            // given
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(List.of(
                    new CupRankDto(1L, null),
                    new CupRankDto(2L, 2),
                    new CupRankDto(3L, 3)
            ));

            // when & then
            String json = mockMvc.perform(put("/cups/ranks")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(INVALID_METHOD_ARGUMENT.name());
            });
        }

        @DisplayName("존재하지 않는 컵일 때 예외가 발생한다")
        @Test
        void error_notFoundCup() throws Exception {
            // given
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(List.of(
                    new CupRankDto(1L, 1),
                    new CupRankDto(2L, 2),
                    new CupRankDto(4L, 3)
            ));

            // when & then
            String json = mockMvc.perform(put("/cups/ranks")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(NOT_FOUND_CUP.name());
            });
        }

        @DisplayName("멤버에 해당하는 컵이 아닐 때 예외가 발생한다")
        @Test
        void error_notPermittedForCup() throws Exception {
            // given
            Member otherMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("other"))
                    .build();
            Member savedOtherMember = memberRepository.save(otherMember);

            OauthAccount oauthAccount = new OauthAccount(otherMember, "testId", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            Cup otherCup = CupFixtureBuilder.withMember(savedOtherMember)
                    .cupNickname(new CupNickname("otherCup"))
                    .build();
            cupRepository.save(otherCup);

            UpdateCupRanksRequest request = new UpdateCupRanksRequest(List.of(
                    new CupRankDto(1L, 1),
                    new CupRankDto(2L, 2),
                    new CupRankDto(4L, 3)
            ));

            // when & then
            String json = mockMvc.perform(put("/cups/ranks")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(NOT_PERMITTED_FOR_CUP.name());
            });
        }

        @DisplayName("요청애 랭크가 중복적으로 들어왔을 때 예외를 발생시킨다")
        @Test
        void error_duplicatedCupRanks() throws Exception {
            // given
            UpdateCupRanksRequest request = new UpdateCupRanksRequest(List.of(
                    new CupRankDto(1L, 2),
                    new CupRankDto(2L, 2),
                    new CupRankDto(4L, 3)
            ));

            // when & then
            String json = mockMvc.perform(put("/cups/ranks")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(DUPLICATED_CUP_RANKS.name());
            });
        }
    }

    @DisplayName("컵을 수정한다")
    @Nested
    class ModifyNicknameAndAmount {

        private Long savedCupId;

        @BeforeEach
        void setUp() {
            Cup cup = CupFixtureBuilder.withMember(savedMember)
                    .cupNickname(new CupNickname("c0c0m0b"))
                    .build();
            Cup savedCup = cupRepository.save(cup);
            savedCupId = savedCup.getId();
        }

        @DisplayName("올바른 데이터로 요청하면 컵을 수정한다")
        @Test
        void success_validInput() throws Exception {
            // given
            UpdateCupRequest updateCupRequest = new UpdateCupRequest("c0c0m0a", 100, IntakeType.WATER, "example");

            // when & then
            mockMvc.perform(patch("/cups/" + savedCupId)
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(updateCupRequest)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @DisplayName("존재하지 않는 컵의 ID로 요청하면 예외가 발생한다")
        @Test
        void error_notFoundCup() throws Exception {
            // given
            UpdateCupRequest updateCupRequest = new UpdateCupRequest("c0c0m0a", 100, IntakeType.WATER, "example");

            // when & then
            String json = mockMvc.perform(patch("/cups/" + Long.MAX_VALUE)
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(updateCupRequest)))
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(NOT_FOUND_CUP.name());
            });
        }

        @DisplayName("멤버에 해당하는 컵이 아니라면 예외가 발생한다")
        @Test
        void error_notPermittedForCup() throws Exception {
            // given
            Member otherMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("other"))
                    .build();
            Member savedOtherMember = memberRepository.save(otherMember);

            OauthAccount oauthAccount = new OauthAccount(otherMember, "testId", OauthProvider.KAKAO);
            oauthAccountRepository.save(oauthAccount);

            Cup otherCup = CupFixtureBuilder.withMember(savedOtherMember)
                    .cupNickname(new CupNickname("otherCup"))
                    .build();

            Cup savedOtherCup = cupRepository.save(otherCup);
            Long savedOtherCupId = savedOtherCup.getId();

            UpdateCupRequest updateCupRequest = new UpdateCupRequest("c0c0m0a", 100, IntakeType.WATER, "example");

            // when & then
            String json = mockMvc.perform(patch("/cups/" + savedOtherCupId)
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .content(objectMapper.writeValueAsString(updateCupRequest)))
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(NOT_PERMITTED_FOR_CUP.name());
            });
        }
    }

    @DisplayName("컵을 삭제할 때")
    @Nested
    class Delete {

        private Long savedCupId;

        @BeforeEach
        void setUp() {
            Cup cup = CupFixtureBuilder.withMember(savedMember)
                    .cupNickname(new CupNickname("c0c0m0b"))
                    .build();
            Cup savedCup = cupRepository.save(cup);
            savedCupId = savedCup.getId();
        }

        @DisplayName("올바른 데이터로 요청하면 컵을 삭제한다")
        @Test
        void success_validInput() throws Exception {
            mockMvc.perform(delete("/cups/" + savedCupId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @DisplayName("존재하지 않는 컵 ID로 요청하면 예외가 발생한다")
        @Test
        void error_notFoundCup() throws Exception {
            String json = mockMvc.perform(delete("/cups/" + Long.MAX_VALUE)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(NOT_FOUND_CUP.name());
            });
        }

        @DisplayName("멤버의 컵이 아닌 ID로 요청하면 예외가 발생한다")
        @Test
        void error_notPermittedForCup() throws Exception {
            // given
            Member otherMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("other"))
                    .build();
            Member savedOtherMember = memberRepository.save(otherMember);

            Cup otherCup = CupFixtureBuilder.withMember(savedOtherMember)
                    .cupNickname(new CupNickname("otherCup"))
                    .build();

            Cup savedOtherCup = cupRepository.save(otherCup);
            Long savedOtherCupId = savedOtherCup.getId();

            // when & then
            String json = mockMvc.perform(delete("/cups/" + savedOtherCupId)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(NOT_FOUND_CUP.name());
            });
        }
    }

    @DisplayName("컵을 초기화시킬 때")
    @Nested
    class Reset {

        @DisplayName("기존에 컵이 있다면 모두 삭제시킨 후 기본 컵을 생성한다.")
        @Test
        void success_whenResettingDefaultCupsDeletesExistingCups() throws Exception {
            // given
            Cup cup = CupFixtureBuilder
                    .withMember(savedMember)
                    .build();
            cupRepository.save(cup);

            // when
            mockMvc.perform(put("/cups/reset")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());

            List<Cup> cups = cupRepository.findAllByMember(savedMember);

            // then
            assertSoftly(softly -> {
                softly.assertThat(cups.size()).isEqualTo(3);
                softly.assertThat(cups.getFirst().getNickname().value()).isEqualTo("종이컵");
                softly.assertThat(cups.get(1).getNickname().value()).isEqualTo("스타벅스 톨");
                softly.assertThat(cups.get(2).getNickname().value()).isEqualTo("스타벅스 그란데");
            });

        }
    }
}
