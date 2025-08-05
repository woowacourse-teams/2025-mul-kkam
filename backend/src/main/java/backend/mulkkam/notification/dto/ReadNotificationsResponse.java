package backend.mulkkam.notification.dto;

import java.util.List;

// TODO 2025. 8. 5. 16:35: 줄바꿈 확인할 것
public record ReadNotificationsResponse(
        List<ReadNotificationResponse> readNotificationResponses,
        Long nextCursor
) {
}
