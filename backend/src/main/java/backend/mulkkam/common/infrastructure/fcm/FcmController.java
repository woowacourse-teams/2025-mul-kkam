package backend.mulkkam.common.infrastructure.fcm;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/message/fcm/topic")
    public ResponseEntity<Void> sendMessageTopic(@RequestBody SendMessageByFcmTopicRequest sendFcmTopicMessageRequest)
            throws FirebaseMessagingException {
        fcmService.sendMessageByTopic(sendFcmTopicMessageRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/message/fcm/token")
    public ResponseEntity<Void> sendMessageToken(@RequestBody SendMessageByFcmTokenRequest sendFcmTokenMessageRequest)
            throws FirebaseMessagingException {
        fcmService.sendMessageByToken(sendFcmTokenMessageRequest);
        return ResponseEntity.ok().build();
    }
}
