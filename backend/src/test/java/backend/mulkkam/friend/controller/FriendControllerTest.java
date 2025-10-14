package backend.mulkkam.friend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.friend.domain.Friend;
import backend.mulkkam.friend.domain.FriendRequest;
import backend.mulkkam.friend.dto.response.GetReceivedFriendRequestCountResponse;
import backend.mulkkam.friend.dto.response.ReadReceivedFriendRequestsResponse;
import backend.mulkkam.friend.repository.FriendRepository;
import backend.mulkkam.friend.repository.FriendRequestRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.controller.ControllerTest;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class FriendControllerTest extends ControllerTest {

    @Autowired
    private OauthJwtTokenHandler oauthJwtTokenHandler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    private Member requester;
    private Member addressee;
    private String tokenOfRequester;
    private String tokenOfAddressee;

    @BeforeEach
    void setUp() {
        requester = MemberFixtureBuilder.builder().build();
        memberRepository.save(requester);

        addressee = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("칼리")).build();
        memberRepository.save(addressee);

        OauthAccount oauthAccountOfRequester = new OauthAccount(requester, "testIdOfRequester", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccountOfRequester);

        OauthAccount oauthAccountOfAddressee = new OauthAccount(addressee, "testIdOfAddressee", OauthProvider.KAKAO);
        oauthAccountRepository.save(oauthAccountOfAddressee);

        tokenOfRequester = oauthJwtTokenHandler.createAccessToken(oauthAccountOfRequester, "temp");
        tokenOfAddressee = oauthJwtTokenHandler.createAccessToken(oauthAccountOfAddressee, "temp");
    }

    @DisplayName("친구 목록에서 친구를 삭제할 때")
    @Nested
    class DeleteFriend {

        @DisplayName("요청자가 삭제하는 경우 정상적으로 삭제된다.")
        @Test
        void success_deleteByRequester() throws Exception {
            // given
            Friend friend = new Friend(requester.getId(), addressee.getId());
            friendRepository.save(friend);

            // when
            mockMvc.perform(delete("/friends/" + addressee.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfRequester))
                    .andExpect(status().isOk());

            // then
            List<Friend> friends = friendRepository.findAll();

            assertThat(friends).isEmpty();
        }

        @DisplayName("수락자가 삭제하는 경우 정상적으로 삭제된다.")
        @Test
        void success_deleteByAddressee() throws Exception {
            // given
            Friend friend = new Friend(requester.getId(), addressee.getId());
            friendRepository.save(friend);

            // when
            mockMvc.perform(delete("/friends/" + requester.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk());

            // then
            List<Friend> friends = friendRepository.findAll();

            assertThat(friends).isEmpty();
        }
    }

    @DisplayName("받은 친구 신청 목록 조회 시")
    @Nested
    class GetReceivedFriendRequests {

        @DisplayName("첫 요청 시 (lastId가 null) 정상적으로 조회된다.")
        @Test
        void success_firstRequest() throws Exception {
            // given
            FriendRequest friendRequest1 = new FriendRequest(requester.getId(), addressee.getId());
            Member member = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("테스터"))
                    .build();

            Member foundMember = memberRepository.save(member);
            FriendRequest friendRequest2 = new FriendRequest(
                    foundMember.getId(),
                    addressee.getId()
            );
            friendRequestRepository.saveAll(List.of(friendRequest1, friendRequest2));

            // when
            String resultContent = mockMvc.perform(get("/friends/requests/received")
                            .param("size", "10")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadReceivedFriendRequestsResponse response = objectMapper.readValue(resultContent,
                    ReadReceivedFriendRequestsResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.friendRequestResponses()).hasSize(2);
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

            FriendRequest request1 = new FriendRequest(member1.getId(), addressee.getId());
            FriendRequest request2 = new FriendRequest(member2.getId(), addressee.getId());
            FriendRequest request3 = new FriendRequest(member3.getId(), addressee.getId());
            friendRequestRepository.saveAll(List.of(request1, request2, request3));

            // when - 첫 번째 페이지 조회
            String firstPageContent = mockMvc.perform(get("/friends/requests/received")
                            .param("size", "2")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadReceivedFriendRequestsResponse firstPageResponse = objectMapper.readValue(
                    firstPageContent, ReadReceivedFriendRequestsResponse.class);

            // then - 첫 번째 페이지 검증
            assertSoftly(softly -> {
                softly.assertThat(firstPageResponse.friendRequestResponses()).hasSize(2);
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

            ReadReceivedFriendRequestsResponse secondPageResponse = objectMapper.readValue(
                    secondPageContent, ReadReceivedFriendRequestsResponse.class);

            // then - 두 번째 페이지 검증
            assertSoftly(softly -> {
                softly.assertThat(secondPageResponse.friendRequestResponses()).hasSize(1);
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

            ReadReceivedFriendRequestsResponse response = objectMapper.readValue(
                    resultContent, ReadReceivedFriendRequestsResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.friendRequestResponses()).isEmpty();
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

            FriendRequest request1 = new FriendRequest(member1.getId(), addressee.getId());
            FriendRequest request2 = new FriendRequest(member2.getId(), addressee.getId());
            friendRequestRepository.saveAll(List.of(request1, request2));

            // when
            String resultContent = mockMvc.perform(get("/friends/requests/received")
                            .param("size", "1")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ReadReceivedFriendRequestsResponse response = objectMapper.readValue(
                    resultContent, ReadReceivedFriendRequestsResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.friendRequestResponses()).hasSize(1);
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

            FriendRequest request1 = new FriendRequest(member1.getId(), addressee.getId());
            FriendRequest request2 = new FriendRequest(member2.getId(), addressee.getId());
            friendRequestRepository.saveAll(List.of(request1, request2));

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
}
