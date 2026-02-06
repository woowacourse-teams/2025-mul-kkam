package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.common.util.concurrent.MoreExecutors;
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

        ApiFuture<String> future = firebaseMessaging.sendAsync(Message.builder()
                .setNotification(Notification.builder()
                        .build())
                .setToken(sendMessageByFcmTokenRequest.token())
                .putData("title", sendMessageByFcmTokenRequest.title())
                .putData("body", sendMessageByFcmTokenRequest.body())
                .putData(ACTION, sendMessageByFcmTokenRequest.action().name())
                .build());

        ApiFutures.addCallback(future, new ApiFutureCallback<>() {
            @Override
            public void onSuccess(String messageId) {
                log.info("[FCM SUCCESS] token={}, messageId={}, action={}",
                        sendMessageByFcmTokenRequest.token(),
                        messageId,
                        sendMessageByFcmTokenRequest.action());
            }

            @Override
            public void onFailure(Throwable t) {
                logFailure("token", sendMessageByFcmTokenRequest.token(), t,
                        sendMessageByFcmTokenRequest.action().name());
            }
        }, MoreExecutors.directExecutor());
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

        ApiFuture<String> future = firebaseMessaging.sendAsync(Message.builder()
                .setNotification(Notification.builder()
                        .build())
                .setTopic(sendFcmTokenMessageRequest.topic())
                .putData("title", sendFcmTokenMessageRequest.title())
                .putData("body", sendFcmTokenMessageRequest.body())
                .putData(ACTION, sendFcmTokenMessageRequest.action().name())
                .build());

        ApiFutures.addCallback(future, new ApiFutureCallback<>() {
            @Override
            public void onSuccess(String messageId) {
                log.info("[FCM SUCCESS] topic={}, messageId={}, action={}",
                        sendFcmTokenMessageRequest.topic(),
                        messageId,
                        sendFcmTokenMessageRequest.action());
            }

            @Override
            public void onFailure(Throwable t) {
                logFailure("topic", sendFcmTokenMessageRequest.topic(), t,
                        sendFcmTokenMessageRequest.action().name());
            }
        }, MoreExecutors.directExecutor());
    }

    public void sendMulticast(SendMessageByFcmTokensRequest sendMessageByFcmTokensRequest) {
        ApiFuture<BatchResponse> future = firebaseMessaging.sendEachForMulticastAsync(MulticastMessage.builder()
                .addAllTokens(sendMessageByFcmTokensRequest.allTokens())
                .putData("title", sendMessageByFcmTokensRequest.title())
                .putData("body", sendMessageByFcmTokensRequest.body())
                .putData(ACTION, sendMessageByFcmTokensRequest.action().name())
                .build());

        ApiFutures.addCallback(future, new ApiFutureCallback<>() {
            @Override
            public void onSuccess(BatchResponse batchResponse) {
                log.info("[FCM MULTICAST] successCount={}, failureCount={}, totalCount={}, action={}",
                        batchResponse.getSuccessCount(),
                        batchResponse.getFailureCount(),
                        sendMessageByFcmTokensRequest.allTokens().size(),
                        sendMessageByFcmTokensRequest.action());
            }

            @Override
            public void onFailure(Throwable t) {
                logFailure("tokenCount",
                        String.valueOf(sendMessageByFcmTokensRequest.allTokens().size()),
                        t,
                        sendMessageByFcmTokensRequest.action().name());
            }
        }, MoreExecutors.directExecutor());
    }

    private void logFailure(String targetType, String targetValue, Throwable t, String action) {
        if (t instanceof FirebaseMessagingException firebaseMessagingException) {
            log.error("[FCM FAILED] {}={}, errorCode={}, errorMessage={}, action={}",
                    targetType,
                    targetValue,
                    firebaseMessagingException.getMessagingErrorCode(),
                    firebaseMessagingException.getMessage(),
                    action);
            return;
        }

        log.error("[FCM FAILED] {}={}, errorMessage={}, action={}",
                targetType,
                targetValue,
                t.getMessage(),
                action);
    }
}
