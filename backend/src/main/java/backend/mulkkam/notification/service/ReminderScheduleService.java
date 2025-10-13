package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.ConflictErrorCode.DUPLICATED_REMINDER_SCHEDULE;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_REMINDER_SCHEDULE;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.ReminderSchedule;
import backend.mulkkam.notification.dto.request.CreateReminderScheduleRequest;
import backend.mulkkam.notification.dto.request.ModifyReminderScheduleTimeRequest;
import backend.mulkkam.notification.dto.response.ReadReminderScheduleResponse;
import backend.mulkkam.notification.dto.response.ReadReminderSchedulesResponse;
import backend.mulkkam.notification.repository.ReminderScheduleRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    private final NotificationService notificationService;

    @Transactional
    @Scheduled(cron = MINUTELY_CRON)
    public void scheduleReminderNotification() {
        LocalDateTime now = LocalDateTime.now();
        executeReminderNotification(now);
    }

    public void executeReminderNotification(LocalDateTime now) {
        List<ReminderSchedule> schedules = reminderScheduleRepository.findAllActiveByHourAndMinuteWithMember(now.toLocalTime());

        if (schedules.isEmpty()) {
            return;
        }

        notificationService.processReminderNotifications(schedules, now);
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

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }
}
