package backend.mulkkam.notification.dto;

import java.util.List;

public record ReadNotificationsResponse(
        List<ReadNotificationResponse> readNotificationResponses,
        Long nextCursor
) {
}
