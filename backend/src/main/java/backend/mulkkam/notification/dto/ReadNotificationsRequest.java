package backend.mulkkam.notification.dto;

import java.time.LocalDateTime;

public record ReadNotificationsRequest(
        Long lastId,
        LocalDateTime clientTime,
        int size
) {
}
