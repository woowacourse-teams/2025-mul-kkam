package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND_REQUEST;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.domain.Friend;
import backend.mulkkam.friend.domain.FriendRequest;
import backend.mulkkam.friend.repository.FriendRepository;
import backend.mulkkam.friend.repository.FriendRequestRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FriendServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private FriendService friendService;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    private Member requester;
    private Member addressee;

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
    }

    @DisplayName("친구 요청을 수락 및 거절할 때")
    @Nested
    class ProcessFriendRequest {

        @DisplayName("존재하지 않는 요청에 대해 예외를 던진다")
        @Test
        void error_byNonExistingFriendRequest() {
            // when & then
            assertThatThrownBy(() -> friendService.processFriendRequest(1L, true, new MemberDetails(requester.getId())))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_FRIEND_REQUEST.name());
        }

        @DisplayName("요청을 받은 사용자가 아닌 경우 예외를 던진다")
        @Test
        void error_hasNoPermissionForAcceptingRequest() {
            // given
            Member invalidMember = MemberFixtureBuilder
                    .builder()
                    .memberNickname(new MemberNickname("체체"))
                    .build();
            memberRepository.save(invalidMember);

            FriendRequest friendRequest = new FriendRequest(requester.getId(), addressee.getId());
            friendRequestRepository.save(friendRequest);

            // when & then
            assertThatThrownBy(() -> friendService.processFriendRequest(
                    friendRequest.getId(),
                    true,
                    new MemberDetails(invalidMember.getId())))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST.name());
        }

        @DisplayName("정상적으로 수락된다")
        @Test
        void success_accepted() {
            // given
            FriendRequest friendRequest = new FriendRequest(requester.getId(), addressee.getId());
            friendRequestRepository.save(friendRequest);

            // when
            friendService.processFriendRequest(
                    friendRequest.getId(),
                    true,
                    new MemberDetails(addressee.getId())
            );

            // then
            List<Friend> friends = friendRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(friends).hasSize(1);
                softly.assertThat(friends.getFirst().getRequesterId()).isEqualTo(requester.getId());
                softly.assertThat(friends.getFirst().getAddresseeId()).isEqualTo(addressee.getId());
            });
        }

        @DisplayName("정상적으로 거절된다")
        @Test
        void success_rejected() {
            // given
            FriendRequest friendRequest = new FriendRequest(requester.getId(), addressee.getId());
            friendRequestRepository.save(friendRequest);

            // when
            friendService.processFriendRequest(
                    friendRequest.getId(),
                    false,
                    new MemberDetails(addressee.getId())
            );

            // then
            List<FriendRequest> friendRequests = friendRequestRepository.findAll();
            List<Friend> friends = friendRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(friends).hasSize(0);
                softly.assertThat(friendRequests).hasSize(0);
            });
        }
    }
}
