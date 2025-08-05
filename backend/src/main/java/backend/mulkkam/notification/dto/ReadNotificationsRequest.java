package backend.mulkkam.notification.dto;

import java.time.LocalDateTime;

// TODO 2025. 8. 5. 16:37: size default 값 처리 하는 방법 찾기
public record ReadNotificationsRequest(Long lastId,
                                       LocalDateTime clientTime,
                                       int size
) {
}
