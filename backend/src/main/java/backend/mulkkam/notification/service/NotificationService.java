package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.dto.ReadNotificationResponse;
import backend.mulkkam.notification.dto.ReadNotificationsRequest;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final int DAY_LIMIT = 7;

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    public ReadNotificationsResponse getNotificationsAfter(ReadNotificationsRequest readNotificationsRequest, Long memberId) {
        Member member = getMemberById(memberId);

        validateSizeRange(readNotificationsRequest);
        int size = readNotificationsRequest.size();

        LocalDateTime clientTime = readNotificationsRequest.clientTime();

        LocalDateTime limitStartDateTime = clientTime.minusDays(DAY_LIMIT);
        Pageable pageable = Pageable.ofSize(size + 1);

        Long lastId = readNotificationsRequest.lastId();
        List<Notification> notifications = getNotificationsByLastIdAndMember(member, lastId, limitStartDateTime, pageable);

        boolean hasNext = notifications.size() > size;

        Long nextCursor = getNextCursor(hasNext, notifications);
        List<ReadNotificationResponse> readNotificationResponses = toReadNotificationResponses(hasNext, notifications);

        return new ReadNotificationsResponse(readNotificationResponses, nextCursor);
    }

    private void validateSizeRange(ReadNotificationsRequest readNotificationsRequest) {
        if (readNotificationsRequest.size() < 1) {
            throw new CommonException(INVALID_PAGE_SIZE_RANGE);
        }
    }

    private List<Notification> getNotificationsByLastIdAndMember(
            Member member,
            Long lastId,
            LocalDateTime limitStartDateTime,
            Pageable pageable) {
        if (lastId == null) {
            return notificationRepository.findLatest(member, limitStartDateTime, pageable);
        }
        return notificationRepository.findByCursor(member, lastId, limitStartDateTime, pageable);
    }

    private Long getNextCursor(
            boolean hasNext,
            List<Notification> notifications
    ) {
        if (hasNext) {
            return notifications.getLast().getId();
        }
        return null;
    }

    private List<ReadNotificationResponse> toReadNotificationResponses(
            boolean hasNext,
            List<Notification> notifications
    ) {
        if (hasNext) {
            notifications.removeLast();
        }

        return notifications.stream()
                .map(ReadNotificationResponse::new)
                .collect(Collectors.toList());
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
