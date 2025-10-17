package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_NOTIFICATION;

import backend.mulkkam.averageTemperature.dto.CreateTokenNotificationRequest;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.common.util.ChunkReader;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.dto.NotificationInsertDto;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.notification.dto.ReadNotificationRow;
import backend.mulkkam.notification.dto.request.ReadNotificationsRequest;
import backend.mulkkam.notification.dto.response.GetNotificationResponse;
import backend.mulkkam.notification.dto.response.GetSuggestionNotificationResponse;
import backend.mulkkam.notification.dto.response.GetUnreadNotificationsCountResponse;
import backend.mulkkam.notification.dto.response.NotificationResponse;
import backend.mulkkam.notification.dto.response.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationBatchRepository;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.notification.repository.ReminderScheduleRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final int DAY_LIMIT = 7;
    private static final int CHUNK_SIZE = 1_000;

    private final SuggestionNotificationService suggestionNotificationService;
    private final DeviceRepository deviceRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationBatchRepository notificationBatchRepository;
    private final ReminderScheduleRepository reminderScheduleRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void processReminderNotifications(LocalDateTime now) {
        NotificationMessageTemplate template = RemindNotificationMessageTemplateProvider.getRandomMessageTemplate();

        Long lastId = null;

        while (true) {
            List<Long> memberIds = getMemberIdsForSendingNotification(now, lastId);

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

    private List<Long> getMemberIdsForSendingNotification(LocalDateTime now, Long lastId) {
        return ChunkReader.readChunk(
                (id, pageable) -> reminderScheduleRepository
                        .findAllActiveMemberIdsBySchedule(
                                now.toLocalTime(),
                                id,
                                pageable
                        ),
                lastId,
                CHUNK_SIZE
        );
    }

    @Transactional
    public ReadNotificationsResponse readNotificationsAfter(
            ReadNotificationsRequest readNotificationsRequest,
            MemberDetails memberDetails
    ) {
        validateSizeRange(readNotificationsRequest);

        LocalDateTime clientTime = readNotificationsRequest.clientTime();
        LocalDateTime limitStartDateTime = clientTime.minusDays(DAY_LIMIT);

        int size = readNotificationsRequest.size();
        Pageable pageable = Pageable.ofSize(size + 1);

        Long lastId = readNotificationsRequest.lastId();
        List<ReadNotificationRow> pagedNotificationResponses = getNotificationResponsesByLastIdAndMember(
                memberDetails,
                lastId,
                limitStartDateTime,
                pageable
        );

        boolean hasNext = pagedNotificationResponses.size() > size;

        List<ReadNotificationRow> readNotificationRows = dropExtraNotificationResponse(hasNext,
                pagedNotificationResponses);

        Long nextCursor = getNextCursor(hasNext, readNotificationRows);
        List<NotificationResponse> readNotificationResponses = toNotificationResponses(readNotificationRows);

        List<Long> ids = readNotificationRows.stream()
                .map(ReadNotificationRow::id)
                .toList();

        notificationRepository.markReadInBulk(ids);

        return new ReadNotificationsResponse(readNotificationResponses, nextCursor);
    }

    @Transactional
    public void createAndSendTokenNotification(CreateTokenNotificationRequest createTokenNotificationRequest) {
        Member member = createTokenNotificationRequest.member();
        List<Device> devicesByMember = deviceRepository.findAllByMember(member);

        notificationRepository.save(createTokenNotificationRequest.toNotification());

        sendPushToMemberDevices(createTokenNotificationRequest, devicesByMember);
    }

    public GetUnreadNotificationsCountResponse getUnReadNotificationsCount(MemberDetails memberDetails,
                                                                           LocalDateTime clientTime) {
        Long memberId = memberDetails.id();
        LocalDateTime limitStartDateTime = clientTime.minusDays(DAY_LIMIT);

        long count = notificationRepository.countUnReadByMemberId(memberId, limitStartDateTime);
        return new GetUnreadNotificationsCountResponse(count);
    }

    @Transactional
    public void delete(
            MemberDetails memberDetails,
            Long notificationId
    ) {
        Long memberId = memberDetails.id();

        Notification notification = getNotification(notificationId);

        if (!notification.isOwnedBy(memberId)) {
            throw new CommonException(NOT_PERMITTED_FOR_NOTIFICATION);
        }

        if (notification.isSuggestion()) {
            suggestionNotificationService.delete(notificationId);
        }

        notificationRepository.delete(notification);
    }

    private void validateSizeRange(ReadNotificationsRequest readNotificationsRequest) {
        if (readNotificationsRequest.size() < 1) {
            throw new CommonException(INVALID_PAGE_SIZE_RANGE);
        }
    }

    private Long getNextCursor(
            boolean hasNext,
            List<ReadNotificationRow> notifications
    ) {
        if (hasNext) {
            return notifications.getLast().id();
        }
        return null;
    }

    private List<ReadNotificationRow> dropExtraNotificationResponse(
            boolean hasNext,
            List<ReadNotificationRow> notifications
    ) {
        if (hasNext) {
            notifications.removeLast();
        }
        return notifications;
    }

    private List<ReadNotificationRow> getNotificationResponsesByLastIdAndMember(
            MemberDetails memberDetails,
            Long lastId,
            LocalDateTime limitStartDateTime,
            Pageable pageable
    ) {
        Long memberId = memberDetails.id();
        if (lastId == null) {
            return notificationRepository.findLatestRows(memberId, limitStartDateTime, pageable);
        }
        return notificationRepository.findByCursorRows(memberId, lastId, limitStartDateTime, pageable);
    }

    private List<NotificationResponse> toNotificationResponses(List<ReadNotificationRow> readNotificationRows) {
        return readNotificationRows.stream()
                .map(this::getNotificationResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse getNotificationResponse(ReadNotificationRow readNotificationRow) {
        if (readNotificationRow.notificationType() != NotificationType.SUGGESTION) {
            return new GetNotificationResponse(readNotificationRow);
        }
        return new GetSuggestionNotificationResponse(readNotificationRow);
    }

    private void sendPushToMemberDevices(
            CreateTokenNotificationRequest createTokenNotificationRequest,
            List<Device> devicesByMember
    ) {
        List<String> tokens = extractTokensFromDevices(devicesByMember);
        SendMessageByFcmTokensRequest sendMessageByFcmTokensRequest = createTokenNotificationRequest.toSendMessageByFcmTokensRequest(
                tokens);
        publisher.publishEvent(sendMessageByFcmTokensRequest);
    }

    private List<String> extractTokensFromDevices(List<Device> devices) {
        return devices.stream()
                .map(Device::getToken)
                .toList();
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
        List<String> tokens = readDeviceTokens(memberIds);
        publisher.publishEvent(template.toSendMessageByFcmTokensRequest(tokens));
    }

    private List<String> readDeviceTokens(List<Long> memberIds) {
        return deviceRepository.findAllTokenByMemberIdIn(memberIds);
    }

    private boolean isLastChunk(List<Long> memberIds) {
        return memberIds.size() < CHUNK_SIZE;
    }

    private Notification getNotification(Long id) {
        return notificationRepository.findByIdWithMember(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_NOTIFICATION));
    }
}
