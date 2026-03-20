package backend.mulkkam.common.infrastructure.fcm.dto;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;

public record SendTopicEvent(SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest) {
}
