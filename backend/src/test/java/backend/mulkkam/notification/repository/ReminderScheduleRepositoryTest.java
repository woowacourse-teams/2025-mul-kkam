package backend.mulkkam.notification.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.notification.domain.ReminderSchedule;
import backend.mulkkam.support.fixture.ReminderScheduleFixtureBuilder;
import backend.mulkkam.support.fixture.member.MemberFixtureBuilder;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@DataJpaTest
class ReminderScheduleRepositoryTest {

    @Autowired
    private ReminderScheduleRepository reminderScheduleRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member enabledMember1;
    private Member enabledMember2;
    private Member disabledMember;

    @BeforeEach
    void setUp() {
        enabledMember1 = createMember("회원1", true);
        enabledMember2 = createMember("회원2", true);
        disabledMember = createMember("회원3", false);

        memberRepository.saveAll(List.of(enabledMember1, enabledMember2, disabledMember));
    }

    @DisplayName("특정 시간(시, 분)에 해당하는 활성화된 멤버 ID들을 조회한다")
    @Test
    void success_findAllActiveMemberIdsBySchedule() {
        // given
        LocalTime targetTime = LocalTime.of(14, 30);

        ReminderSchedule schedule1 = createReminderSchedule(enabledMember1, targetTime);
        ReminderSchedule schedule2 = createReminderSchedule(enabledMember2, targetTime);
        ReminderSchedule schedule3 = createReminderSchedule(disabledMember, targetTime);

        reminderScheduleRepository.saveAll(List.of(schedule1, schedule2, schedule3));

        // when
        Pageable pageable = PageRequest.of(0, 10);
        List<Long> result = reminderScheduleRepository.findAllActiveMemberIdsBySchedule(
                targetTime, null, pageable
        );

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(
                enabledMember1.getId(),
                enabledMember2.getId()
        );
        assertThat(result).doesNotContain(disabledMember.getId());
    }

    private Member createMember(String nickname, boolean isReminderEnabled) {
        return MemberFixtureBuilder.builder()
                .memberNickname(new MemberNickname(nickname))
                .isReminderEnabled(isReminderEnabled)
                .build();
    }

    private ReminderSchedule createReminderSchedule(Member member, LocalTime schedule) {
        return ReminderScheduleFixtureBuilder.withMember(member)
                .schedule(schedule)
                .build();
    }
}
