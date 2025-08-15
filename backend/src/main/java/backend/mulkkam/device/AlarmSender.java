package backend.mulkkam.device;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;

public interface AlarmSender {

    void notifyTopic(SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest);

    void notifyToken(SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest);
}
