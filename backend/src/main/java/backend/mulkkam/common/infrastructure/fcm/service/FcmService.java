package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmService {

    private static final String ACTION = "action";

    private final FirebaseMessaging firebaseMessaging;

    public void sendMessageByToken(SendMessageByFcmTokenRequest sendFcmTokenMessageRequest) throws FirebaseMessagingException {
        firebaseMessaging.send(Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(sendFcmTokenMessageRequest.title())
                        .setBody(sendFcmTokenMessageRequest.body())
                        .build())
                .setToken(sendFcmTokenMessageRequest.token())
                .putData(ACTION, sendFcmTokenMessageRequest.action().name())
                .build());
    }

    public void sendMessageByTopic(SendMessageByFcmTopicRequest sendFcmTopicMessageRequest) throws FirebaseMessagingException {
        firebaseMessaging.send(Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(sendFcmTopicMessageRequest.title())
                        .setBody(sendFcmTopicMessageRequest.body())
                        .build())
                .setTopic(sendFcmTopicMessageRequest.topic())
                .putData(ACTION, sendFcmTopicMessageRequest.action().name())
                .build());
    }
}
