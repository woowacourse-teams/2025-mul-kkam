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
public class FcmClient {

    private static final String ACTION = "action";

    private final FirebaseMessaging firebaseMessaging;

    public void sendMessageByToken(SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest) {
//        try {
//            log.info("[MOCK FCM] token={}, title={}, body={}, action={}",
//                    sendMessageByFcmTokenRequest.token(), sendMessageByFcmTokenRequest.title(),
//                    sendMessageByFcmTokenRequest.body(), sendMessageByFcmTokenRequest.action());
//            Thread.sleep(350);
//        } catch (InterruptedException e) {
//            log.warn("[MOCK FCM] sleep interrupted");
//        }

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
//        try {
//            log.info("[MOCK FCM] topic={}, title={}, body={}, action={}",
//                    sendFcmTokenMessageRequest.topic(), sendFcmTokenMessageRequest.title(),
//                    sendFcmTokenMessageRequest.body(), sendFcmTokenMessageRequest.action());
//            Thread.sleep(350);
//        } catch (InterruptedException e) {
//            log.warn("[MOCK FCM] sleep interrupted");
//        }

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
                    .setNotification(Notification.builder()
                            .setTitle(sendMessageByFcmTokensRequest.title())
                            .setBody(sendMessageByFcmTokensRequest.body())
                            .build())
                    .addAllTokens(sendMessageByFcmTokensRequest.allTokens())
                    .putData(ACTION, sendMessageByFcmTokensRequest.action().name())
                    .build());
            log.warn("multicast batchResponse : {}", batchResponse.toString());
        } catch (FirebaseMessagingException e) {
            throw new AlarmException(e);
        }
    }
}
