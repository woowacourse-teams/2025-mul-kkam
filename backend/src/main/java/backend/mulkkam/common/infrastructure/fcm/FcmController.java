package backend.mulkkam.common.infrastructure.fcm;

import backend.mulkkam.common.exception.FailureBody;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmService;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FCM", description = "FCM 푸시 알림 API")
@RequiredArgsConstructor
@RestController
public class FcmController {

    private final FcmService fcmService;

    @Operation(summary = "토픽 기반 FCM 메시지 전송", description = "특정 토픽을 구독한 모든 사용자에게 FCM 메시지를 전송합니다.") @ApiResponse(responseCode = "200", description = "메시지 전송 성공") @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class))) @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class))) @ApiResponse(responseCode = "500", description = "FCM 서버 오류", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = { @ExampleObject(name = "FCM 서버 오류", summary = "외부 서비스 실패", value = "{\"code\":\"SEND_MESSAGE_FAILED\"}") }))
    @PostMapping("/message/fcm/topic")
    public ResponseEntity<Void> sendMessageTopic(
            @RequestBody SendMessageByFcmTopicRequest sendFcmTopicMessageRequest
    ) throws FirebaseMessagingException {
        fcmService.sendMessageByTopic(sendFcmTopicMessageRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "토큰 기반 FCM 메시지 전송", description = "특정 사용자의 FCM 토큰으로 개별 메시지를 전송합니다.") @ApiResponse(responseCode = "200", description = "메시지 전송 성공") @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = FailureBody.class))) @ApiResponse(responseCode = "404", description = "유효하지 않은 FCM 토큰", content = @Content(schema = @Schema(implementation = FailureBody.class))) @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = FailureBody.class))) @ApiResponse(responseCode = "500", description = "FCM 서버 오류", content = @Content(schema = @Schema(implementation = FailureBody.class), examples = { @ExampleObject(name = "FCM 서버 오류", summary = "외부 서비스 실패", value = "{\"code\":\"SEND_MESSAGE_FAILED\"}") }))
    @PostMapping("/message/fcm/token")
    public ResponseEntity<Void> sendMessageToken(
            @RequestBody SendMessageByFcmTokenRequest sendFcmTokenMessageRequest
    ) throws FirebaseMessagingException {
        fcmService.sendMessageByToken(sendFcmTokenMessageRequest);
        return ResponseEntity.ok().build();
    }
}
