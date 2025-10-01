package backend.mulkkam.notification.service;

import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_MEMBER;
import static backend.mulkkam.common.exception.errorCode.NotFoundErrorCode.NOT_FOUND_REMIND_SCHEDULE;

import backend.mulkkam.common.dto.MemberDetails;
import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.ReminderSchedule;
import backend.mulkkam.notification.dto.request.CreateReminderScheduleRequest;
import backend.mulkkam.notification.dto.request.ModifyReminderScheduleTimeRequest;
import backend.mulkkam.notification.dto.response.ReadReminderSchedulesResponse;
import backend.mulkkam.notification.repository.ReminderScheduleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReminderScheduleService {

    private final ReminderScheduleRepository reminderScheduleRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void create(
            MemberDetails memberDetails,
            CreateReminderScheduleRequest createReminderScheduleRequest
    ) {
        Member member = getMember(memberDetails.id());
        ReminderSchedule reminderSchedule = new ReminderSchedule(member, createReminderScheduleRequest.schedule());
        reminderScheduleRepository.save(reminderSchedule);
    }

    public ReadReminderSchedulesResponse read(MemberDetails memberDetails) {
        Member member = getMember(memberDetails.id());
        boolean isReminderEnabled = member.isReminderEnabled();
        List<ReminderSchedule> reminderSchedules = reminderScheduleRepository.findAllByMember(member);
        return new ReadReminderSchedulesResponse(isReminderEnabled, reminderSchedules);
    }

    @Transactional
    public void modifyTime(
            MemberDetails memberDetails,
            ModifyReminderScheduleTimeRequest modifyReminderScheduleTimeRequest
    ) {
        Member member = getMember(memberDetails.id());
        ReminderSchedule reminderSchedule = getReminderSchedule(modifyReminderScheduleTimeRequest.id());
        reminderSchedule.isOwnedBy(member);
        reminderSchedule.modifyTime(modifyReminderScheduleTimeRequest.schedule());
    }

    @Transactional
    public void delete(
            MemberDetails memberDetails,
            Long id
    ) {
        Member member = getMember(memberDetails.id());
        ReminderSchedule reminderSchedule = getReminderSchedule(id);
        reminderSchedule.isOwnedBy(member);
        reminderScheduleRepository.deleteById(id);
    }

    private ReminderSchedule getReminderSchedule(Long id) {
        return reminderScheduleRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_REMIND_SCHEDULE));
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(NOT_FOUND_MEMBER));
    }

}
