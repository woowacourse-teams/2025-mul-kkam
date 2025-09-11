package backend.mulkkam.common.infrastructure.fcm.dto;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;

public record SendTokenEvent (SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest) {
}
