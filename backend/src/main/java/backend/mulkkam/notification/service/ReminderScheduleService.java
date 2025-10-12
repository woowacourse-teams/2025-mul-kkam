package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_REMINDER_SCHEDULE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_REMINDER_SCHEDULE;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.infrastructure.fcm.dto.request.SendMessageByFcmTokensRequest;
import backend.mulkkam.device.domain.Device;
import backend.mulkkam.device.repository.DeviceRepository;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.ReminderSchedule;
import backend.mulkkam.notification.dto.NotificationMessageTemplate;
import backend.mulkkam.notification.dto.request.CreateReminderScheduleRequest;
import backend.mulkkam.notification.dto.request.ModifyReminderScheduleTimeRequest;
import backend.mulkkam.notification.dto.response.ReadReminderScheduleResponse;
import backend.mulkkam.notification.dto.response.ReadReminderSchedulesResponse;
import backend.mulkkam.notification.repository.NotificationRepository;
import backend.mulkkam.notification.repository.ReminderScheduleRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReminderScheduleService {

    private static final String MINUTELY_CRON = "0 * * * * *";
    private final ReminderScheduleRepository reminderScheduleRepository;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final DeviceRepository deviceRepository;
    private final ApplicationEventPublisher publisher;

    @Scheduled(cron = MINUTELY_CRON)
    public void notifyReminder() {
        LocalTime now = LocalTime.now();
        List<ReminderSchedule> schedules = reminderScheduleRepository.findAllActiveByHourAndMinuteWithMember(now);

        NotificationMessageTemplate randomMessageTemplate = RemindNotificationMessageTemplateProvider.getRandomMessageTemplate();

        List<Member> members = schedules.stream()
                .map(ReminderSchedule::getMember)
                .toList();

        // TODO: 배치 처리 로직 연결하기
        // 멤버당 알림 저장 로직
        notificationRepository.saveAll(randomMessageTemplate.toNotifications(members, LocalDateTime.now()));

        // 멤버의 device를 모두 찾고, 한번에 multicast
        List<String> tokens = members.stream()
                .flatMap(this::getDeviceTokens)
                .toList();

        SendMessageByFcmTokensRequest sendMessageByFcmTokensRequest = randomMessageTemplate.toSendMessageByFcmTokensRequest(
                tokens);
        publisher.publishEvent(sendMessageByFcmTokensRequest);
    }

    @Transactional
    public void create(
            MemberDetails memberDetails,
            CreateReminderScheduleRequest createReminderScheduleRequest
    ) {
        Member member = getMember(memberDetails.id());
        try {
            ReminderSchedule reminderSchedule = new ReminderSchedule(member, createReminderScheduleRequest.schedule());
            reminderScheduleRepository.saveAndFlush(reminderSchedule);
        } catch (DataIntegrityViolationException e) {
            throw new CommonException(DUPLICATED_REMINDER_SCHEDULE);
        }
    }

    public ReadReminderSchedulesResponse read(MemberDetails memberDetails) {
        Member member = getMember(memberDetails.id());
        boolean isReminderEnabled = member.isReminderEnabled();
        List<ReadReminderScheduleResponse> reminderSchedules = reminderScheduleRepository.findAllByMemberOrderByScheduleAsc(
                        member)
                .stream()
                .map(ReadReminderScheduleResponse::new)
                .toList();
        return new ReadReminderSchedulesResponse(isReminderEnabled, reminderSchedules);
    }

    @Transactional
    public void modifyTime(
            MemberDetails memberDetails,
            ModifyReminderScheduleTimeRequest modifyReminderScheduleTimeRequest
    ) {
        ReminderSchedule reminderSchedule = reminderScheduleRepository
                .findByIdAndMemberId(modifyReminderScheduleTimeRequest.id(), memberDetails.id())
                .orElseThrow(() -> new CommonException(NOT_FOUND_REMINDER_SCHEDULE));
        try {
            reminderSchedule.modifyTime(modifyReminderScheduleTimeRequest.schedule());
            reminderScheduleRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new CommonException(DUPLICATED_REMINDER_SCHEDULE);
        }
    }

    @Transactional
    public void delete(
            MemberDetails memberDetails,
            Long id
    ) {
        if (reminderScheduleRepository.existsByIdAndMemberId(id, memberDetails.id())) {
            reminderScheduleRepository.deleteById(id);
            return;
        }
        throw new CommonException(NOT_FOUND_REMINDER_SCHEDULE);
    }

    private Stream<String> getDeviceTokens(Member member) {
        return deviceRepository.findAllByMember(member).stream()
                .map(Device::getToken);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
