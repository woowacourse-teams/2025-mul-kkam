package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.device.AlarmService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmService implements AlarmService {

    private static final String ACTION = "action";

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void sendMessageByToken(SendMessageByFcmTokenRequest sendFcmTokenMessageRequest) {
        try {
            firebaseMessaging.send(Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(sendFcmTokenMessageRequest.title())
                            .setBody(sendFcmTokenMessageRequest.body())
                            .build())
                    .setToken(sendFcmTokenMessageRequest.token())
                    .putData(ACTION, sendFcmTokenMessageRequest.action().name())
                    .build());
        } catch (FirebaseMessagingException e) {
            throw new AlarmException(e);
        }
    }

    @Override
    public void sendMessageByTopic(SendMessageByFcmTopicRequest sendFcmTopicMessageRequest) {
        try {
            firebaseMessaging.send(Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(sendFcmTopicMessageRequest.title())
                            .setBody(sendFcmTopicMessageRequest.body())
                            .build())
                    .setTopic(sendFcmTopicMessageRequest.topic())
                    .putData(ACTION, sendFcmTopicMessageRequest.action().name())
                    .build());
        } catch (FirebaseMessagingException e) {
            throw new AlarmException(e);
        }
    }
}
