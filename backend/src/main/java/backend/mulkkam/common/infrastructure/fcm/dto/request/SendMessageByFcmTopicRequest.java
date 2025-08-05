package backend.mulkkam.common.infrastructure.fcm.dto.request;

import backend.mulkkam.common.infrastructure.fcm.domain.Action;

public record SendMessageByFcmTopicRequest(
        String title,
        String body,
        String topic,
        Action action
) {
}
