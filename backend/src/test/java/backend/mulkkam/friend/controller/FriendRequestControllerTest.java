package backend.mulkkam.friend.controller;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.ALREADY_ACCEPTED;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static backend.mulkkam.friend.domain.FriendRelationStatus.ACCEPTED;
import static backend.mulkkam.friend.domain.FriendRelationStatus.REQUESTED;
import static backend.mulkkam.friend.dto.request.PatchFriendStatusRequest.FriendRequestStatus.ACCEPT;
import static backend.mulkkam.friend.dto.request.PatchFriendStatusRequest.FriendRequestStatus.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.friend.dto.request.CreateFriendRequestRequest;
import backend.mulkkam.friend.dto.request.PatchFriendStatusRequest;
import backend.mulkkam.friend.dto.response.FriendRelationResponse;
import backend.mulkkam.friend.dto.response.GetReceivedFriendRequestCountResponse;
import backend.mulkkam.friend.dto.response.ReadReceivedFriendRelationResponse;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

class FriendRequestControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private FriendRelationRepository friendRelationRepository;

    private Member requester;
    private Member addressee;
    private String tokenOfRequester;
    private String tokenOfAddressee;

    @BeforeEach
    void setUp() {
        requester = MemberFixtureBuilder.builder().build();
        memberRepository.save(requester);

        addressee = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("칼리2")).build();
        memberRepository.save(addressee);

        OauthAccount oauthAccountOfRequester = new OauthAccount(requester, "testIdOfRequester",
                OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccountOfRequester);

        OauthAccount oauthAccountOfAddressee = new OauthAccount(addressee, "testIdOfAddressee",
                OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccountOfAddressee);

        tokenOfRequester = oauthJwtTokenHandler.createAccessToken(oauthAccountOfRequester, "requester");
        tokenOfAddressee = oauthJwtTokenHandler.createAccessToken(oauthAccountOfAddressee, "addressee");
    }

    @DisplayName("친구 요청을 보낼 때")
    @Nested
    class create {

        @DisplayName("올바른 요청이 들어온 경우, '요청' 상태의 엔티티가 성공적으로 생성된다.")
        @Test
        void success() throws Exception {
            // given
            CreateFriendRequestRequest request = new CreateFriendRequestRequest(addressee.getId());

            // when
            mockMvc.perform(post("/friend-requests")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            // then
            List<FriendRelation> relations = friendRelationRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(relations).hasSize(1);
                softly.assertThat(relations.getFirst().getRequesterId()).isEqualTo(requester.getId());
                softly.assertThat(relations.getFirst().getAddresseeId()).isEqualTo(addressee.getId());
                softly.assertThat(relations.getFirst().getFriendRelationStatus()).isSameAs(REQUESTED);
            });
        }

        @DisplayName("요청자와 수신자가 동일한 경우 예외가 발생한다.")
        @Test
        void error_sameIds() throws Exception {
            // given
            CreateFriendRequestRequest request = new CreateFriendRequestRequest(requester.getId());

            // when
            String json = mockMvc.perform(post("/friend-requests")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertThat(actual.getCode()).isEqualTo(INVALID_FRIEND_REQUEST.name());
        }

        @DisplayName("친구 신청 대상자가 존재하지 않은 경우 예외가 발생한다.")
        @Test
        void error_notFoundAddressee() throws Exception {
            // given
            CreateFriendRequestRequest request = new CreateFriendRequestRequest(999L);

            // when
            String json = mockMvc.perform(post("/friend-requests")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertThat(actual.getCode()).isEqualTo(NOT_FOUND_MEMBER.name());
        }

        @DisplayName("요청자 - 수신자 사이에 친구 신청이 이미 존재하는 경우 예외가 발생한다.")
        @Test
        void error_alreadyExistsFriendRequest() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(addressee.getId(), requester.getId(), FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);
            CreateFriendRequestRequest request = new CreateFriendRequestRequest(addressee.getId());

            // when
            String json = mockMvc.perform(post("/friend-requests")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertThat(actual.getCode()).isEqualTo(DUPLICATED_FRIEND_REQUEST.name());
        }

        @DisplayName("요청자 - 수신자가 이미 친구 관계인 경우 예외가 발생한다.")
        @Test
        void error_alreadyFriend() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(addressee.getId(), requester.getId(), ACCEPTED);
            friendRelationRepository.save(friendRelation);
            CreateFriendRequestRequest request = new CreateFriendRequestRequest(addressee.getId());

            // when
            String json = mockMvc.perform(post("/friend-requests")
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertThat(actual.getCode()).isEqualTo(DUPLICATED_FRIEND_REQUEST.name());
        }
    }

    @DisplayName("친구 요청을 취소할 때")
    @Nested
    class Cancel {

        @DisplayName("친구 신청자가 요청을 취소할 수 있다.")
        @Test
        void success_requestByRequester() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(), REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when
            mockMvc.perform(delete("/friend-requests/" + friendRelation.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            // then
            assertThat(friendRelationRepository.findAll()).isEmpty();
        }

        @DisplayName("이미 수락된 요청인 경우 예외가 발생한다.")
        @Test
        void error_alreadyAccepted() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(), ACCEPTED);
            friendRelationRepository.save(friendRelation);

            // when
            String json = mockMvc.perform(delete("/friend-requests/" + friendRelation.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertThat(actual.getCode()).isEqualTo(ALREADY_ACCEPTED.name());
        }

        @DisplayName("요청자가 아닌 유저는 친구 신청을 취소할 수 없다.")
        @Test
        void error_requestByOther() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(), REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when
            String json = mockMvc.perform(delete("/friend-requests/" + friendRelation.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertThat(actual.getCode()).isEqualTo(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST.name());
        }
    }

    @DisplayName("친구 요청 상태를 수정할 때")
    @Nested
    class Update {

        @DisplayName("정상적으로 거절된다")
        @Test
        void success_rejected() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(), REQUESTED);
            friendRelationRepository.save(friendRelation);
            PatchFriendStatusRequest request = new PatchFriendStatusRequest(REJECT);

            // when
            mockMvc.perform(patch("/friend-requests/" + friendRelation.getId())
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk());

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(friendRelations).hasSize(0);
            });
        }

        @DisplayName("정상적으로 수락된다")
        @Test
        void success_accepted() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(), REQUESTED);
            friendRelationRepository.save(friendRelation);
            PatchFriendStatusRequest request = new PatchFriendStatusRequest(ACCEPT);

            // when
            mockMvc.perform(patch("/friend-requests/" + friendRelation.getId())
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION,
                                    "Bearer " + tokenOfAddressee)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk());

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(friendRelations).hasSize(1);
                softly.assertThat(friendRelations.getFirst().getRequesterId()).isEqualTo(requester.getId());
                softly.assertThat(friendRelations.getFirst().getAddresseeId()).isEqualTo(addressee.getId());
                softly.assertThat(friendRelations.getFirst().getFriendRelationStatus()).isEqualTo(ACCEPTED);
            });
        }

        @DisplayName("친구 신청 수신자가 아닌 멤버가 요청한 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"REJECT", "ACCEPT"})
        void error_acceptByOtherMember(String status) throws Exception {
            // given
            Member other = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("똥똥이"))
                    .build();
            memberRepository.save(other);

            OauthAccount oauthAccountOfOther = new OauthAccount(
                    other,
                    "testIdOfOther",
                    OauthProvider.KAKAO
            );
            oauthAccountRepository.save(oauthAccountOfOther);
            String tokenOfOther = oauthJwtTokenHandler.createAccessToken(oauthAccountOfOther, "other");

            FriendRelation friendRelation = new FriendRelation(
                    requester.getId(),
                    addressee.getId(),
                    REQUESTED
            );
            friendRelationRepository.save(friendRelation);
            PatchFriendStatusRequest request = new PatchFriendStatusRequest(
                    PatchFriendStatusRequest.FriendRequestStatus.valueOf(status)
            );

            // when
            String json = mockMvc.perform(patch("/friend-requests/" + friendRelation.getId())
                            .contentType(APPLICATION_JSON)
                            .header(HttpHeaders.AUTHORIZATION,
                                    "Bearer " + tokenOfOther)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().is4xxClientError())
                    .andReturn().getResponse().getContentAsString();
            FailureBody actual = objectMapper.readValue(json, FailureBody.class);

            // then
            assertThat(actual.getCode()).isEqualTo(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST.name());
        }
    }

    @DisplayName("받은 친구 신청 목록 조회 시")
    @Nested
    class GetReceived {

        @DisplayName("첫 페이지 조회 시 정상적으로 반환된다")
        @Test
        void success_firstPage() throws Exception {
            // given
            Member requester2 = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("requester2"))
                    .build();
            memberRepository.save(requester2);

            FriendRelation friendRelation1 = new FriendRelation(requester.getId(), addressee.getId(), REQUESTED);
            FriendRelation friendRelation2 = new FriendRelation(requester2.getId(), addressee.getId(), REQUESTED);
            friendRelationRepository.saveAll(List.of(friendRelation1, friendRelation2));

            // when
            String json = mockMvc.perform(get("/friend-requests/received")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee)
                            .param("size", "20"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ReadReceivedFriendRelationResponse actual = objectMapper.readValue(json,
                    ReadReceivedFriendRelationResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.results()).hasSize(2);
                softly.assertThat(actual.hasNext()).isFalse();
            });
        }

        @DisplayName("다음 페이지 조회 시 커서 기반 페이징이 적용된다")
        @Test
        void success_nextPage() throws Exception {
            // given
            List<FriendRelation> friendRelations = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                Member sender = MemberFixtureBuilder.builder()
                        .memberNickname(new MemberNickname("sender" + i))
                        .build();
                memberRepository.save(sender);
                friendRelations.add(new FriendRelation(sender.getId(), requester.getId(), REQUESTED));
            }
            friendRelationRepository.saveAll(friendRelations);

            // when - 첫 페이지 조회
            String firstPageJson = mockMvc.perform(get("/friend-requests/received")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .param("size", "20"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ReadReceivedFriendRelationResponse firstPage = objectMapper.readValue(firstPageJson,
                    ReadReceivedFriendRelationResponse.class);

            // when - 다음 페이지 조회
            String secondPageJson = mockMvc.perform(get("/friend-requests/received")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .param("lastId", String.valueOf(firstPage.nextId()))
                            .param("size", "20"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ReadReceivedFriendRelationResponse secondPage = objectMapper.readValue(secondPageJson,
                    ReadReceivedFriendRelationResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(firstPage.results()).hasSize(20);
                softly.assertThat(firstPage.hasNext()).isTrue();
                softly.assertThat(secondPage.results()).hasSize(5);
                softly.assertThat(secondPage.hasNext()).isFalse();
            });
        }

        @DisplayName("lastId를 사용한 페이지네이션이 정상적으로 동작한다.")
        @Test
        void success_withPagination() throws Exception {
            // given
            Member member1 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원1")).build();
            Member member2 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원2")).build();
            Member member3 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원3")).build();
            memberRepository.saveAll(List.of(member1, member2, member3));

            FriendRelation friendRelation1 = new FriendRelation(member1.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            FriendRelation friendRelation2 = new FriendRelation(member2.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            FriendRelation friendRelation3 = new FriendRelation(member3.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.saveAll(List.of(friendRelation1, friendRelation2, friendRelation3));

            // when - 첫 번째 페이지 조회
            String firstPageContent = mockMvc.perform(get("/friend-requests/received")
                            .param("size", "2")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadReceivedFriendRelationResponse firstPageResponse = objectMapper.readValue(
                    firstPageContent, ReadReceivedFriendRelationResponse.class);

            // then - 첫 번째 페이지 검증
            assertSoftly(softly -> {
                softly.assertThat(firstPageResponse.results()).hasSize(2);
                softly.assertThat(firstPageResponse.hasNext()).isTrue();
            });

            // when - 두 번째 페이지 조회
            String secondPageContent = mockMvc.perform(get("/friend-requests/received")
                            .param("lastId", firstPageResponse.nextId().toString())
                            .param("size", "2")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadReceivedFriendRelationResponse secondPageResponse = objectMapper.readValue(
                    secondPageContent, ReadReceivedFriendRelationResponse.class);

            // then - 두 번째 페이지 검증
            assertSoftly(softly -> {
                softly.assertThat(secondPageResponse.results()).hasSize(1);
                softly.assertThat(secondPageResponse.hasNext()).isFalse();
            });
        }

        @DisplayName("받은 친구 신청이 없는 경우 빈 목록이 반환된다.")
        @Test
        void success_emptyList() throws Exception {
            // when
            String resultContent = mockMvc.perform(get("/friend-requests/received")
                            .param("size", "10")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadReceivedFriendRelationResponse response = objectMapper.readValue(
                    resultContent, ReadReceivedFriendRelationResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.results()).isEmpty();
                softly.assertThat(response.hasNext()).isFalse();
                softly.assertThat(response.nextId()).isNull();
            });
        }

        @DisplayName("size보다 많은 데이터가 있을 때 hasNext가 true이다.")
        @Test
        void success_hasNextTrue() throws Exception {
            // given
            Member member1 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원1")).build();
            Member member2 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원2")).build();
            memberRepository.saveAll(List.of(member1, member2));

            FriendRelation friendRelation1 = new FriendRelation(member1.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            FriendRelation friendRelation2 = new FriendRelation(member2.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.saveAll(List.of(friendRelation1, friendRelation2));

            // when
            String resultContent = mockMvc.perform(get("/friend-requests/received")
                            .param("size", "1")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadReceivedFriendRelationResponse response = objectMapper.readValue(
                    resultContent, ReadReceivedFriendRelationResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.results()).hasSize(1);
                softly.assertThat(response.hasNext()).isTrue();
                softly.assertThat(response.nextId()).isNotNull();
            });
        }

        @DisplayName("size 파라미터가 결과 개수를 제한한다")
        @Test
        void success_sizeLimit() throws Exception {
            // given
            List<FriendRelation> friendRelations = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Member sender = MemberFixtureBuilder.builder()
                        .memberNickname(new MemberNickname("sender" + i))
                        .build();
                memberRepository.save(sender);
                friendRelations.add(new FriendRelation(sender.getId(), requester.getId(), REQUESTED));
            }
            friendRelationRepository.saveAll(friendRelations);

            // when
            String json = mockMvc.perform(get("/friend-requests/received")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ReadReceivedFriendRelationResponse actual = objectMapper.readValue(json,
                    ReadReceivedFriendRelationResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.results()).hasSize(5);
                softly.assertThat(actual.hasNext()).isTrue();
            });
        }
    }

    @DisplayName("받은 친구 신청 개수 조회 시")
    @Nested
    class GetReceivedCount {

        @DisplayName("정상적으로 개수를 조회한다.")
        @Test
        void success_getCount() throws Exception {
            // given
            Member member1 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원1")).build();
            Member member2 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원2")).build();
            memberRepository.saveAll(List.of(member1, member2));

            FriendRelation request1 = new FriendRelation(member1.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            FriendRelation request2 = new FriendRelation(member2.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.saveAll(List.of(request1, request2));

            // when
            String resultContent = mockMvc.perform(get("/friend-requests/received-count")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            GetReceivedFriendRequestCountResponse response = objectMapper.readValue(
                    resultContent, GetReceivedFriendRequestCountResponse.class);

            // then
            assertThat(response.count()).isEqualTo(2L);
        }

        @DisplayName("받은 친구 신청이 없는 경우 0을 반환한다.")
        @Test
        void success_zeroCount() throws Exception {
            // when
            String resultContent = mockMvc.perform(get("/friend-requests/received-count")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            GetReceivedFriendRequestCountResponse response = objectMapper.readValue(
                    resultContent, GetReceivedFriendRequestCountResponse.class);

            // then
            assertThat(response.count()).isEqualTo(0L);
        }
    }

    @DisplayName("친구 목록을 조회할 때")
    @Nested
    class Read {

        @DisplayName("정상적으로 조회된다")
        @Test
        void success() throws Exception {
            // given
            List<Member> addressees = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Member member = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("히로" + i)).build();
                memberRepository.save(member);
                addressees.add(member);

                FriendRelation friendRelation = new FriendRelation(requester.getId(), member.getId(),
                        FriendRelationStatus.ACCEPTED);
                friendRelationRepository.save(friendRelation);
            }

            // when
            MvcResult result = mockMvc.perform(get("/friends")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = result.getResponse().getContentAsString();

            FriendRelationResponse parsedResponse = objectMapper.readValue(jsonResponse, FriendRelationResponse.class);

            // then
            List<Long> memberIdsInRelation = parsedResponse.informationOfMembers().stream()
                    .map(FriendRelationResponse.MemberInfo::memberId)
                    .toList();

            List<Long> memberIdsOfAddressees = addressees.stream()
                    .map(Member::getId)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(memberIdsInRelation).hasSize(10);
                softly.assertThat(memberIdsInRelation).containsExactlyInAnyOrderElementsOf(memberIdsOfAddressees);
            });
        }
    }

    @DisplayName("보낸 친구 신청 목록 조회 시 ")
    @Nested
    class GetSent {

        @DisplayName("정상적으로 조회한다.")
        @Test
        void success_getCount() throws Exception {
            // given
            List<Long> idsOfFriendRelation = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Member member = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("히로" + i)).build();
                memberRepository.save(member);

                // 3의 배수인 경우에는 내가 신청을 받은 관계로 저장
                if (i % 3 == 0) {
                    FriendRelation friendRelation = new FriendRelation(member.getId(), requester.getId(),
                            FriendRelationStatus.REQUESTED);
                    friendRelationRepository.save(friendRelation);
                    continue;
                }

                // 짝수인 경우에는 내가 신청을 한 관계로 저장
                if (i % 2 == 0) {
                    FriendRelation friendRelation = new FriendRelation(requester.getId(), member.getId(),
                            FriendRelationStatus.REQUESTED);
                    friendRelationRepository.save(friendRelation);
                    idsOfFriendRelation.add(friendRelation.getId());
                }
            }

            // when
            String resultContent = mockMvc.perform(get("/friend-requests/sent")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadSentFriendRelationResponse response = objectMapper.readValue(
                    resultContent, ReadSentFriendRelationResponse.class);

            // then
            List<Long> actual = response.results().stream()
                    .map(ReadSentFriendRelationResponse.SentFriendRelationInfo::friendRequestId)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(actual).containsExactlyInAnyOrderElementsOf(idsOfFriendRelation);
            });
        }

        @DisplayName("첫 요청 시 (lastId가 null) 정상적으로 조회된다.")
        @Test
        void success_firstRequest() throws Exception {
            // given
            FriendRelation friendRelation1 = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("테스터"))
                    .build();

            Member foundMember = memberRepository.save(member);
            FriendRelation friendRelation2 = new FriendRelation(requester.getId(), foundMember.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.saveAll(List.of(friendRelation1, friendRelation2));

            // when
            String resultContent = mockMvc.perform(get("/friend-requests/sent")
                            .param("size", "10")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadSentFriendRelationResponse response = objectMapper.readValue(resultContent,
                    ReadSentFriendRelationResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.results()).hasSize(2);
                softly.assertThat(response.hasNext()).isFalse();
                softly.assertThat(response.nextId()).isNotNull();
            });
        }

        @DisplayName("ACCEPTED 상태의 친구 관계는 조회되지 않는다")
        @Test
        void success_excludeAcceptedRelations() throws Exception {
            // given
            Member addressee2 = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("addressee2"))
                    .build();
            memberRepository.save(addressee2);

            FriendRelation requestedRelation = new FriendRelation(requester.getId(), addressee.getId(), REQUESTED);
            FriendRelation acceptedRelation = new FriendRelation(requester.getId(), addressee2.getId(), ACCEPTED);
            friendRelationRepository.saveAll(List.of(requestedRelation, acceptedRelation));

            // when
            String json = mockMvc.perform(get("/friend-requests/sent")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester)
                            .param("size", "20"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ReadSentFriendRelationResponse actual = objectMapper.readValue(json,
                    ReadSentFriendRelationResponse.class);

            // then
            assertThat(actual.results()).hasSize(1);
        }
    }

    @DisplayName("lastId를 사용한 페이지네이션이 정상적으로 동작한다.")
    @Test
    void success_withPagination() throws Exception {
        // given
        Member member1 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원1")).build();
        Member member2 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원2")).build();
        Member member3 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원3")).build();
        memberRepository.saveAll(List.of(member1, member2, member3));

        FriendRelation friendRelation1 = new FriendRelation(requester.getId(), member1.getId(),
                FriendRelationStatus.REQUESTED);
        FriendRelation friendRelation2 = new FriendRelation(requester.getId(), member2.getId(),
                FriendRelationStatus.REQUESTED);
        FriendRelation friendRelation3 = new FriendRelation(requester.getId(), member3.getId(),
                FriendRelationStatus.REQUESTED);
        friendRelationRepository.saveAll(List.of(friendRelation1, friendRelation2, friendRelation3));

        // when - 첫 번째 페이지 조회
        String firstPageContent = mockMvc.perform(get("/friend-requests/sent")
                        .param("size", "2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReadSentFriendRelationResponse firstPageResponse = objectMapper.readValue(
                firstPageContent, ReadSentFriendRelationResponse.class);

        // then - 첫 번째 페이지 검증
        assertSoftly(softly -> {
            softly.assertThat(firstPageResponse.results()).hasSize(2);
            softly.assertThat(firstPageResponse.hasNext()).isTrue();
        });

        // when - 두 번째 페이지 조회
        String secondPageContent = mockMvc.perform(get("/friend-requests/sent")
                        .param("lastId", firstPageResponse.nextId().toString())
                        .param("size", "2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReadSentFriendRelationResponse secondPageResponse = objectMapper.readValue(
                secondPageContent, ReadSentFriendRelationResponse.class);

        // then - 두 번째 페이지 검증
        assertSoftly(softly -> {
            softly.assertThat(secondPageResponse.results()).hasSize(1);
            softly.assertThat(secondPageResponse.hasNext()).isFalse();
        });
    }

    @DisplayName("size보다 많은 데이터가 있을 때 hasNext가 true이다.")
    @Test
    void success_hasNextTrue() throws Exception {
        // given
        Member member1 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원1")).build();
        Member member2 = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("회원2")).build();
        memberRepository.saveAll(List.of(member1, member2));

        FriendRelation friendRelation1 = new FriendRelation(requester.getId(), member1.getId(),
                FriendRelationStatus.REQUESTED);
        FriendRelation friendRelation2 = new FriendRelation(requester.getId(), member2.getId(),
                FriendRelationStatus.REQUESTED);
        friendRelationRepository.saveAll(List.of(friendRelation1, friendRelation2));

        // when
        String resultContent = mockMvc.perform(get("/friend-requests/sent")
                        .param("size", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReadSentFriendRelationResponse response = objectMapper.readValue(
                resultContent, ReadSentFriendRelationResponse.class);

        // then
        assertSoftly(softly -> {
            softly.assertThat(response.results()).hasSize(1);
            softly.assertThat(response.hasNext()).isTrue();
            softly.assertThat(response.nextId()).isNotNull();
        });
    }
}
