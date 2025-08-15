package backend.mulkkam.device;

import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FirebaseAlarmSender implements AlarmSender {

    private final FcmService fcmService;

    @Override
    public void notifyTopic(SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest) {
        try {
            fcmService.sendMessageByTopic(sendMessageByFcmTopicRequest);
        } catch (FirebaseMessagingException firebaseMessagingException) {
            throw new AlarmException(firebaseMessagingException);
        }
    }

    @Override
    public void notifyToken(SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest) {
        try {
            fcmService.sendMessageByToken(sendMessageByFcmTokenRequest);
        } catch (FirebaseMessagingException e) {
            throw new AlarmException(e);
        }
    }
}
