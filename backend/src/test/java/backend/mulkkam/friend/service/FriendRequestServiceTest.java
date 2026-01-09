package backend.mulkkam.friend.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.ALREADY_ACCEPTED;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_FRIEND_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.friend.domain.FriendRelation;
import backend.mulkkam.friend.domain.FriendRelationStatus;
import backend.mulkkam.friend.dto.request.PatchFriendStatusRequest;
import backend.mulkkam.friend.dto.response.ReadSentFriendRelationResponse;
import backend.mulkkam.friend.repository.FriendRelationRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.domain.vo.MemberRole;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import backend.mulkkam.support.service.ServiceTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FriendRequestServiceTest extends ServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRelationRepository friendRelationRepository;

    @Autowired
    private FriendRequestService friendRequestService;

    record FriendRequestPair(Member requester, Member addressee) {
    }

    private Member createAndSaveMember(String nickname) {
        return memberRepository.save(
                MemberFixtureBuilder.builder()
                        .memberNickname(new MemberNickname(nickname))
                        .build()
        );
    }

    private FriendRequestPair createFriendRequestPair() {
        Member requester = createAndSaveMember("요청자");
        Member addressee = createAndSaveMember("수신자");
        return new FriendRequestPair(requester, addressee);
    }

    @DisplayName("친구 요청을 거절할 때")
    @Nested
    class RejectFriendRelationRequest {

        @DisplayName("존재하지 않는 요청에 대해 예외를 던진다")
        @Test
        void fail_cannot_reject_non_existing_request() {
            // given
            Member requester = createAndSaveMember("요청자");

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> friendRequestService.modifyFriendStatus(
                            new PatchFriendStatusRequest(requester.getId(), PatchFriendStatusRequest.Status.REJECTED),
                            new MemberDetails(requester.getId(), MemberRole.MEMBER)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_FRIEND_REQUEST);
        }

        @DisplayName("요청을 받은 사용자가 아닌 경우 예외를 던진다")
        @Test
        void fail_cannot_reject_without_permission() {
            // given
            FriendRequestPair pair = createFriendRequestPair();
            Member invalidMember = createAndSaveMember("무관한사람");

            FriendRelation friendRelation = new FriendRelation(pair.requester().getId(), pair.addressee().getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> friendRequestService.modifyFriendStatus(
                            new PatchFriendStatusRequest(pair.requester().getId(),
                                    PatchFriendStatusRequest.Status.REJECTED),
                            new MemberDetails(invalidMember.getId(), MemberRole.MEMBER)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_FRIEND_REQUEST);
        }

        @DisplayName("정상적으로 거절된다")
        @Test
        void success_friend_request_is_rejected() {
            // given
            FriendRequestPair pair = createFriendRequestPair();
            FriendRelation friendRelation = new FriendRelation(pair.requester().getId(), pair.addressee().getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when
            friendRequestService.modifyFriendStatus(
                    new PatchFriendStatusRequest(pair.requester().getId(), PatchFriendStatusRequest.Status.REJECTED),
                    new MemberDetails(pair.addressee().getId(), MemberRole.MEMBER)
            );

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(friendRelations).hasSize(0);
            });
        }

        @DisplayName("요청자가 본인의 요청을 거절하려 하면 예외를 던진다")
        @Test
        void fail_requester_cannot_reject_own_request() {
            // given
            FriendRequestPair pair = createFriendRequestPair();
            FriendRelation friendRelation = new FriendRelation(pair.requester().getId(), pair.addressee().getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> friendRequestService.modifyFriendStatus(
                            new PatchFriendStatusRequest(pair.requester().getId(),
                                    PatchFriendStatusRequest.Status.REJECTED),
                            new MemberDetails(pair.requester().getId(), MemberRole.MEMBER)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_FRIEND_REQUEST);
        }

        @DisplayName("이미 처리된 요청을 다시 처리하려 하면 예외를 던진다")
        @Test
        void fail_cannot_reject_already_processed_request() {
            // given
            FriendRequestPair pair = createFriendRequestPair();
            FriendRelation friendRelation = new FriendRelation(pair.requester().getId(), pair.addressee().getId(),
                    FriendRelationStatus.ACCEPTED);
            friendRelationRepository.save(friendRelation);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> friendRequestService.modifyFriendStatus(
                            new PatchFriendStatusRequest(pair.requester().getId(),
                                    PatchFriendStatusRequest.Status.REJECTED),
                            new MemberDetails(pair.addressee().getId(), MemberRole.MEMBER)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(ALREADY_ACCEPTED);
        }
    }

    @DisplayName("친구 요청을 수락할 때")
    @Nested
    class AcceptFriendRelationRequest {

        @DisplayName("존재하지 않는 요청에 대해 예외를 던진다")
        @Test
        void fail_cannot_accept_non_existing_request() {
            // given
            Member requester = createAndSaveMember("요청자");

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> friendRequestService.modifyFriendStatus(
                            new PatchFriendStatusRequest(requester.getId(), PatchFriendStatusRequest.Status.ACCEPTED),
                            new MemberDetails(requester.getId(), MemberRole.MEMBER)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_FRIEND_REQUEST);
        }

        @DisplayName("요청을 받은 사용자가 아닌 경우 예외를 던진다")
        @Test
        void fail_cannot_accept_without_permission() {
            // given
            FriendRequestPair pair = createFriendRequestPair();
            Member invalidMember = createAndSaveMember("무관한사람");

            FriendRelation friendRelation = new FriendRelation(pair.requester().getId(), pair.addressee().getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> friendRequestService.modifyFriendStatus(
                            new PatchFriendStatusRequest(pair.requester().getId(),
                                    PatchFriendStatusRequest.Status.ACCEPTED),
                            new MemberDetails(invalidMember.getId(), MemberRole.MEMBER)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_FRIEND_REQUEST);
        }

        @DisplayName("정상적으로 수락된다")
        @Test
        void success_friend_request_is_accepted() {
            // given
            FriendRequestPair pair = createFriendRequestPair();
            FriendRelation friendRelation = new FriendRelation(pair.requester().getId(), pair.addressee().getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when
            friendRequestService.modifyFriendStatus(
                    new PatchFriendStatusRequest(pair.requester().getId(), PatchFriendStatusRequest.Status.ACCEPTED),
                    new MemberDetails(pair.addressee().getId(), MemberRole.MEMBER)
            );

            // then
            List<FriendRelation> friendRelations = friendRelationRepository.findAll();
            assertSoftly(softly -> {
                softly.assertThat(friendRelations).hasSize(1);
                softly.assertThat(friendRelations.getFirst().getRequesterId()).isEqualTo(pair.requester().getId());
                softly.assertThat(friendRelations.getFirst().getAddresseeId()).isEqualTo(pair.addressee().getId());
                softly.assertThat(friendRelations.getFirst().getFriendRelationStatus())
                        .isEqualTo(FriendRelationStatus.ACCEPTED);
            });
        }

        @DisplayName("요청자가 본인의 요청을 처리하려 하면 예외를 던진다")
        @Test
        void fail_requester_cannot_accept_own_request() {
            // given
            FriendRequestPair pair = createFriendRequestPair();
            FriendRelation friendRelation = new FriendRelation(pair.requester().getId(), pair.addressee().getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> friendRequestService.modifyFriendStatus(
                            new PatchFriendStatusRequest(pair.requester().getId(),
                                    PatchFriendStatusRequest.Status.ACCEPTED),
                            new MemberDetails(pair.requester().getId(), MemberRole.MEMBER)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_FRIEND_REQUEST);
        }

        @DisplayName("이미 처리된 요청을 다시 처리하려 하면 예외를 던진다")
        @Test
        void error_cannotProcessAlreadyProcessedRequest() {
            // given
            FriendRequestPair pair = createFriendRequestPair();
            FriendRelation friendRelation = new FriendRelation(pair.requester().getId(), pair.addressee().getId(),
                    FriendRelationStatus.REQUESTED);
            friendRelationRepository.save(friendRelation);
            friendRequestService.modifyFriendStatus(
                    new PatchFriendStatusRequest(pair.requester().getId(), PatchFriendStatusRequest.Status.REJECTED),
                    new MemberDetails(pair.addressee().getId(), MemberRole.MEMBER));

            // when
            CommonException ex = assertThrows(CommonException.class,
                    () -> friendRequestService.modifyFriendStatus(
                            new PatchFriendStatusRequest(pair.requester().getId(),
                                    PatchFriendStatusRequest.Status.ACCEPTED),
                            new MemberDetails(pair.addressee().getId(), MemberRole.MEMBER)));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(NOT_FOUND_FRIEND_REQUEST);
        }
    }

    @DisplayName("내가 보낸 친구 신청 목록을 조회할 때")
    @Nested
    class GetSentFriendRequest {

        @DisplayName("내가 요청자인 경우만 반환한다.")
        @Test
        void success_returns_only_sent_by_me() {
            // given
            Member requester = createAndSaveMember("요청자");
            List<Long> idsOfFriendRelation = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Member member = createAndSaveMember("히로" + i);

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
            ReadSentFriendRelationResponse result = friendRequestService.readSent(
                    new MemberDetails(requester.getId(), MemberRole.MEMBER),
                    null,
                    10
            );

            // then
            List<Long> actual = result.results().stream()
                    .map(ReadSentFriendRelationResponse.SentFriendRelationInfo::friendRequestId)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(actual).containsExactlyInAnyOrderElementsOf(idsOfFriendRelation);
            });
        }

        @DisplayName("상태가 REQUESTED 인 경우만 반환한다")
        @Test
        void success_returns_only_requested_status() {
            // given
            Member requester = createAndSaveMember("요청자");
            List<Long> expected = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Member member = createAndSaveMember("히로" + i);

                if (i % 3 == 0) {
                    FriendRelation friendRelation = new FriendRelation(requester.getId(), member.getId(),
                            FriendRelationStatus.ACCEPTED);
                    friendRelationRepository.save(friendRelation);
                    continue;
                }

                FriendRelation friendRelation = new FriendRelation(requester.getId(), member.getId(),
                        FriendRelationStatus.REQUESTED);
                friendRelationRepository.save(friendRelation);
                expected.add(friendRelation.getId());
            }

            // when
            ReadSentFriendRelationResponse result = friendRequestService.readSent(
                    new MemberDetails(requester.getId(), MemberRole.MEMBER),
                    null,
                    10
            );

            // then
            List<Long> actual = result.results().stream()
                    .map(ReadSentFriendRelationResponse.SentFriendRelationInfo::friendRequestId)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
            });
        }
    }
}
