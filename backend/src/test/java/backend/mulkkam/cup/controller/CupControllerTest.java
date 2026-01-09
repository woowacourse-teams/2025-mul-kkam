package backend.mulkkam.cup.controller;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_METHOD_ARGUMENT;
import static backend.mulkkam.common.exception.errorCode.InternalServerErrorErrorCode.NOT_EXIST_DEFAULT_CUP_EMOJI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.cup.domain.CupEmoji;
import backend.mulkkam.cup.domain.DefaultCup;
import backend.mulkkam.cup.domain.EmojiType;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.domain.vo.CupNickname;
import backend.mulkkam.cup.dto.CupRankDto;
import backend.mulkkam.cup.dto.request.CreateCupWithoutRankRequest;
import backend.mulkkam.cup.dto.request.UpdateCupRanksRequest;
import backend.mulkkam.cup.dto.response.DefaultCupResponse;
import backend.mulkkam.cup.dto.response.DefaultCupsResponse;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.cup.dto.CreateCupWithoutRankRequestFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class CupControllerTest extends ControllerTest {

    private static final String defaultEmojiUrl = "url";

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private CupEmojiRepository cupEmojiRepository;

    private final AtomicLong oauthIdCounter = new AtomicLong(1);

    record AuthenticatedMember(Member member, OauthAccount oauthAccount, String token) {}

    private AuthenticatedMember createAuthenticatedMember() {
        Member member = memberRepository.save(MemberFixtureBuilder.builder().build());
        OauthAccount oauthAccount = oauthAccountRepository.save(
                new OauthAccount(member, "oauthId" + oauthIdCounter.getAndIncrement(), OauthProvider.KAKAO)
        );
        String token = oauthJwtTokenHandler.createAccessToken(oauthAccount, "deviceUuid");
        return new AuthenticatedMember(member, oauthAccount, token);
    }

    private CupEmoji saveDefaultCupEmojisAndGetFirst() {
        for (IntakeType intakeType : IntakeType.values()) {
            DefaultCup.of(intakeType);
            CupEmoji cupEmoji = new CupEmoji(defaultEmojiUrl);
            cupEmoji.setEmojiType(intakeType, EmojiType.DEFAULT);
            cupEmojiRepository.save(cupEmoji);
        }
        return cupEmojiRepository.findAll().getFirst();
    }

    @DisplayName("사용자 기본 컵 리스트 조회")
    @Nested
    class ReadDefault {

        @DisplayName("모든 기본 컵을 반환한다.")
        @Test
        void success_returns_all_default_cups() throws Exception {
            // given
            saveDefaultCupEmojisAndGetFirst();

            // when
            String json = mockMvc.perform(get("/cups/default"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            DefaultCupsResponse response = objectMapper.readValue(json, DefaultCupsResponse.class);
            List<String> actualNames = response.cups()
                    .stream()
                    .map(DefaultCupResponse::cupNickname)
                    .toList();
            List<String> expectedNames = Arrays.stream(DefaultCup.values())
                    .map(DefaultCup::getNickname)
                    .map(CupNickname::value)
                    .toList();

            // then
            assertThat(actualNames).containsExactlyInAnyOrderElementsOf(expectedNames);
        }

        @DisplayName("기본 이모지가 DB에 저장되어 있지 않은 경우, 500 에러가 발생한다.")
        @Test
        void fail_returns_500_when_default_emoji_not_exists() throws Exception {
            // given
            cupEmojiRepository.deleteAll();

            // when
            String json = mockMvc.perform(get("/cups/default"))
                    .andDo(print())
                    .andExpect(status().is5xxServerError())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertThat(actual.getCode()).isEqualTo(NOT_EXIST_DEFAULT_CUP_EMOJI.name());
        }
    }

    @DisplayName("컵을 생성한다")
    @Nested
    class CreateAtLastRank {

        @DisplayName("요청하는 컵 데이터가 올바르게 들어왔을 때 컵을 생성한다")
        @Test
        void success_cup_is_created_with_valid_input() throws Exception {
            // given
            AuthenticatedMember auth = createAuthenticatedMember();
            CupEmoji savedCupEmoji = saveDefaultCupEmojisAndGetFirst();

            CreateCupWithoutRankRequest createCupWithoutRankRequest = CreateCupWithoutRankRequestFixtureBuilder
                    .withCupEmojiId(savedCupEmoji.getId())
                    .build();

            // when & then
            mockMvc.perform(post("/cups")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.token())
                            .content(objectMapper.writeValueAsString(createCupWithoutRankRequest)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @DisplayName("컵의 랭크들을 변경한다")
    @Nested
    class UpdateRanks {

        @DisplayName("랭크에서 하나가 null이 들어왔을 때 예외가 발생한다")
        @Test
        void fail_rank_cannot_be_null() throws Exception {
            // given
            AuthenticatedMember auth = createAuthenticatedMember();
            saveDefaultCupEmojisAndGetFirst();

            UpdateCupRanksRequest request = new UpdateCupRanksRequest(List.of(
                    new CupRankDto(1L, null),
                    new CupRankDto(2L, 2),
                    new CupRankDto(3L, 3)
            ));

            // when & then
            String json = mockMvc.perform(put("/cups/ranks")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + auth.token())
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(INVALID_METHOD_ARGUMENT.name());
            });
        }
    }
}
