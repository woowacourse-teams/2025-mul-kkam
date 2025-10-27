package backend.mulkkam.support.fixture;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.notification.domain.ReminderSchedule;
import java.time.LocalTime;

public class ReminderScheduleFixtureBuilder {

    private final Member member;
    private LocalTime schedule = LocalTime.of(12, 30);

    private ReminderScheduleFixtureBuilder(Member member) {
        this.member = member;
    }

    public static ReminderScheduleFixtureBuilder withMember(Member member) {
        return new ReminderScheduleFixtureBuilder(member);
    }

    public ReminderScheduleFixtureBuilder schedule(LocalTime schedule) {
        this.schedule = schedule;
        return this;
    }

    public ReminderSchedule build() {
        return new ReminderSchedule(member, schedule);
    }
}
