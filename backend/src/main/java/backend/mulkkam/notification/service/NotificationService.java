package backend.mulkkam.notification.service;

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

    public ReadNotificationsResponse getNotificationsAfter(ReadNotificationsRequest readNotificationsRequest) {
        Long lastId = readNotificationsRequest.lastId();
        int size = readNotificationsRequest.size();
        LocalDateTime clientTime = readNotificationsRequest.clientTime();

        LocalDateTime limitStartDateTime = clientTime.minusDays(DAY_LIMIT);
        Pageable pageable = Pageable.ofSize(size + 1);
        List<Notification> notifications = notificationRepository.findByCursor(lastId, limitStartDateTime, pageable);

        boolean hasNext = notifications.size() > size;

        Long nextCursor = getNextCursor(hasNext, notifications);
        List<ReadNotificationResponse> readNotificationResponses = toReadNotificationResponses(hasNext, notifications);

        return new ReadNotificationsResponse(readNotificationResponses, nextCursor);
    }

    private static Long getNextCursor(boolean hasNext, List<Notification> notifications) {
        if (hasNext) {
            return notifications.getLast().getId();
        }
        return null;
    }

    private List<ReadNotificationResponse> toReadNotificationResponses(boolean hasNext, List<Notification> notifications) {
        if (hasNext) {
            notifications.removeLast();
        }

        return notifications.stream()
                .map(ReadNotificationResponse::new)
                .collect(Collectors.toList());
    }
}
