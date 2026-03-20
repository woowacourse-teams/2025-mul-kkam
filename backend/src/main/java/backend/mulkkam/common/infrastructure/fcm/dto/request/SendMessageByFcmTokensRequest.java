package backend.mulkkam.common.infrastructure.fcm.dto.request;

import backend.mulkkam.common.domain.DevicePlatform;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import java.util.List;

public record SendMessageByFcmTokensRequest(
        String title,
        String body,
        List<String> allTokens,
        DevicePlatform platform,
        Action action
) {

    public SendMessageByFcmTokensRequest(
            NotificationMessageTemplate template,
            List<String> tokens,
            DevicePlatform platform
    ) {
        this(template.title(), template.body(), tokens, platform, template.action());
    }

    public SendMessageByFcmTokensRequest withTokens(List<String> tokens) {
        return new SendMessageByFcmTokensRequest(
                title,
                body,
                tokens,
                platform,
                action
        );
    }

}
