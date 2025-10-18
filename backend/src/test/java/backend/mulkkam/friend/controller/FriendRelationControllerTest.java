package backend.mulkkam.friend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.friend.dto.response.GetReceivedFriendRequestCountResponse;
import backend.mulkkam.friend.dto.response.ReadReceivedFriendRelationResponse;
import backend.mulkkam.friend.dto.FriendRelationResponse;
import backend.mulkkam.friend.dto.FriendRelationResponse.MemberInfo;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse.SentFriendRelationInfo;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

class FriendRelationControllerTest extends ControllerTest {

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

        tokenOfRequester = oauthJwtTokenHandler.createAccessToken(oauthAccountOfRequester, "temp");
        tokenOfAddressee = oauthJwtTokenHandler.createAccessToken(oauthAccountOfAddressee, "temp");
    }

    @DisplayName("친구 목록에서 친구를 삭제할 때")
    @Nested
    class DeleteFriendRelation {

        @DisplayName("요청자가 삭제하는 경우 정상적으로 삭제된다.")
        @Test
        void success_deleteByRequester() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.ACCEPTED);
            FriendRelation savedFriendRelation = friendRelationRepository.save(friendRelation);

            // when
            mockMvc.perform(delete("/friends/" + savedFriendRelation.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                    .andExpect(status().isNoContent());

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertThat(friendRelations).isEmpty();
        }

        @DisplayName("수락자가 삭제하는 경우 정상적으로 삭제된다.")
        @Test
        void success_deleteByAddressee() throws Exception {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.ACCEPTED);
            FriendRelation savedFriendRelation = friendRelationRepository.save(friendRelation);

            // when
            mockMvc.perform(delete("/friends/" + savedFriendRelation.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isNoContent());

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertThat(friendRelations).isEmpty();
        }

        @DisplayName("친구 요청을 거절할 때")
        @Nested
        class RejectFriendRelationRequest {

            @DisplayName("정상적으로 거절된다")
            @Test
            void success_rejected() throws Exception {
                // given
                FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                        FriendRelationStatus.REQUESTED);
                friendRelationRepository.save(friendRelation);

                // when
                mockMvc.perform(post("/friends/request/" + friendRelation.getId() + "/reject")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                        .andDo(print()).andExpect(status().isOk());

                // then
                List<FriendRelation> friendRelations = friendRelationRepository.findAll();
                assertSoftly(softly -> {
                    softly.assertThat(friendRelations).hasSize(0);
                });
            }
        }

        @DisplayName("친구 요청을 수락할 때")
        @Nested
        class AcceptFriendRelationRequest {

            @DisplayName("정상적으로 수락된다")
            @Test
            void success_accepted() throws Exception {
                // given
                FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                        FriendRelationStatus.REQUESTED);
                friendRelationRepository.save(friendRelation);

                // when
                mockMvc.perform(post("/friends/request/" + friendRelation.getId() + "/accept")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + tokenOfAddressee))
                        .andDo(print()).andExpect(status().isOk());

                // then
                List<FriendRelation> friendRelations = friendRelationRepository.findAll();
                assertSoftly(softly -> {
                    softly.assertThat(friendRelations).hasSize(1);
                    softly.assertThat(friendRelations.getFirst().getRequesterId()).isEqualTo(requester.getId());
                    softly.assertThat(friendRelations.getFirst().getAddresseeId()).isEqualTo(addressee.getId());
                    softly.assertThat(friendRelations.getFirst().getFriendRelationStatus())
                            .isEqualTo(FriendRelationStatus.ACCEPTED);
                });
            }
        }
    }

    @DisplayName("받은 친구 신청 목록 조회 시")
    @Nested
    class GetReceivedFriendRequests {

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
            FriendRelation friendRelation2 = new FriendRelation(foundMember.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.saveAll(List.of(friendRelation1, friendRelation2));

            // when
            String resultContent = mockMvc.perform(get("/friends/requests/received")
                            .param("size", "10")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadReceivedFriendRelationResponse response = objectMapper.readValue(resultContent,
                    ReadReceivedFriendRelationResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.friendRelationResponses()).hasSize(2);
                softly.assertThat(response.hasNext()).isFalse();
                softly.assertThat(response.nextId()).isNotNull();
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
            String firstPageContent = mockMvc.perform(get("/friends/requests/received")
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
                softly.assertThat(firstPageResponse.friendRelationResponses()).hasSize(2);
                softly.assertThat(firstPageResponse.hasNext()).isTrue();
            });

            // when - 두 번째 페이지 조회
            String secondPageContent = mockMvc.perform(get("/friends/requests/received")
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
                softly.assertThat(secondPageResponse.friendRelationResponses()).hasSize(1);
                softly.assertThat(secondPageResponse.hasNext()).isFalse();
            });
        }

        @DisplayName("받은 친구 신청이 없는 경우 빈 목록이 반환된다.")
        @Test
        void success_emptyList() throws Exception {
            // when
            String resultContent = mockMvc.perform(get("/friends/requests/received")
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
                softly.assertThat(response.friendRelationResponses()).isEmpty();
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
            String resultContent = mockMvc.perform(get("/friends/requests/received")
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
                softly.assertThat(response.friendRelationResponses()).hasSize(1);
                softly.assertThat(response.hasNext()).isTrue();
                softly.assertThat(response.nextId()).isNotNull();
            });
        }
    }

    @DisplayName("받은 친구 신청 개수 조회 시")
    @Nested
    class GetReceivedFriendRequestCount {

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
            String resultContent = mockMvc.perform(get("/friends/requests/received/count")
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
            String resultContent = mockMvc.perform(get("/friends/requests/received/count")
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
                    .map(MemberInfo::memberId)
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
    class GetSentFriendRequest {

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

                // 3의 배수인 경우에는 내가 신청을 한 관계로 저장
                if (i % 2 == 0) {
                    FriendRelation friendRelation = new FriendRelation(requester.getId(), member.getId(),
                            FriendRelationStatus.REQUESTED);
                    friendRelationRepository.save(friendRelation);
                    idsOfFriendRelation.add(friendRelation.getId());
                }
            }

            // when
            String resultContent = mockMvc.perform(get("/friends/requests/sent")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadSentFriendRelationResponse response = objectMapper.readValue(
                    resultContent, ReadSentFriendRelationResponse.class);

            // then
            List<Long> actual = response.friendRequestResponses().stream()
                    .map(SentFriendRelationInfo::friendRequestId)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(actual).containsExactlyInAnyOrderElementsOf(idsOfFriendRelation);
            });
        }
    }
}
