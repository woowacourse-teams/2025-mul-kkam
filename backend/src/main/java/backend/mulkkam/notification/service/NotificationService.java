package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.BadGateErrorCode.SEND_MESSAGE_FAILED;
import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_PAGE_SIZE_RANGE;

import backend.mulkkam.averageTemperature.dto.CreateTokenNotificationRequest;
import backend.mulkkam.common.dto.MemberDetails;
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
import backend.mulkkam.notification.dto.ReadNotificationsResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final int DAY_LIMIT = 7;

    private final FcmService fcmService;
    private final DeviceRepository deviceRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

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
        List<Notification> notifications = getNotificationsByLastIdAndMember(
                memberDetails,
                lastId,
                limitStartDateTime,
                pageable
        );

        boolean hasNext = notifications.size() > size;

        Long nextCursor = getNextCursor(hasNext, notifications);
        List<ReadNotificationResponse> readNotificationResponses = toReadNotificationResponses(hasNext, notifications);

        return new ReadNotificationsResponse(readNotificationResponses, nextCursor);
    }

    private void validateSizeRange(GetNotificationsRequest getNotificationsRequest) {
        if (getNotificationsRequest.size() < 1) {
            throw new CommonException(INVALID_PAGE_SIZE_RANGE);
        }
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

    private List<ReadNotificationResponse> toReadNotificationResponses(
            boolean hasNext,
            List<Notification> notifications
    ) {
        if (hasNext) {
            notifications.removeLast();
        }

        return notifications.stream()
                .map(ReadNotificationResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createTopicNotification(CreateTopicNotificationRequest createTopicNotificationRequest) {
        List<Member> allMember = memberRepository.findAll();
        for (Member member : allMember) {
            Notification notification = createTopicNotificationRequest.toNotification(member);
            notificationRepository.save(notification);
        }

        SendMessageByFcmTopicRequest sendMessageByFcmTopicRequest = createTopicNotificationRequest.toSendMessageByFcmTopicRequest();
        try {
            fcmService.sendMessageByTopic(sendMessageByFcmTopicRequest);
        } catch (FirebaseMessagingException e) {
            throw new CommonException(SEND_MESSAGE_FAILED);
        }
    }

    @Transactional
    public void createTokenNotification(CreateTokenNotificationRequest createTokenNotificationRequest) {
        Member member = createTokenNotificationRequest.member();
        List<Device> devicesByMember = deviceRepository.findAllByMember(member);

        notificationRepository.save(createTokenNotificationRequest.toNotification());

        try {
            sendNotificationByMember(createTokenNotificationRequest, devicesByMember);
        } catch (FirebaseMessagingException e) {
            throw new CommonException(SEND_MESSAGE_FAILED);
        }
    }

    private void sendNotificationByMember(CreateTokenNotificationRequest createTokenNotificationRequest,
                                          List<Device> devicesByMember)
            throws FirebaseMessagingException {
        for (Device device : devicesByMember) {
            SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest = createTokenNotificationRequest.toSendMessageByFcmTokenRequest(
                    device.getToken());
            fcmService.sendMessageByToken(sendMessageByFcmTokenRequest);
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
}
