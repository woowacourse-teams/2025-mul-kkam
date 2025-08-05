package backend.mulkkam.notification.service;

import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.dto.ReadNotificationResponse;
import backend.mulkkam.notification.dto.ReadNotificationsRequest;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        List<Notification> notifications = notificationRepository.findByCursor(lastId, limitStartDateTime, pageable); // TODO 2025. 8. 5. 16:50: limitDateTime 인 거를 sql에서 할 지 비지니스에서 할 지

        boolean hasNext = notifications.size() > size;

        if (hasNext) {
            notifications.removeLast();
        }

        Long nextCursor = hasNext ? notifications.getLast().getId() : null;

        List<ReadNotificationResponse> readNotificationResponses = new ArrayList<>();
        for (Notification notification : notifications) {
            readNotificationResponses.add(new ReadNotificationResponse(notification));
        }

        return new ReadNotificationsResponse(readNotificationResponses, nextCursor);
    }
}
