package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_SUGGESTION_NOTIFICATION;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.common.infrastructure.fcm.service.FcmService;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.intake.dto.request.ModifyIntakeTargetAmountByRecommendRequest;
import backend.mulkkam.intake.service.IntakeAmountService;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.SuggestionNotification;
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

    private final FcmService fcmService;
    private final IntakeAmountService intakeAmountService;
    private final SuggestionNotificationRepository suggestionNotificationRepository;
    private final DeviceRepository deviceRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void createAndSendSuggestionNotification(
            CreateTokenSuggestionNotificationRequest createTokenSuggestionNotificationRequest) {
        Member member = createTokenSuggestionNotificationRequest.member();
        List<Device> devicesByMember = deviceRepository.findAllByMember(member);
        Notification notification = createTokenSuggestionNotificationRequest.toNotification();
        Notification savedNotification = notificationRepository.save(notification);
        suggestionNotificationRepository.save(
                createTokenSuggestionNotificationRequest.toSuggestionNotification(savedNotification));

        sendNotificationByMember(createTokenSuggestionNotificationRequest, devicesByMember);
    }

    @Transactional
    public void applyTargetAmount(
            Long id,
            MemberDetails memberDetails
    ) {
        SuggestionNotification suggestionNotification = getSuggestionNotification(id, memberDetails.id());

        intakeAmountService.modifyDailyTargetBySuggested(memberDetails,
                new ModifyIntakeTargetAmountByRecommendRequest(suggestionNotification.getRecommendedTargetAmount()));

        suggestionNotification.updateApplyTargetAmount(true);
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

    private SuggestionNotification getSuggestionNotification(Long id, Long memberId) {
        return suggestionNotificationRepository.findByIdAndNotificationMemberId(id, memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_SUGGESTION_NOTIFICATION));
    }
}
