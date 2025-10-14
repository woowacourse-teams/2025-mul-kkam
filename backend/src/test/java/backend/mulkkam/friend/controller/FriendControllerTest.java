package backend.mulkkam.friend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.infrastructure.OauthJwtTokenHandler;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.friend.domain.Friend;
import backend.mulkkam.friend.domain.FriendRequest;
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

            // then
            List<Friend> friends = friendRepository.findAll();
        }

        @DisplayName("친구 요청을 거절할 때")
        @Nested
        class RejectFriendRequest {

            @DisplayName("정상적으로 거절된다")
            @Test
            void success_rejected() throws Exception {
                // given
                FriendRequest friendRequest = new FriendRequest(requester.getId(), addressee.getId());
                friendRequestRepository.save(friendRequest);

                // when
                mockMvc.perform(post("/friends/request/" + friendRequest.getId() + "/reject")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenOfAddressee))
                        .andDo(print()).andExpect(status().isOk());

                // then
                List<FriendRequest> friendRequests = friendRequestRepository.findAll();
                List<Friend> friends = friendRepository.findAll();
                assertSoftly(softly -> {
                    softly.assertThat(friendRequests).hasSize(0);
                    softly.assertThat(friends).hasSize(0);
                });
            }
        }

        @DisplayName("친구 요청을 수락할 때")
        @Nested
        class AcceptFriendRequest {

            @DisplayName("정상적으로 수락된다")
            @Test
            void success_accepted() throws Exception {
                // given
                FriendRequest friendRequest = new FriendRequest(requester.getId(), addressee.getId());
                friendRequestRepository.save(friendRequest);

                // when
                mockMvc.perform(post("/friends/request/" + friendRequest.getId() + "/accept")
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + tokenOfAddressee))
                        .andDo(print()).andExpect(status().isOk());

                // then
                List<FriendRequest> friendRequests = friendRequestRepository.findAll();
                List<Friend> friends = friendRepository.findAll();
                assertSoftly(softly -> {
                    softly.assertThat(friends).hasSize(1);
                    softly.assertThat(friendRequests).hasSize(0);
                    softly.assertThat(friends.getFirst().getRequesterId()).isEqualTo(requester.getId());
                    softly.assertThat(friends.getFirst().getAddresseeId()).isEqualTo(addressee.getId());
                });
            }
        }
    }
}