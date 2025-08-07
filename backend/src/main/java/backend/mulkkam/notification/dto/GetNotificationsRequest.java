package backend.mulkkam.notification.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record GetNotificationsRequest(
        Long lastId,
        @NotNull LocalDateTime clientTime,
        int size
) {
}
