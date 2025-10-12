package backend.mulkkam.common.infrastructure.fcm.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FcmClient {

    private static final String ACTION = "action";

    private final FirebaseMessaging firebaseMessaging;

    public void sendMessageByToken(SendMessageByFcmTokenRequest req) {
        try {
            log.info("[MOCK FCM] token={}, title={}, body={}, action={}",
                    req.token(), req.title(), req.body(), req.action());
            Thread.sleep(350);
        } catch (InterruptedException e) {
            log.warn("[MOCK FCM] sleep interrupted");
        }

//        try {
//            firebaseMessaging.send(Message.builder()
//                    .setNotification(Notification.builder()
//                            .setTitle(req.title())
//                            .setBody(req.body())
//                            .build())
//                    .setToken(req.token())
//                    .putData(ACTION, req.action().name())
//                    .build());
//        } catch (FirebaseMessagingException e) {
//            throw new AlarmException(e);
//        }
    }

    public void sendMessageByTopic(SendMessageByFcmTopicRequest req) {
        try {
            log.info("[MOCK FCM] topic={}, title={}, body={}, action={}",
                    req.topic(), req.title(), req.body(), req.action());
            Thread.sleep(350);
        } catch (InterruptedException e) {
            log.warn("[MOCK FCM] sleep interrupted");
        }

//        try {
//            firebaseMessaging.send(Message.builder()
//                    .setNotification(Notification.builder()
//                            .setTitle(req.title())
//                            .setBody(req.body())
//                            .build())
//                    .setTopic(req.topic())
//                    .putData(ACTION, req.action().name())
//                    .build());
//        } catch (FirebaseMessagingException e) {
//            throw new AlarmException(e);
//        }
    }
}