package backend.mulkkam.notification.service;

import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.messaging.dto.NotificationMessage;
import backend.mulkkam.messaging.producer.NotificationMessageProducer;
import backend.mulkkam.notification.dto.NotificationInsertDto;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.notification.repository.NotificationBatchRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@Profile("!consumer") // API Server에서만 활성화
public class NotificationChunkService {

    private static final int CHUNK_SIZE = 1_000;

    private final NotificationBatchRepository notificationBatchRepository;
    private final DeviceRepository deviceRepository;
    private final Optional<NotificationMessageProducer> messageProducer;

    @Autowired
    public NotificationChunkService(
            NotificationBatchRepository notificationBatchRepository,
            DeviceRepository deviceRepository,
            @Autowired(required = false) NotificationMessageProducer messageProducer
    ) {
        this.notificationBatchRepository = notificationBatchRepository;
        this.deviceRepository = deviceRepository;
        this.messageProducer = Optional.ofNullable(messageProducer);
    }

    @Transactional
    public void processChunk(
            List<Long> memberIds,
            LocalDateTime now,
            NotificationMessageTemplate template
    ) {
        // 1. Notification 엔티티 저장
        saveNotifications(memberIds, template);

        // 2. 트랜잭션 커밋 후 RabbitMQ로 메시지 발행 (Producer가 있을 때만)
        messageProducer.ifPresent(producer -> 
                publishMessagesAfterCommit(memberIds, template, producer));
    }

    private void saveNotifications(
            List<Long> memberIds,
            NotificationMessageTemplate template
    ) {
        List<NotificationInsertDto> notificationInsertDtos = memberIds.stream()
                .map(memberId -> new NotificationInsertDto(template, memberId))
                .toList();
        notificationBatchRepository.batchInsert(notificationInsertDtos, CHUNK_SIZE);
    }

    private void publishMessagesAfterCommit(
            List<Long> memberIds,
            NotificationMessageTemplate template,
            NotificationMessageProducer producer
    ) {
        // 디바이스 토큰 조회
        List<Object[]> memberTokenPairs = deviceRepository.findMemberIdAndTokenByMemberIdIn(memberIds);

        // 메시지 생성
        List<NotificationMessage> messages = memberTokenPairs.stream()
                .map(pair -> NotificationMessage.create(
                        (Long) pair[0],
                        (String) pair[1],
                        template.title(),
                        template.body(),
                        template.action(),
                        template.type()
                ))
                .toList();

        // 트랜잭션 커밋 후 메시지 발행
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            producer.publishBatch(messages);
                        } catch (Exception e) {
                            log.error("[PUBLISH AFTER COMMIT FAILED] count={}, error={}",
                                    messages.size(), e.getMessage());
                            // 발행 실패 시 로깅만 - Notification은 이미 저장됨
                        }
                    }
                }
        );
    }
}
