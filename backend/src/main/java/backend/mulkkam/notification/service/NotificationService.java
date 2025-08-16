package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;

import backend.mulkkam.averageTemperature.dto.CreateTokenNotificationRequest;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTopicRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmService;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.dto.CreateTopicNotificationRequest;
import backend.mulkkam.notification.dto.GetNotificationsRequest;
import backend.mulkkam.notification.dto.ReadNotificationResponse;
import backend.mulkkam.notification.dto.GetNotificationsCountResponse;
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
    private final MemberRepository memberRepository;

    @Transactional
    public ReadNotificationsResponse getNotificationsAfter(
            GetNotificationsRequest getNotificationsRequest,
            Member member
    ) {
        validateSizeRange(getNotificationsRequest);

        LocalDateTime clientTime = getNotificationsRequest.clientTime();
        LocalDateTime limitStartDateTime = clientTime.minusDays(DAY_LIMIT);

        int size = getNotificationsRequest.size();
        Pageable pageable = Pageable.ofSize(size + 1);

        Long lastId = getNotificationsRequest.lastId();
        List<Notification> notifications = getNotificationsByLastIdAndMember(member, lastId, limitStartDateTime,
                pageable);

        boolean hasNext = notifications.size() > size;

        Long nextCursor = getNextCursor(hasNext, notifications);
        List<Notification> readNotifications = getReadNotifications(hasNext, notifications);
        List<ReadNotificationResponse> readNotificationResponses = toReadNotificationResponses(readNotifications);

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

    public GetNotificationsCountResponse getNotificationsCount(Member member) {
        long count = notificationRepository.countByIsReadFalseAndMember(member);
        return new GetNotificationsCountResponse(count);
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

    private List<Notification> getReadNotifications(
            boolean hasNext,
            List<Notification> notifications
    ) {
        if (hasNext) {
            notifications.removeLast();
        }
        notifications.forEach(
                notification -> notification.updateIsRead(true)
        );
        return notifications;
    }

    private List<Notification> getNotificationsByLastIdAndMember(
            Member member,
            Long lastId,
            LocalDateTime limitStartDateTime,
            Pageable pageable
    ) {
        if (lastId == null) {
            return notificationRepository.findLatest(member, limitStartDateTime, pageable);
        }
        return notificationRepository.findByCursor(member, lastId, limitStartDateTime, pageable);
    }

    private List<ReadNotificationResponse> toReadNotificationResponses(List<Notification> notifications) {
        return notifications.stream()
                .map(ReadNotificationResponse::new)
                .collect(Collectors.toList());
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
}
