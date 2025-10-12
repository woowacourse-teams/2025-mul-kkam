package backend.mulkkam.common.infrastructure.fcm.dto.request;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import java.util.List;

public record SendMessageByFcmTokensRequest(
        String title,
        String body,
        List<String> tokens,
        Action action
) {
}
