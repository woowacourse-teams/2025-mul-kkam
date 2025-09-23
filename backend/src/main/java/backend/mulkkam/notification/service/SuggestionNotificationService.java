package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_SUGGESTION_NOTIFICATION;

import backend.mulkkam.averageTemperature.domain.AverageTemperature;
import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.AlarmException;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.NotFoundErrorCode;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokenRequest;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.intake.domain.vo.ExtraIntakeAmount;
import backend.mulkkam.intake.dto.request.ModifyIntakeTargetAmountBySuggestionRequest;
import backend.mulkkam.intake.service.IntakeAmountService;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.City;
import backend.mulkkam.notification.domain.CityDateTime;
import backend.mulkkam.notification.domain.Notification;
import backend.mulkkam.notification.domain.SuggestionNotification;
import backend.mulkkam.notification.dto.CreateActivityNotification;
import backend.mulkkam.notification.dto.CreateTokenSuggestionNotificationRequest;
import backend.mulkkam.notification.dto.CreateWeatherNotification;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.notification.repository.SuggestionNotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SuggestionNotificationService {

    private static final String DAILY_8AM_CRON = "0 0 8 * * *";

    private final IntakeAmountService intakeAmountService;
    private final WeatherService weatherService;
    private final SuggestionNotificationRepository suggestionNotificationRepository;
    private final DeviceRepository deviceRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @Scheduled(cron = DAILY_8AM_CRON)
    public void notifyAdditionalWaterIntakeByWeather() {
        CityDateTime cityNow = CityDateTime.now(City.SEOUL);
        notifyMembersByWeatherCondition(cityNow);
    }

    private void notifyMembersByWeatherCondition(CityDateTime cityNow) {
        AverageTemperature averageTemperature = weatherService.getAverageTemperature(cityNow);

        if (!isHotEnough(averageTemperature)) {
            return;
        }

        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            try {
                createAndSendSuggestionNotification(
                        toCreateSuggestionNotificationRequest(cityNow.localDateTime(), averageTemperature, member));
            } catch (AlarmException e) {
                log.info("[CLIENT_ERROR] accountId = {}, code={}({})",
                        member.getId(), // 2025. 8. 27. 19:34: 필드명이 accountId 이지만, memberId로 로깅하는 이유 v.250827_1934
                        e.getErrorCode().name(),
                        e.getErrorCode().getStatus()
                );
                // TODO 2025. 8. 27. 20:00: 로깅 리펙토링 필요(errorLoggedByGlobal)
            }
        }
    }

    @Transactional
    public void createActivityNotification(
            CreateActivityNotification createActivityNotification,
            MemberDetails memberDetails
    ) {
        Member member = getMember(memberDetails.id());
        CreateTokenSuggestionNotificationRequest createTokenSuggestionNotificationRequest = createActivityNotification.toFcmToken(member);
        createAndSendSuggestionNotification(createTokenSuggestionNotificationRequest);
    }

    @Transactional
    public void createAndSendSuggestionNotification(
            CreateTokenSuggestionNotificationRequest createTokenSuggestionNotificationRequest) {
        Member member = createTokenSuggestionNotificationRequest.member();
        List<Device> devicesByMember = deviceRepository.findAllByMember(member);

        Notification notification = createTokenSuggestionNotificationRequest.toNotification();
        Notification savedNotification = notificationRepository.save(notification);

        suggestionNotificationRepository.save(
                createTokenSuggestionNotificationRequest.toSuggestionNotification(savedNotification));

        sendNotificationByMemberDevices(createTokenSuggestionNotificationRequest, devicesByMember);
    }

    @Transactional
    public void applyTargetAmount(
            Long id,
            MemberDetails memberDetails
    ) {
        SuggestionNotification suggestionNotification = getSuggestionNotification(id, memberDetails.id());

        intakeAmountService.modifyDailyTargetBySuggested(memberDetails,
                new ModifyIntakeTargetAmountBySuggestionRequest(suggestionNotification.getRecommendedTargetAmount()));

        suggestionNotification.updateApplyTargetAmount(true);
    }

    public void delete(Long id) {
        SuggestionNotification suggestionNotification = getSuggestionNotification(id);
        suggestionNotificationRepository.delete(suggestionNotification);
    }

    private boolean isHotEnough(AverageTemperature averageTemperatureForCityDate) {
        double temperature = averageTemperatureForCityDate.getTemperature();
        return temperature > ExtraIntakeAmount.getExtraIntakeTemperatureThreshold();
    }

    private void sendNotificationByMemberDevices(
            CreateTokenSuggestionNotificationRequest createTokenSuggestionNotificationRequest,
            List<Device> devicesByMember
    ) {
        for (Device device : devicesByMember) {
            SendMessageByFcmTokenRequest sendMessageByFcmTokenRequest = createTokenSuggestionNotificationRequest.toSendMessageByFcmTokenRequest(
                    device.getToken());
            publisher.publishEvent(sendMessageByFcmTokenRequest);
        }
    }

    private CreateTokenSuggestionNotificationRequest toCreateSuggestionNotificationRequest(
            LocalDateTime todayDateTime,
            AverageTemperature averageTemperature,
            Member member
    ) {
        Double weight = member.getPhysicalAttributes().getWeight();
        ExtraIntakeAmount extraIntakeAmount = ExtraIntakeAmount.calculateWithAverageTemperature(averageTemperature.getTemperature(), weight);

        CreateWeatherNotification createWeatherNotification = new CreateWeatherNotification(averageTemperature,
                extraIntakeAmount, member, todayDateTime);

        return createWeatherNotification.toCreateTokenSuggestionNotificationRequest();
    }

    private SuggestionNotification getSuggestionNotification(Long id, Long memberId) {
        return suggestionNotificationRepository.findByIdAndNotificationMemberId(id, memberId)
                .orElseThrow(() -> new CommonException(NOT_FOUND_SUGGESTION_NOTIFICATION));
    }

    private SuggestionNotification getSuggestionNotification(Long id) {
        return suggestionNotificationRepository.findById(id)
                .orElseThrow(() -> new CommonException(NotFoundErrorCode.NOT_FOUND_SUGGESTION_NOTIFICATION));
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
