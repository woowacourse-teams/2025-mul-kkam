package backend.mulkkam.friend.service;

import static org.assertj.core.api.Assertions.assertThat;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.domain.Friend;
import backend.mulkkam.friend.repository.FriendRepository;
import backend.mulkkam.friend.repository.FriendRequestRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.service.ServiceIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FriendServiceIntegrationTest extends ServiceIntegrationTest {

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

    @DisplayName("친구를 삭제할 때")
    @Nested
    class Delete {

        @DisplayName("요청자가 삭제하는 경우 정상적으로 삭제된다.")
        @Test
        void success_deleteByRequester() {
            // given
            Friend friend = new Friend(requester.getId(), addressee.getId());
            friendRepository.save(friend);

            // when
            friendService.delete(addressee.getId(), new MemberDetails(requester.getId()));

            // then
            assertThat(friendRepository.findAll()).isEmpty();
        }

        @DisplayName("수락자가 삭제하는 경우 정상적으로 삭제된다.")
        @Test
        void success_deleteByAddressee() {
            // given
            Friend friend = new Friend(requester.getId(), addressee.getId());
            friendRepository.save(friend);

            // when
            friendService.delete(addressee.getId(), new MemberDetails(requester.getId()));

            // then
            assertThat(friendRepository.findAll()).isEmpty();
        }
    }
}
