package backend.mulkkam.friend.service;


import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.ALREADY_ACCEPTED;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND_RELATION;
import static backend.mulkkam.friend.dto.request.PatchFriendStatusRequest.FriendRequestStatus.ACCEPT;
import static backend.mulkkam.friend.dto.request.PatchFriendStatusRequest.FriendRequestStatus.REJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.auth.repository.OauthAccountRepository;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.friend.dto.response.FriendRelationResponse;
import backend.mulkkam.friend.dto.request.PatchFriendStatusRequest;
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

class FriendRelationServiceIntegrationTest extends ServiceIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OauthAccountRepository oauthAccountRepository;

    @Autowired
    private FriendCommandService friendCommandService;

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private FriendRelationRepository friendRelationRepository;

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
            friendCommandService.deleteFriend(savedFriendRelation.getId(), new MemberDetails(requester.getId()));

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
            friendCommandService.deleteFriend(requester.getId(), new MemberDetails(foundFriendRelation.getId()));

            // then
            assertThat(friendRelationRepository.findAll()).isEmpty();
        }
    }

    @DisplayName("친구 요청을 거절할 때")
    @Nested
    class RejectFriendRelationRequest {

        @DisplayName("존재하지 않는 요청에 대해 예외를 던진다")
        @Test
        void error_byNonExistingFriendRequest() {
            // when & then
            assertThatThrownBy(() -> friendRequestService.modifyFriendStatus(
                    1L,
                    new PatchFriendStatusRequest(REJECT),
                    new MemberDetails(requester.getId())))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_FRIEND_RELATION.name());
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

            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when & then
            assertThatThrownBy(() -> friendRequestService.modifyFriendStatus(
                    friendRelation.getId(),
                    new PatchFriendStatusRequest(REJECT),
                    new MemberDetails(invalidMember.getId())))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST.name());
        }

        @DisplayName("정상적으로 거절된다")
        @Test
        void success_rejected() {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when
            friendRequestService.modifyFriendStatus(
                    friendRelation.getId(),
                    new PatchFriendStatusRequest(REJECT),
                    new MemberDetails(addressee.getId())
            );

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(friendRelations).hasSize(0);
            });
        }

        @DisplayName("요청자가 본인의 요청을 거절하려 하면 예외를 던진다")
        @Test
        void error_requesterCannotProcessOwnRequest() {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when & then
            assertThatThrownBy(() -> friendRequestService.modifyFriendStatus(
                    friendRelation.getId(),
                    new PatchFriendStatusRequest(REJECT),
                    new MemberDetails(requester.getId())))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST.name());
        }

        @DisplayName("이미 처리된 요청을 다시 처리하려 하면 예외를 던진다")
        @Test
        void error_cannotProcessAlreadyProcessedRequest() {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.ACCEPTED);
            FriendRelation savedFriendRelation = friendRelationRepository.save(friendRelation);

            // when & then
            assertThatThrownBy(() -> friendRequestService.modifyFriendStatus(
                    savedFriendRelation.getId(),
                    new PatchFriendStatusRequest(REJECT),
                    new MemberDetails(addressee.getId())))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(ALREADY_ACCEPTED.name());
        }
    }

    @DisplayName("친구 요청을 수락할 때")
    @Nested
    class AcceptFriendRelationRequest {

        @DisplayName("존재하지 않는 요청에 대해 예외를 던진다")
        @Test
        void error_byNonExistingFriendRequest() {
            // when & then
            assertThatThrownBy(() -> friendRequestService.modifyFriendStatus(
                    1L,
                    new PatchFriendStatusRequest(ACCEPT),
                    new MemberDetails(requester.getId())))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_FRIEND_RELATION.name());
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

            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when & then
            assertThatThrownBy(() -> friendRequestService.modifyFriendStatus(
                    friendRelation.getId(),
                    new PatchFriendStatusRequest(ACCEPT),
                    new MemberDetails(invalidMember.getId())))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST.name());
        }

        @DisplayName("정상적으로 수락된다")
        @Test
        void success_accepted() {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when
            friendRequestService.modifyFriendStatus(
                    friendRelation.getId(),
                    new PatchFriendStatusRequest(ACCEPT),
                    new MemberDetails(addressee.getId())
            );

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

        @DisplayName("요청자가 본인의 요청을 처리하려 하면 예외를 던진다")
        @Test
        void error_requesterCannotProcessOwnRequest() {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when & then
            assertThatThrownBy(() -> friendRequestService.modifyFriendStatus(
                    friendRelation.getId(),
                    new PatchFriendStatusRequest(ACCEPT),
                    new MemberDetails(requester.getId())))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST.name());
        }

        @DisplayName("이미 처리된 요청을 다시 처리하려 하면 예외를 던진다")
        @Test
        void error_cannotProcessAlreadyProcessedRequest() {
            // given
            FriendRelation friendRelation = new FriendRelation(requester.getId(), addressee.getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);
            friendRequestService.modifyFriendStatus(
                    friendRelation.getId(),
                    new PatchFriendStatusRequest(REJECT),
                    new MemberDetails(addressee.getId()));

            // when & then
            assertThatThrownBy(() -> friendRequestService.modifyFriendStatus(
                    friendRelation.getId(),
                    new PatchFriendStatusRequest(ACCEPT),
                    new MemberDetails(addressee.getId())))
                    .isInstanceOf(CommonException.class)
                    .hasMessage(NOT_FOUND_FRIEND_RELATION.name());
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

            // wheㄱn
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
