package backend.mulkkam.notification.service;

import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmService;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.dto.CreateTokenSuggestionNotificationRequest;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.notification.repository.SuggestionNotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SuggestionNotificationService {

    private final SuggestionNotificationRepository suggestionNotificationRepository;
    private final DeviceRepository deviceRepository;
    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;

    @Transactional
    public void createAndSendSuggestionNotification(CreateTokenSuggestionNotificationRequest createTokenSuggestionNotificationRequest) {
        Member member = createTokenSuggestionNotificationRequest.member();
        List<Device> devicesByMember = deviceRepository.findAllByMember(member);

        Notification savedNotification = notificationRepository.save(createTokenSuggestionNotificationRequest.toNotification());
        suggestionNotificationRepository.save(createTokenSuggestionNotificationRequest.toSuggestionNotification(savedNotification));

        sendNotificationByMember(createTokenSuggestionNotificationRequest, devicesByMember);
    }

    private void sendNotificationByMember(
            CreateTokenSuggestionNotificationRequest createTokenSuggestionNotificationRequest,
            List<Device> devicesByMember
    ) {
        for (Device device : devicesByMember) {
            SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest = createTokenSuggestionNotificationRequest.toSendMessageByFcmTokenRequest(
                    device.getToken());
            fcmService.sendMessageByToken(sendMessageByFcmTokenRequest);
        }
    }
}
