package backend.mulkkam.device;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;

public interface AlarmService {

    void sendMessageByToken(SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest);

    void sendMessageByTopic(SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest);
}
