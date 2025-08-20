package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;
import static backend.mulkkam.common.exception.errorCode.ForbiddenErrorCode.NOT_PERMITTED_FOR_NOTIFICATION;

import backend.mulkkam.averageTemperature.dto.CreateTokenNotificationRequest;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmService;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.NotificationType;
import backend.mulkkam.notification.domain.SuggestionNotification;
import backend.mulkkam.notification.dto.CreateTopicNotificationRequest;
import backend.mulkkam.notification.dto.GetNotificationResponse;
import backend.mulkkam.notification.dto.GetNotificationsRequest;
import backend.mulkkam.notification.dto.GetSuggestionNotificationResponse;
import backend.mulkkam.notification.dto.GetUnreadNotificationsCountResponse;
import backend.mulkkam.notification.dto.NotificationResponse;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.notification.repository.SuggestionNotificationRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final int DAY_LIMIT = 7;

    private final FcmService fcmService;
    private final DeviceRepository deviceRepository;
    private final NotificationRepository notificationRepository;
    private final SuggestionNotificationRepository suggestionNotificationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReadNotificationsResponse getNotificationsAfter(
            GetNotificationsRequest getNotificationsRequest,
            MemberDetails memberDetails
    ) {
        validateSizeRange(getNotificationsRequest);

        LocalDateTime clientTime = getNotificationsRequest.clientTime();
        LocalDateTime limitStartDateTime = clientTime.minusDays(DAY_LIMIT);

        int size = getNotificationsRequest.size();
        Pageable pageable = Pageable.ofSize(size + 1);

        Long lastId = getNotificationsRequest.lastId();
        List<Notification> pagedNotifications = getNotificationsByLastIdAndMember(
                memberDetails,
                lastId,
                limitStartDateTime,
                pageable
        );

        boolean hasNext = pagedNotifications.size() > size;

        Long nextCursor = getNextCursor(hasNext, pagedNotifications);
        List<Notification> notifications = getNotifications(hasNext, pagedNotifications);
        List<NotificationResponse> readNotificationResponses = toNotificationResponses(notifications);
        notifications.forEach(
                notification -> notification.updateIsRead(true)
        );

        return new ReadNotificationsResponse(readNotificationResponses, nextCursor);
    }

    @Transactional
    public void createAndSendTopicNotification(CreateTopicNotificationRequest createTopicNotificationRequest) {
        List<Member> allMember = memberRepository.findAll();
        for (Member member : allMember) {
            Notification notification = createTopicNotificationRequest.toNotification(member);
            notificationRepository.save(notification);
        }

        SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest = createTopicNotificationRequest.toSendMessageByFcmTopicRequest();
        fcmService.sendMessageByTopic(sendMessageByFcmTopicRequest);
    }

    @Transactional
    public void createAndSendTokenNotification(CreateTokenNotificationRequest createTokenNotificationRequest) {
        Member member = createTokenNotificationRequest.member();
        List<Device> devicesByMember = deviceRepository.findAllByMember(member);

        notificationRepository.save(createTokenNotificationRequest.toNotification());
        sendNotificationByMember(createTokenNotificationRequest, devicesByMember);
    }

    public GetUnreadNotificationsCountResponse getNotificationsCount(MemberDetails memberDetails) {
        Long memberId = memberDetails.id();
        long count = notificationRepository.countByIsReadFalseAndMemberId(memberId);
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
            SuggestionNotification suggestionNotification = getSuggestionNotification(notificationId);
            suggestionNotificationRepository.delete(suggestionNotification);
        }

        notificationRepository.delete(notification);
    }

    private void validateSizeRange(GetNotificationsRequest getNotificationsRequest) {
        if (getNotificationsRequest.size() < 1) {
            throw new CommonException(INVALID_PAGE_SIZE_RANGE);
        }
    }

    private Long getNextCursor(
            boolean hasNext,
            List<Notification> notifications
    ) {
        if (hasNext) {
            return notifications.getLast().getId();
        }
        return null;
    }

    private List<Notification> getNotifications(
            boolean hasNext,
            List<Notification> notifications
    ) {
        if (hasNext) {
            notifications.removeLast();
        }
        return notifications;
    }

    private List<Notification> getNotificationsByLastIdAndMember(
            MemberDetails memberDetails,
            Long lastId,
            LocalDateTime limitStartDateTime,
            Pageable pageable
    ) {
        Long memberId = memberDetails.id();
        if (lastId == null) {
            return notificationRepository.findLatest(memberId, limitStartDateTime, pageable);
        }
        return notificationRepository.findByCursor(memberId, lastId, limitStartDateTime, pageable);
    }

    private List<NotificationResponse> toNotificationResponses(List<Notification> notifications) {
        List<NotificationResponse> notificationResponses = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationResponse notificationResponse = getNotificationResponse(notification);
            notificationResponses.add(notificationResponse);

        }
        return notificationResponses;
    }

    private NotificationResponse getNotificationResponse(Notification notification) {
        if (notification.getNotificationType() != NotificationType.SUGGESTION) {
            return new GetNotificationResponse(notification);
        }
        SuggestionNotification suggestionNotification = suggestionNotificationRepository.getSuggestionNotificationByNotification(
                notification);
        return new GetSuggestionNotificationResponse(notification, suggestionNotification);
    }

    private void sendNotificationByMember(
            CreateTokenNotificationRequest createTokenNotificationRequest,
            List<Device> devicesByMember
    ) {
        for (Device device : devicesByMember) {
            SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest = createTokenNotificationRequest.toSendMessageByFcmTokenRequest(
                    device.getToken());
            fcmService.sendMessageByToken(sendMessageByFcmTokenRequest);
        }
    }

    private Notification getNotification(Long id) {
        return notificationRepository.findByIdWithMember(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_NOTIFICATION));
    }

    private SuggestionNotification getSuggestionNotification(Long id) {
        return suggestionNotificationRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_SUGGESTION_NOTIFICATION));
    }
}
