package backend.mulkkam.common.infrastructure.fcm.service;


import static backend.mulkkam.common.exception.errorCode.FirebaseErrorCode.SENDER_ID_MISMATCH;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import java.lang.reflect.Method;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FcmServiceUnitTest {

    @Mock
    FirebaseMessaging firebaseMessaging;

    @InjectMocks
    FcmService fcmService;

    @DisplayName("토큰 형식으로 알림을 보낼 때")
    @Nested
    class SendMessageByToken {

        @DisplayName("유효한 토큰 요청 시 알림이 정상 발송되고 예외가 발생하지 않는다")
        @Test
        void success_validInput() throws FirebaseMessagingException {
            // given
            when(firebaseMessaging.send(any(Message.class))).thenReturn("msgId");

            SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest = new SendMessageByFcmTokenRequest("title",
                    "body", "token-123", Action.GO_NOTIFICATION);

            // then
            Assertions.assertThatCode(
                    () -> fcmService.sendMessageByToken(sendMessageByFcmTokenRequest)
            ).doesNotThrowAnyException();

            ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
            verify(firebaseMessaging, times(1)).send(messageArgumentCaptor.capture());

            Message msg = messageArgumentCaptor.getValue();

            String token = invoke(msg, "getToken");
            Map<String, String> data = invoke(msg, "getData");
            String topic = invoke(msg, "getTopic");
            String condition = invoke(msg, "getCondition");

            assertSoftly(softly -> {
                softly.assertThat(token).isEqualTo("token-123");
                softly.assertThat(data.get("action")).isEqualTo(Action.GO_NOTIFICATION.name());
                softly.assertThat(topic).isNull();
                softly.assertThat(condition).isNull();
            });
        }

        @DisplayName("실패 시 예외가 발생한다")
        @Test
        void sendMessageByToken_wrapsFcmException() throws Exception {
            // given
            FirebaseMessagingException firebaseMessagingException = mock(FirebaseMessagingException.class);
            when(firebaseMessagingException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.SENDER_ID_MISMATCH);
            when(firebaseMessaging.send(any(Message.class))).thenThrow(firebaseMessagingException);

            SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest = new SendMessageByFcmTokenRequest("title",
                    "body", "token-123", Action.GO_NOTIFICATION);

            // when & then
            assertThatThrownBy(
                    () -> fcmService.sendMessageByToken(sendMessageByFcmTokenRequest)
            ).isInstanceOf(AlarmException.class)
                    .hasMessage(SENDER_ID_MISMATCH.name());

            verify(firebaseMessaging, times(1)).send(any(Message.class));
        }
    }

    @DisplayName("토픽 형식으로 알림을 보낼 때")
    @Nested
    class SendMessageByTopic {

        @DisplayName("성공 시 예외가 터지지 않는다")
        @Test
        void success_validInput() throws FirebaseMessagingException {
            // given
            when(firebaseMessaging.send(any(Message.class))).thenReturn("msgId");

            SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest = new SendMessageByFcmTopicRequest("title",
                    "body", "exampleTopic", Action.GO_NOTIFICATION);

            // when & then
            Assertions.assertThatCode(
                    () -> fcmService.sendMessageByTopic(sendMessageByFcmTopicRequest)
            ).doesNotThrowAnyException();

            ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
            verify(firebaseMessaging, times(1)).send(messageArgumentCaptor.capture());

            Message msg = messageArgumentCaptor.getValue();

            String token = invoke(msg, "getToken");
            Map<String, String> data = invoke(msg, "getData");
            String topic = invoke(msg, "getTopic");
            String condition = invoke(msg, "getCondition");

            assertSoftly(softly -> {
                softly.assertThat(topic).isEqualTo("exampleTopic");
                softly.assertThat(token).isNull();
                softly.assertThat(data.get("action")).isEqualTo(Action.GO_NOTIFICATION.name());
                softly.assertThat(condition).isNull();
            });
        }

        @DisplayName("실패 시 예외가 발생한다")
        @Test
        void error() throws FirebaseMessagingException {
            // given
            FirebaseMessagingException firebaseMessagingException = mock(FirebaseMessagingException.class);
            when(firebaseMessagingException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.SENDER_ID_MISMATCH);
            when(firebaseMessaging.send(any(Message.class))).thenThrow(firebaseMessagingException);

            SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest = new SendMessageByFcmTopicRequest("title",
                    "body", "exampleTopic", Action.GO_NOTIFICATION);

            // when & then
            assertThatThrownBy(
                    () -> fcmService.sendMessageByTopic(sendMessageByFcmTopicRequest)
            ).isInstanceOf(AlarmException.class)
                    .hasMessage(SENDER_ID_MISMATCH.name());

            verify(firebaseMessaging, times(1)).send(any(Message.class));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T invoke(Message message, String method) {
        try {
            Method declaredMethod = Message.class.getDeclaredMethod(method);
            declaredMethod.setAccessible(true);
            return (T) declaredMethod.invoke(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
