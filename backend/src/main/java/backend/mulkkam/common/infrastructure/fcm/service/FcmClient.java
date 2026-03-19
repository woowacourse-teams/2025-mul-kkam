package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.domain.DevicePlatform;
import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import com.google.firebase.messaging.*;
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

    public void sendMessageByToken(SendMessageByFcmTokenRequest request) {
        try {
            Message message = messageBuilder(
                    request.title(),
                    request.body(),
                    request.action().name(),
                    request.platform()
            )
                    .setToken(request.token())
                    .build();

            String messageId = firebaseMessaging.send(message);

            log.info("[FCM SUCCESS] token={}, messageId={}, action={}",
                    request.token(), messageId, request.action());

        } catch (FirebaseMessagingException e) {
            log.error("[FCM FAILED] token={}, errorCode={}, errorMessage={}, action={}",
                    request.token(), e.getMessagingErrorCode(), e.getMessage(), request.action());
            throw new AlarmException(e);
        }
    }

    public void sendMessageByTopic(SendMessageByFcmTopicRequest request) {
        try {
            Message message = messageBuilder(
                    request.title(),
                    request.body(),
                    request.action().name(),
                    request.platform()
            )
                    .setTopic(request.topic())
                    .build();

            firebaseMessaging.send(message);

        } catch (FirebaseMessagingException e) {
            throw new AlarmException(e);
        }
    }

    public void sendMulticast(SendMessageByFcmTokensRequest request) {
        try {
            MulticastMessage message = multicastMessageBuilder(
                    request.title(),
                    request.body(),
                    request.action().name(),
                    request.platform()
            )
                    .addAllTokens(request.allTokens())
                    .build();

            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);

            log.info("[FCM MULTICAST] successCount={}, failureCount={}, totalCount={}, action={}",
                    response.getSuccessCount(),
                    response.getFailureCount(),
                    request.allTokens().size(),
                    request.action());

        } catch (FirebaseMessagingException e) {
            log.error("[FCM MULTICAST FAILED] tokenCount={}, errorCode={}, errorMessage={}, action={}",
                    request.allTokens().size(),
                    e.getMessagingErrorCode(),
                    e.getMessage(),
                    request.action());
            throw new AlarmException(e);
        }
    }

    private Message.Builder messageBuilder(String title, String body, String action, DevicePlatform platform) {
        Message.Builder builder = Message.builder()

                // ✅ iOS / Android 공통 알림 표시용 (핵심)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())

                // ✅ 앱 내부 로직 처리용
                .putData("title", title)
                .putData("body", body)
                .putData(ACTION, action);

        if (platform == DevicePlatform.IOS) {
            builder.setApnsConfig(buildApnsConfig(title, body));
        }

        return builder;
    }

    private MulticastMessage.Builder multicastMessageBuilder(String title, String body, String action,
                                                             DevicePlatform platform) {
        MulticastMessage.Builder builder = MulticastMessage.builder()

                // ✅ iOS / Android 공통 알림 표시용
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())

                // ✅ 앱 내부 처리용
                .putData("title", title)
                .putData("body", body)
                .putData(ACTION, action);

        if (platform == DevicePlatform.IOS) {
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
}