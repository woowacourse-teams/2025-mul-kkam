package backend.mulkkam.admin.service;

import backend.mulkkam.admin.dto.request.SendAdminBroadcastNotificationRequest;
import backend.mulkkam.admin.dto.response.GetAdminNotificationListResponse;
import backend.mulkkam.common.infrastructure.fcm.domain.Action;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.util.ChunkReader;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.NotificationInsertDto;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.notification.repository.NotificationBatchRepository;
import backend.mulkkam.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminNotificationService {

    private static final int CHUNK_SIZE = 1_000;

    private final NotificationRepository notificationRepository;
    private final NotificationBatchRepository notificationBatchRepository;
    private final MemberRepository memberRepository;
    private final DeviceRepository deviceRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public Page<GetAdminNotificationListResponse> getNotifications(Pageable pageable) {
        return notificationRepository.findAll(pageable)
                .map(GetAdminNotificationListResponse::from);
    }

    @Transactional
    public void sendBroadcastNotification(SendAdminBroadcastNotificationRequest request) {
        NotificationMessageTemplate template = new NotificationMessageTemplate(
                request.title(),
                request.body(),
                Action.GO_HOME,
                NotificationType.NOTICE
        );

        Long lastId = null;

        while (true) {
            List<Long> memberIds = getAllMemberIds(lastId);

            if (memberIds.isEmpty()) {
                break;
            }

            saveAndSendNotifications(memberIds, template);

            if (isLastChunk(memberIds)) {
                break;
            }

            lastId = memberIds.getLast();
        }
    }

    private List<Long> getAllMemberIds(Long lastId) {
        return ChunkReader.readChunk(
                memberRepository::findIdsAfter,
                lastId,
                CHUNK_SIZE
        );
    }

    private void saveAndSendNotifications(List<Long> memberIds, NotificationMessageTemplate template) {
        savedNotifications(memberIds, template);
        sendNotifications(memberIds, template);
    }

    private void savedNotifications(List<Long> memberIds, NotificationMessageTemplate template) {
        List<NotificationInsertDto> notificationInsertDtos = memberIds.stream()
                .map(memberId -> new NotificationInsertDto(template, memberId))
                .toList();
        notificationBatchRepository.batchInsert(notificationInsertDtos, CHUNK_SIZE);
    }

    private void sendNotifications(List<Long> memberIds, NotificationMessageTemplate template) {
        List<String> tokens = deviceRepository.findAllTokenByMemberIdIn(memberIds);
        publisher.publishEvent(template.toSendMessageByFcmTokensRequest(tokens));
    }

    private boolean isLastChunk(List<Long> memberIds) {
        return memberIds.size() < CHUNK_SIZE;
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        notificationRepository.delete(notification);
    }
}
