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
            String messageId = firebaseMessaging.send(Message.builder()
                    .setNotification(Notification.builder()
                            .build())
                    .setToken(sendMessageByFcmTokenRequest.token())
                    .putData("title", sendMessageByFcmTokenRequest.title())
                    .putData("body", sendMessageByFcmTokenRequest.body())
                    .putData(ACTION, sendMessageByFcmTokenRequest.action().name())
                    .build());

            log.info("[FCM SUCCESS] token={}, messageId={}, action={}",
                    sendMessageByFcmTokenRequest.token(),
                    messageId,
                    sendMessageByFcmTokenRequest.action());
        } catch (FirebaseMessagingException e) {
            log.error("[FCM FAILED] token={}, errorCode={}, errorMessage={}, action={}",
                    sendMessageByFcmTokenRequest.token(),
                    e.getMessagingErrorCode(),
                    e.getMessage(),
                    sendMessageByFcmTokenRequest.action());
            throw new AlarmException(e);
        }
    }

    public void sendMessageByTopic(SendMessageByFcmTopicRequest sendFcmTokenMessageRequest) {
        try {
            firebaseMessaging.send(Message.builder()
                    .setNotification(Notification.builder()
                            .build())
                    .setTopic(sendFcmTokenMessageRequest.topic())
                    .putData("title", sendFcmTokenMessageRequest.title())
                    .putData("body", sendFcmTokenMessageRequest.body())
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
            log.info("[FCM MULTICAST] successCount={}, failureCount={}, totalCount={}, action={}",
                    batchResponse.getSuccessCount(),
                    batchResponse.getFailureCount(),
                    sendMessageByFcmTokensRequest.allTokens().size(),
                    sendMessageByFcmTokensRequest.action());

        } catch (FirebaseMessagingException e) {
            log.error("[FCM MULTICAST FAILED] tokenCount={}, errorCode={}, errorMessage={}, action={}",
                    sendMessageByFcmTokensRequest.allTokens().size(),
                    e.getMessagingErrorCode(),
                    e.getMessage(),
                    sendMessageByFcmTokensRequest.action());
            throw new AlarmException(e);
        }
    }
}
