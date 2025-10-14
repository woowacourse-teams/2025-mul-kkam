package backend.mulkkam.notification.domain;

import backend.mulkkam.member.domain.Member;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public enum DefaultRemindSchedule {

    AT_14_00(LocalTime.of(14, 0)),

    AT_19_00(LocalTime.of(19, 0)),
    ;

    private final LocalTime schedule;

    DefaultRemindSchedule(LocalTime schedule) {
        this.schedule = schedule;
    }

    public static List<ReminderSchedule> of(Member member) {
        return Arrays.stream(DefaultRemindSchedule.values())
                .map(defaultRemindSchedule -> defaultRemindSchedule.toReminderSchedule(member))
                .toList();
    }

    public ReminderSchedule toReminderSchedule(Member member) {
        return new ReminderSchedule(member, this.schedule);
    }
}
