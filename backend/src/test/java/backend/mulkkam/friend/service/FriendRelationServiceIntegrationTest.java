package backend.mulkkam.friend.service;


import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_FRIEND_RELATION;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_PROCESS_FRIEND_REQUEST;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND_RELATION;
import static backend.mulkkam.friend.dto.PatchFriendStatusRequest.FriendRequestStatus.ACCEPT;
import static backend.mulkkam.friend.dto.PatchFriendStatusRequest.FriendRequestStatus.REJECT;
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
import backend.mulkkam.friend.dto.PatchFriendStatusRequest;
import backend.mulkkam.friend.repository.FriendRelationRepository;
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
                    .hasMessage(INVALID_FRIEND_RELATION.name());
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
}
