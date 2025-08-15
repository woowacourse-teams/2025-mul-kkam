package backend.mulkkam.notification.service;

import backend.mulkkam.averageTemperature.dto.CreateTokenNotificationRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.dto.CreateActivityNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ActivityService {

    private final NotificationService notificationService;

    public void createActivityNotification(
            CreateActivityNotification createActivityNotification,
            Member member
    ) {
        CreateTokenNotificationRequest createTokenNotificationRequest = createActivityNotification.toFcmToken(member);
        notificationService.createAndSendTokenNotification(createTokenNotificationRequest);
    }
}
