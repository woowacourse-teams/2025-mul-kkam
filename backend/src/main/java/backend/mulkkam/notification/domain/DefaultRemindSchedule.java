package backend.mulkkam.notification.domain;

import backend.mulkkam.member.domain.Member;
import java.time.LocalTime;

public enum DefaultRemindSchedule {

    AT_14_00(LocalTime.of(14, 0)),

    AT_19_00(LocalTime.of(19, 0)),
    ;

    private final LocalTime schedule;

    DefaultRemindSchedule(LocalTime schedule) {
        this.schedule = schedule;
    }

    public ReminderSchedule toReminderSchedule(Member member) {
        return new ReminderSchedule(member, this.schedule);
    }
}
