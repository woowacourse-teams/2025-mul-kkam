package backend.mulkkam.messaging.consumer;

import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.exception.errorCode.FirebaseErrorCode;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmClient;
import backend.mulkkam.messaging.dto.NotificationMessage;
import backend.mulkkam.messaging.inbox.domain.InboxMessage;
import backend.mulkkam.messaging.inbox.repository.InboxMessageRepository;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("consumer") // Consumer Worker에서만 활성화
public class NotificationMessageConsumer {

    private static final int MAX_RETRY_COUNT = 3;

    private final InboxMessageRepository inboxMessageRepository;
    private final FcmClient fcmClient;
    private final RabbitTemplate rabbitTemplate;
    private final CompensatingActionClient compensatingActionClient;

    @Value("${rabbitmq.exchange.notification:notification.exchange}.dlx")
    private String dlxExchange;

    @Value("${rabbitmq.routing-key.notification:notification.routing-key}")
    private String routingKey;

    @RabbitListener(
            queues = "${rabbitmq.queue.notification.main:notification.queue}",
            ackMode = "MANUAL"
    )
    public void consume(
            NotificationMessage message,
            Channel channel,
            Message amqpMessage
    ) throws IOException {
        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();
        String messageId = message.messageId();

        log.info("[CONSUME START] messageId={}, memberId={}, attempt={}",
                messageId, message.memberId(), message.attemptCount());

        try {
            // 1. Inbox Pattern - 중복 체크 및 저장
            if (!tryInsertInbox(message)) {
                // 이미 처리된 메시지 - ACK 후 종료
                log.info("[DUPLICATE MESSAGE] messageId={}, ACK immediately", messageId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 2. FCM 전송
            sendFcmNotification(message);

            // 3. Inbox 상태 업데이트 및 ACK
            markInboxCompleted(messageId);
            channel.basicAck(deliveryTag, false);

            log.info("[CONSUME SUCCESS] messageId={}, memberId={}",
                    messageId, message.memberId());

        } catch (AlarmException e) {
            handleFcmError(message, e, channel, deliveryTag);
        } catch (Exception e) {
            handleUnexpectedError(message, e, channel, deliveryTag);
        }
    }

    /**
     * Inbox에 메시지 삽입 시도
     *
     * @return true: 새 메시지, false: 중복 메시지
     */
    @Transactional
    public boolean tryInsertInbox(NotificationMessage message) {
        try {
            InboxMessage inboxMessage = InboxMessage.create(
                    message.messageId(),
                    message.memberId(),
                    message.token()
            );
            inboxMessageRepository.save(inboxMessage);
            return true;
        } catch (DataIntegrityViolationException e) {
            // Unique constraint violation = 중복 메시지
            return false;
        }
    }

    private void sendFcmNotification(NotificationMessage message) {
        fcmClient.sendMessageByToken(new SendMessageByFcmTokenRequest(
                message.title(),
                message.body(),
                message.token(),
                message.action()
        ));
    }

    @Transactional
    public void markInboxCompleted(String messageId) {
        inboxMessageRepository.findById(messageId)
                .ifPresent(InboxMessage::markCompleted);
    }

    @Transactional
    public void markInboxFailed(String messageId, String errorMessage) {
        inboxMessageRepository.findById(messageId)
                .ifPresent(inbox -> inbox.markFailed(errorMessage));
    }

    private void handleFcmError(
            NotificationMessage message,
            AlarmException e,
            Channel channel,
            long deliveryTag
    ) throws IOException {
        String errorCodeName = e.getErrorCode() != null ? e.getErrorCode().name() : "INTERNAL";
        FirebaseErrorCode errorCode = FirebaseErrorCode.findByName(errorCodeName);

        log.warn("[FCM ERROR] messageId={}, errorCode={}", message.messageId(), errorCode);

        if (FirebaseErrorCode.isPermanentError(errorCode)) {
            // 영구 오류 - Compensating Action 호출 후 ACK
            handlePermanentError(message, errorCode, channel, deliveryTag);
        } else {
            // 일시적 오류 - Retry
            handleTransientError(message, channel, deliveryTag);
        }
    }

    private void handlePermanentError(
            NotificationMessage message,
            FirebaseErrorCode errorCode,
            Channel channel,
            long deliveryTag
    ) throws IOException {
        log.error("[PERMANENT ERROR] messageId={}, errorCode={}, sending to DLQ",
                message.messageId(), errorCode);

        // Compensating Action - 유효하지 않은 토큰 삭제 요청
        if (errorCode == FirebaseErrorCode.UNREGISTERED ||
                errorCode == FirebaseErrorCode.INVALID_ARGUMENT) {
            try {
                compensatingActionClient.requestTokenDeletion(
                        message.memberId(),
                        message.token()
                );
            } catch (Exception callbackError) {
                log.error("[CALLBACK FAILED] messageId={}, error={}",
                        message.messageId(), callbackError.getMessage());
            }
        }

        markInboxFailed(message.messageId(), errorCode.name());

        // DLQ로 이동
        rabbitTemplate.convertAndSend(dlxExchange, routingKey + ".dlq", message);
        channel.basicAck(deliveryTag, false);
    }

    private void handleTransientError(
            NotificationMessage message,
            Channel channel,
            long deliveryTag
    ) throws IOException {
        int nextAttempt = message.attemptCount() + 1;

        if (nextAttempt >= MAX_RETRY_COUNT) {
            log.error("[MAX RETRY EXCEEDED] messageId={}, sending to DLQ",
                    message.messageId());
            markInboxFailed(message.messageId(), "MAX_RETRY_EXCEEDED");
            rabbitTemplate.convertAndSend(dlxExchange, routingKey + ".dlq", message);
            channel.basicAck(deliveryTag, false);
            return;
        }

        log.info("[RETRY] messageId={}, attempt={}", message.messageId(), nextAttempt);

        // Retry Queue로 재전송
        NotificationMessage retryMessage = message.incrementAttempt();
        rabbitTemplate.convertAndSend(dlxExchange, routingKey + ".retry", retryMessage);
        channel.basicAck(deliveryTag, false);
    }

    private void handleUnexpectedError(
            NotificationMessage message,
            Exception e,
            Channel channel,
            long deliveryTag
    ) throws IOException {
        log.error("[UNEXPECTED ERROR] messageId={}, error={}",
                message.messageId(), e.getMessage(), e);
        markInboxFailed(message.messageId(), e.getMessage());
        rabbitTemplate.convertAndSend(dlxExchange, routingKey + ".dlq", message);
        channel.basicAck(deliveryTag, false);
    }
}
