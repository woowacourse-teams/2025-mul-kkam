package backend.mulkkam.common.infrastructure.fcm.dto.request;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;

public record SendMessageByFcmTokenRequest(
        String title,
        String body,
        String token,
        Action action
) {
}
