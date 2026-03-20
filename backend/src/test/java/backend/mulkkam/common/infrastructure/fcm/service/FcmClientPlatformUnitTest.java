package backend.mulkkam.common.infrastructure.fcm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.domain.DevicePlatform;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FcmClientPlatformUnitTest {

    @Mock
    FirebaseMessaging firebaseMessaging;

    @InjectMocks
    FcmClient fcmClient;

    @DisplayName("토큰 기반 메시지를 보낼 때")
    @Nested
    class SendMessageByToken {

        @DisplayName("iOS 플랫폼이면 APNs 알림 설정이 포함된다")
        @Test
        void success_whenPlatformIsIos() throws Exception {
            // given
            when(firebaseMessaging.send(any(Message.class))).thenReturn("msgId");

            SendMessageByFcmTokenRequest request = new SendMessageByFcmTokenRequest(
                    "title",
                    "body",
                    "token-123",
                    DevicePlatform.IOS,
                    Action.GO_HOME
            );

            // when
            fcmClient.sendMessageByToken(request);

            // then
            ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
            verify(firebaseMessaging).send(captor.capture());

            Message message = captor.getValue();
            ApnsConfig apnsConfig = invoke(message, "getApnsConfig");

            Map<String, Object> payload = getField(apnsConfig, "payload");
            Object apsObject = payload.get("aps");
            Map<String, Object> apsFields = extractApsFields(apsObject);

            Object alertObject = apsFields.get("alert");
            Map<String, Object> alertFields = extractAlertFields(alertObject);

            assertSoftly(softly -> {
                softly.assertThat(apnsConfig).isNotNull();
                softly.assertThat(alertFields.get("title")).isEqualTo("title");
                softly.assertThat(alertFields.get("body")).isEqualTo("body");
                softly.assertThat(apsFields.get("sound")).isEqualTo("default");
                softly.assertThat(apsFields.get("badge")).isEqualTo(0);
            });
        }

        @DisplayName("Android 플랫폼이면 APNs 설정이 없다")
        @Test
        void success_whenPlatformIsAndroid() throws Exception {
            // given
            when(firebaseMessaging.send(any(Message.class))).thenReturn("msgId");

            SendMessageByFcmTokenRequest request = new SendMessageByFcmTokenRequest(
                    "title",
                    "body",
                    "token-123",
                    DevicePlatform.ANDROID,
                    Action.GO_HOME
            );

            // when
            fcmClient.sendMessageByToken(request);

            // then
            ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
            verify(firebaseMessaging).send(captor.capture());

            Message message = captor.getValue();
            ApnsConfig apnsConfig = invoke(message, "getApnsConfig");
            assertThat(apnsConfig).isNull();
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractApsFields(Object apsObject) {
        if (apsObject instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return invoke(apsObject, "getFields");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractAlertFields(Object alertObject) {
        if (alertObject instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of(
                "title", getOptionalField(alertObject, "title"),
                "body", getOptionalField(alertObject, "body")
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> T invoke(Object target, String method) {
        try {
            Method declaredMethod = target.getClass().getDeclaredMethod(method);
            declaredMethod.setAccessible(true);
            return (T) declaredMethod.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object target, String field) {
        try {
            Field declaredField = target.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            return (T) declaredField.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOptionalField(Object target, String field) {
        try {
            Field declaredField = target.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            return (T) declaredField.get(target);
        } catch (Exception e) {
            return null;
        }
    }
}
