package backend.mulkkam.member.controller;

import static backend.mulkkam.auth.domain.OauthProvider.KAKAO;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.SAME_AS_BEFORE_NICKNAME;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATE_MEMBER_NICKNAME;
import static backend.mulkkam.member.dto.response.MemberSearchItemResponse.Status;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
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
import backend.mulkkam.cup.domain.DefaultCup;
import backend.mulkkam.cup.domain.EmojiType;
import backend.mulkkam.cup.domain.IntakeType;
import backend.mulkkam.cup.repository.CupEmojiRepository;
import backend.mulkkam.cup.repository.CupRepository;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.intake.repository.IntakeHistoryDetailRepository;
import backend.mulkkam.intake.repository.IntakeHistoryRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.dto.request.ModifyIsMarketingNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.ModifyIsNightNotificationAgreedRequest;
import backend.mulkkam.member.dto.request.ModifyIsReminderEnabledRequest;
import backend.mulkkam.member.dto.response.MemberResponse;
import backend.mulkkam.member.dto.response.MemberSearchItemResponse;
import backend.mulkkam.member.dto.response.MemberSearchItemResponse.Direction;
import backend.mulkkam.member.dto.response.MemberSearchResponse;
import backend.mulkkam.member.dto.response.NotificationSettingsResponse;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.AccountRefreshTokenFixtureBuilder;
import backend.mulkkam.support.fixture.FriendRelationFixtureBuilder;
import backend.mulkkam.support.fixture.IntakeHistoryDetailFixtureBuilder;
import backend.mulkkam.support.fixture.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.fixture.cup.CupFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MemberControllerTest extends ControllerTest {

    private static final String defaultEmojiUrl = "url";

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

    @Autowired
    private FriendRelationRepository friendRelationRepository;

    private final Member member = MemberFixtureBuilder
            .builder()
            .isNightNotificationAgreed(true)
            .isMarketingNotificationAgreed(true)
            .weight(null)
            .gender(null)
            .isReminderEnabled(true)
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
        String deviceUuid = "deviceUuid";

        token = oauthJwtTokenHandler.createAccessToken(oauthAccount, deviceUuid);

        saveDefaultCupEmojis();

        cupEmoji = cupEmojiRepository.findAll().getFirst();

        Cup cup = CupFixtureBuilder
                .withMemberAndCupEmoji(member, cupEmoji)
                .build();
        savedCup = cupRepository.save(cup);
    }

    private void saveDefaultCupEmojis() {
        for (IntakeType intakeType : IntakeType.values()) {
            DefaultCup.of(intakeType);
            CupEmoji cupEmoji = new CupEmoji(defaultEmojiUrl);
            cupEmoji.setEmojiType(intakeType, EmojiType.DEFAULT);
            cupEmojiRepository.save(cupEmoji);
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
                            .header(AUTHORIZATION, "Bearer " + token)
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
                            .header(AUTHORIZATION, "Bearer " + token)
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
                            .header(AUTHORIZATION, "Bearer " + token))
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
                            .header(AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            MemberResponse actual = objectMapper.readValue(json, MemberResponse.class);

            //then
            assertSoftly(softly -> {
                softly.assertThat(actual.weight()).isNull();
                softly.assertThat(actual.gender()).isNull();
            });
        }

        @DisplayName("리마인더 수신 여부 설정을 수정한다.")
        @Test
        void success_whenModifyReminderEnabled() throws Exception {
            //given
            ModifyIsReminderEnabledRequest modifyIsReminderEnabledRequest = new ModifyIsReminderEnabledRequest(false);

            // when
            mockMvc.perform(patch("/members/reminder")
                            .header(AUTHORIZATION, "Bearer " + token)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(modifyIsReminderEnabledRequest)))

                    .andExpect(status().isOk());
            Member foundMember = memberRepository.findById(member.getId()).orElseThrow();

            //then
            assertSoftly(softly -> {
                softly.assertThat(foundMember.isReminderEnabled()).isFalse();
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
            String deviceUuid = "deviceUuid";

            String otherToken = oauthJwtTokenHandler.createAccessToken(oauthAccount, deviceUuid);

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
                            .header(AUTHORIZATION, "Bearer " + otherToken))
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
                            .header(AUTHORIZATION, "Bearer " + token))
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
                            .header(AUTHORIZATION, "Bearer " + token))
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
                            .header(AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            // then
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.getCode()).isEqualTo(DUPLICATE_MEMBER_NICKNAME.name());
            });
        }
    }

    @DisplayName("회원 닉네임을 검색할 때에")
    @Nested
    class Search {

        @DisplayName("접두사에 맞는 모든 멤버를 가져온다.")
        @Test
        void success_whenBringAllNicknameMatchPrefix() throws Exception {
            // given

            Member member1 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("돈까스먹는환노"))
                    .build();
            Member member2 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("돈까스먹는공백"))
                    .build();
            Member member3 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("치즈동까스먹는체체"))
                    .build();
            memberRepository.saveAll(List.of(member1, member2, member3));

            // when
            String json = mockMvc.perform(get("/members/search")
                            .param("word", "돈까스")
                            .param("lastId", "")
                            .param("size", "10")
                            .header(AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // then
            MemberSearchResponse actual = objectMapper.readValue(json, MemberSearchResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.memberSearchItemResponses()).hasSize(2);
                softly.assertThat(actual.memberSearchItemResponses().getFirst().memberNickname()).isEqualTo("돈까스먹는공백");
                softly.assertThat(actual.memberSearchItemResponses().get(1).memberNickname()).isEqualTo("돈까스먹는환노");
                softly.assertThat(actual.hasNext()).isFalse();
            });
        }

        @DisplayName("빈 prefix로 검색 시 빈 결과를 반환한다.")
        @Test
        void success_whenPrefixIsBlank() throws Exception {
            // given
            Member member1 = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("테스트회원"))
                    .build();
            memberRepository.save(member1);

            // when
            String json = mockMvc.perform(get("/members/search")
                            .param("word", "")
                            .param("lastId", "")
                            .param("size", "10")
                            .header(AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // then
            MemberSearchResponse actual = objectMapper.readValue(json, MemberSearchResponse.class);
            assertSoftly(softly -> {
                softly.assertThat(actual.memberSearchItemResponses()).isEmpty();
                softly.assertThat(actual.hasNext()).isFalse();
            });
        }

        @DisplayName("페이지 크기보다 많은 결과가 있을 때 hasNext가 true이다.")
        @Test
        void success_whenHasNextPage() throws Exception {
            // given
            List<Member> members = List.of(
                    MemberFixtureBuilder.builder().memberNickname(new MemberNickname("테스트1")).build(),
                    MemberFixtureBuilder.builder().memberNickname(new MemberNickname("테스트2")).build(),
                    MemberFixtureBuilder.builder().memberNickname(new MemberNickname("테스트3")).build()
            );
            memberRepository.saveAll(members);

            // when
            String json = mockMvc.perform(get("/members/search")
                            .param("word", "테스트")
                            .param("lastId", "")
                            .param("size", "2")
                            .header(AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // then
            MemberSearchResponse actual = objectMapper.readValue(json, MemberSearchResponse.class);
            assertSoftly(softly -> {
                softly.assertThat(actual.memberSearchItemResponses()).hasSize(2);
                softly.assertThat(actual.hasNext()).isTrue();
            });
        }

        @DisplayName("친구 관계 상태별로 검색 결과가 다르게 반환된다.")
        @Test
        void success_whenSearchWithDifferentFriendStatus() throws Exception {
            // given
            Member acceptedFriend = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("테스트수락친구"))
                    .build();
            Member requestedByMe = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("테스트요청보냄"))
                    .build();
            Member requestedToMe = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("테스트요청받음"))
                    .build();
            Member noRelation = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("테스트관계없음"))
                    .build();

            memberRepository.saveAll(List.of(acceptedFriend, requestedByMe, requestedToMe, noRelation));

            FriendRelation acceptedRelation = FriendRelationFixtureBuilder
                    .builder()
                    .requesterId(member.getId())
                    .addresseeId(acceptedFriend.getId())
                    .friendStatus(FriendRelationStatus.ACCEPTED)
                    .build();

            FriendRelation sentRequest = FriendRelationFixtureBuilder
                    .builder()
                    .requesterId(member.getId())
                    .addresseeId(requestedByMe.getId())
                    .friendStatus(FriendRelationStatus.REQUESTED)
                    .build();

            FriendRelation receivedRequest = FriendRelationFixtureBuilder
                    .builder()
                    .requesterId(requestedToMe.getId())
                    .addresseeId(member.getId())
                    .friendStatus(FriendRelationStatus.REQUESTED)
                    .build();

            friendRelationRepository.saveAll(List.of(acceptedRelation, sentRequest, receivedRequest));

            // when
            String json = mockMvc.perform(get("/members/search")
                            .param("word", "테스트")
                            .param("lastId", "")
                            .param("size", "10")
                            .header(AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // then
            MemberSearchResponse actual = objectMapper.readValue(json, MemberSearchResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.memberSearchItemResponses()).hasSize(4);
                MemberSearchItemResponse acceptedItem = actual.memberSearchItemResponses().stream()
                        .filter(item -> item.memberNickname().equals("테스트수락친구"))
                        .findFirst()
                        .orElseThrow();
                softly.assertThat(acceptedItem.status()).isEqualTo(Status.ACCEPTED);
                softly.assertThat(acceptedItem.direction()).isEqualTo(MemberSearchItemResponse.Direction.NONE);

                MemberSearchItemResponse sentItem = actual.memberSearchItemResponses().stream()
                        .filter(item -> item.memberNickname().equals("테스트요청보냄"))
                        .findFirst()
                        .orElseThrow();
                softly.assertThat(sentItem.status()).isEqualTo(Status.REQUESTED);
                softly.assertThat(sentItem.direction()).isEqualTo(MemberSearchItemResponse.Direction.REQUESTED_BY_ME);

                MemberSearchItemResponse receivedItem = actual.memberSearchItemResponses().stream()
                        .filter(item -> item.memberNickname().equals("테스트요청받음"))
                        .findFirst()
                        .orElseThrow();
                softly.assertThat(receivedItem.status()).isEqualTo(Status.REQUESTED);
                softly.assertThat(receivedItem.direction())
                        .isEqualTo(MemberSearchItemResponse.Direction.REQUESTED_TO_ME);

                MemberSearchItemResponse noRelationItem = actual.memberSearchItemResponses().stream()
                        .filter(item -> item.memberNickname().equals("테스트관계없음"))
                        .findFirst()
                        .orElseThrow();
                softly.assertThat(noRelationItem.status()).isEqualTo(Status.NONE);
                softly.assertThat(noRelationItem.direction()).isEqualTo(MemberSearchItemResponse.Direction.NONE);
            });
        }

        @DisplayName("REQUESTED 상태에서 방향이 올바르게 표시된다.")
        @Test
        void success_whenDirectionIsCorrectForRequestedStatus() throws Exception {
            // given
            Member requestSentMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("요청보낸회원"))
                    .build();
            Member requestReceivedMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("요청받은회원"))
                    .build();

            memberRepository.saveAll(List.of(requestSentMember, requestReceivedMember));

            FriendRelation sentRelation = FriendRelationFixtureBuilder
                    .builder()
                    .requesterId(requestSentMember.getId())
                    .addresseeId(member.getId())
                    .friendStatus(FriendRelationStatus.REQUESTED)
                    .build();

            FriendRelation receivedRelation = FriendRelationFixtureBuilder
                    .builder()
                    .requesterId(member.getId())
                    .addresseeId(requestReceivedMember.getId())
                    .friendStatus(FriendRelationStatus.REQUESTED)
                    .build();

            friendRelationRepository.saveAll(List.of(sentRelation, receivedRelation));

            // when
            String json = mockMvc.perform(get("/members/search")
                            .param("word", "요청")
                            .param("lastId", "")
                            .param("size", "10")
                            .header(AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // then
            MemberSearchResponse actual = objectMapper.readValue(json, MemberSearchResponse.class);

            assertSoftly(softly -> {
                softly.assertThat(actual.memberSearchItemResponses()).hasSize(2);

                MemberSearchItemResponse sentItem = actual.memberSearchItemResponses().stream()
                        .filter(item -> item.memberNickname().equals("요청보낸회원"))
                        .findFirst()
                        .orElseThrow();
                softly.assertThat(sentItem.status()).isEqualTo(Status.REQUESTED);
                softly.assertThat(sentItem.direction()).isEqualTo(Direction.REQUESTED_TO_ME);

                MemberSearchItemResponse receivedItem = actual.memberSearchItemResponses().stream()
                        .filter(item -> item.memberNickname().equals("요청받은회원"))
                        .findFirst()
                        .orElseThrow();
                softly.assertThat(receivedItem.status()).isEqualTo(Status.REQUESTED);
                softly.assertThat(receivedItem.direction())
                        .isEqualTo(Direction.REQUESTED_BY_ME);
            });
        }
    }
}
