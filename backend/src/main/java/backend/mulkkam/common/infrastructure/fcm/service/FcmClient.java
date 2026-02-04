package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.domain.DevicePlatform;
import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FcmClient {

    private static final String ACTION = "action";
    private static final String DEFAULT_SOUND = "default";
    private static final int CLEAR_BADGE = 0;

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
            Message message = messageBuilder(
                    sendMessageByFcmTokenRequest.title(),
                    sendMessageByFcmTokenRequest.body(),
                    sendMessageByFcmTokenRequest.action().name(),
                    sendMessageByFcmTokenRequest.platform()
            )
                    .setToken(sendMessageByFcmTokenRequest.token())
                    .build();
            String messageId = firebaseMessaging.send(message);

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
//        try {
//            log.info("[MOCK FCM] topic={}, title={}, body={}, action={}",
//                    sendFcmTokenMessageRequest.topic(), sendFcmTokenMessageRequest.title(),
//                    sendFcmTokenMessageRequest.body(), sendFcmTokenMessageRequest.action());
//            Thread.sleep(350);
//        } catch (InterruptedException e) {
//            log.warn("[MOCK FCM] sleep interrupted");
//        }

        try {
            Message message = messageBuilder(
                    sendFcmTokenMessageRequest.title(),
                    sendFcmTokenMessageRequest.body(),
                    sendFcmTokenMessageRequest.action().name(),
                    sendFcmTokenMessageRequest.platform()
            )
                    .setTopic(sendFcmTokenMessageRequest.topic())
                    .build();
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new AlarmException(e);
        }
    }

    public void sendMulticast(SendMessageByFcmTokensRequest sendMessageByFcmTokensRequest) {
        try {
            MulticastMessage multicastMessage = multicastMessageBuilder(
                    sendMessageByFcmTokensRequest.title(),
                    sendMessageByFcmTokensRequest.body(),
                    sendMessageByFcmTokensRequest.action().name(),
                    sendMessageByFcmTokensRequest.platform()
            )
                    .addAllTokens(sendMessageByFcmTokensRequest.allTokens())
                    .build();
            BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(multicastMessage);
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

    private Message.Builder messageBuilder(String title, String body, String action, DevicePlatform platform) {
        Message.Builder builder = Message.builder()
                .putData("title", title)
                .putData("body", body)
                .putData(ACTION, action);
        if (resolvePlatform(platform) == DevicePlatform.IOS) {
            builder.setApnsConfig(buildApnsConfig(title, body));
        }
        return builder;
    }

    private MulticastMessage.Builder multicastMessageBuilder(String title, String body, String action,
                                                            DevicePlatform platform) {
        MulticastMessage.Builder builder = MulticastMessage.builder()
                .putData("title", title)
                .putData("body", body)
                .putData(ACTION, action);
        if (resolvePlatform(platform) == DevicePlatform.IOS) {
            builder.setApnsConfig(buildApnsConfig(title, body));
        }
        return builder;
    }

    private ApnsConfig buildApnsConfig(String title, String body) {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setAlert(ApsAlert.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .setSound(DEFAULT_SOUND)
                        .setBadge(CLEAR_BADGE)
                        .build())
                .build();
    }

    private DevicePlatform resolvePlatform(DevicePlatform platform) {
        if (platform == null) {
            return DevicePlatform.ANDROID;
        }
        return platform;
    }
}
