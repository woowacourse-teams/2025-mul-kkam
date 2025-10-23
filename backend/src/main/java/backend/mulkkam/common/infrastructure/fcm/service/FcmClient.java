package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
// TODO: 구현 모두 끝내면 @Profile("worker") 추가하기
public class FcmClient {

    private static final String ACTION = "action";

    private final FirebaseMessaging firebaseMessaging;

    public void sendMessageByToken(SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest) {
        try {
            firebaseMessaging.send(Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(sendMessageByFcmTokenRequest.title())
                            .setBody(sendMessageByFcmTokenRequest.body())
                            .build())
                    .setToken(sendMessageByFcmTokenRequest.token())
                    .putData(ACTION, sendMessageByFcmTokenRequest.action().name())
                    .build());
        } catch (FirebaseMessagingException e) {
            throw new AlarmException(e);
        }
    }

    public void sendMessageByTopic(SendMessageByFcmTopicRequest sendFcmTokenMessageRequest) {
        try {
            firebaseMessaging.send(Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(sendFcmTokenMessageRequest.title())
                            .setBody(sendFcmTokenMessageRequest.body())
                            .build())
                    .setTopic(sendFcmTokenMessageRequest.topic())
                    .putData(ACTION, sendFcmTokenMessageRequest.action().name())
                    .build());
        } catch (FirebaseMessagingException e) {
            throw new AlarmException(e);
        }
    }

    public void sendMulticast(SendMessageByFcmTokensRequest sendMessageByFcmTokensRequest) {
        try {
            BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(MulticastMessage.builder()
                    .addAllTokens(sendMessageByFcmTokensRequest.allTokens())
                    .putData("title", sendMessageByFcmTokensRequest.title())
                    .putData("body", sendMessageByFcmTokensRequest.body())
                    .putData(ACTION, sendMessageByFcmTokensRequest.action().name())
                    .build());
            log.warn("multicast batchResponse : {}", batchResponse.toString());
        } catch (FirebaseMessagingException e) {
            throw new AlarmException(e);
        }
    }
}
