package backend.mulkkam.friend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.friend.dto.response.FriendRelationResponse;
import backend.mulkkam.friend.repository.FriendRelationRepository;
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

import java.util.ArrayList;
import java.util.List;

class FriendServiceTest extends ServiceIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private FriendRelationRepository friendRelationRepository;

    @Autowired
    private FriendService friendService;

    private Member requester;
    private Member addressee;

    @BeforeEach
    void setUp() {
        requester = MemberFixtureBuilder.builder().build();
        memberRepository.save(requester);

        addressee = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("칼리2")).build();
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
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.ACCEPTED);
            FriendRelation savedFriendRelation = friendRelationRepository.save(friendRelation);

            // when
            friendService.deleteFriend(savedFriendRelation.getId(), new MemberDetails(requester.getId()));

            // then
            assertThat(friendRelationRepository.findAll()).isEmpty();
        }

        @DisplayName("수락자가 삭제하는 경우 정상적으로 삭제된다.")
        @Test
        void success_deleteByAddressee() {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.ACCEPTED);
            FriendRelation foundFriendRelation = friendRelationRepository.save(friendRelation);

            // when
            friendService.deleteFriend(foundFriendRelation.getId(), new MemberDetails(addressee.getId()));

            // then
            assertThat(friendRelationRepository.findAll()).isEmpty();
        }
    }

    @DisplayName("친구 목록을 조회할 때")
    @Nested
    class Read {

        @DisplayName("친구 관계의 수락자나 요청자가 조회 시도자인 경우만 반환한다")
        @Test
        void success_onlyRequesterOrAddressee() {
            // given
            List<Long> idOfMemberInRelation = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Member member = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("히로" + i)).build();
                memberRepository.save(member);

                // 3의 배수인 경우에는 member 를 요청자로 저장
                if (i % 3 == 0) {
                    FriendRelation friendRelation = new FriendRelation(member.getId(), requester.getId(),
                            FriendRelationStatus.ACCEPTED);
                    friendRelationRepository.save(friendRelation);
                    idOfMemberInRelation.add(member.getId());
                    continue;
                }

                // 짝수인 경우에는 member 를 수락자로 저장
                if (i % 2 == 0) {
                    FriendRelation friendRelation = new FriendRelation(requester.getId(), member.getId(),
                            FriendRelationStatus.ACCEPTED);
                    friendRelationRepository.save(friendRelation);
                    idOfMemberInRelation.add(member.getId());
                }
            }

            // when
            FriendRelationResponse friendRelationResponse = friendService.readFriendRelationsInStatusAccepted(null, 10,
                    new MemberDetails(requester.getId()));

            // then
            List<Long> memberIdsOfResult = friendRelationResponse.informationOfMembers().stream()
                    .map(FriendRelationResponse.MemberInfo::memberId)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(memberIdsOfResult).containsExactlyInAnyOrderElementsOf(idOfMemberInRelation);
            });
        }

        @DisplayName("상태가 ACCEPTED 인 경우에만 반환한다")
        @Test
        void success_onlyStatusISAccepted() {
            // given
            List<Long> memberIdsOfAcceptedRelations = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Member member = MemberFixtureBuilder.builder().memberNickname(new MemberNickname("히로" + i)).build();
                memberRepository.save(member);

                // 3의 배수인 경우에는 신청된 관계로 저장
                if (i % 3 == 0) {
                    FriendRelation friendRelation = new FriendRelation(member.getId(), requester.getId(),
                            FriendRelationStatus.REQUESTED);
                    friendRelationRepository.save(friendRelation);
                    continue;
                }

                // 2의 배수인 경우에는 수락된 관계로 저장
                if (i % 2 == 0) {
                    FriendRelation friendRelation = new FriendRelation(requester.getId(), member.getId(),
                            FriendRelationStatus.ACCEPTED);
                    friendRelationRepository.save(friendRelation);
                    memberIdsOfAcceptedRelations.add(member.getId());
                }
            }

            // when
            FriendRelationResponse friendRelationResponse = friendService.readFriendRelationsInStatusAccepted(null, 10,
                    new MemberDetails(requester.getId()));

            // then
            List<Long> memberIdsOfResult = friendRelationResponse.informationOfMembers().stream()
                    .map(FriendRelationResponse.MemberInfo::memberId)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(memberIdsOfResult).containsExactlyInAnyOrderElementsOf(memberIdsOfAcceptedRelations);
            });
        }
    }
}
