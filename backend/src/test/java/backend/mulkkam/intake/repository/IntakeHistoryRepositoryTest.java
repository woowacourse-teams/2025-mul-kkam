package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.IntakeHistoryFixture;
import backend.mulkkam.support.MemberFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJpaTest
class IntakeHistoryRepositoryTest {

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("Member 의 id 을 통해 음용 기록을 찾을 때")
    @Nested
    class FindAllByMemberId {

        @DisplayName("정상적으로 모든 기록들이 조회된다")
        @Test
        void success() {
            // given
            Member member = new MemberFixture().build();
            Member savedMember = memberRepository.save(member);

            IntakeHistory firstIntakeHistory = new IntakeHistoryFixture()
                    .member(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 3, 16),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory secondIntakeHistory = new IntakeHistoryFixture()
                    .member(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 3, 17),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            intakeHistoryRepository.save(firstIntakeHistory);
            intakeHistoryRepository.save(secondIntakeHistory);

            List<IntakeHistory> expected = List.of(firstIntakeHistory, secondIntakeHistory);

            // when
            List<IntakeHistory> actual = intakeHistoryRepository.findAllByMemberId(savedMember.getId());

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("해당 멤버의 기록이 아닌 경우 조회되지 않는다")
        @Test
        void success_containsOnlyHistoryOfMember() {
            // given
            Member member = new MemberFixture().build();
            Member savedMember = memberRepository.save(member);

            Member anotherMember = new MemberFixture()
                    .memberNickname(new MemberNickname("칼리"))
                    .build();
            Member savedAnotherMember = memberRepository.save(anotherMember);

            IntakeHistory historyOfAnotherMember = new IntakeHistoryFixture()
                    .member(savedAnotherMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 3, 16),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            IntakeHistory historyOfMember = new IntakeHistoryFixture()
                    .member(savedMember)
                    .dateTime(LocalDateTime.of(
                            LocalDate.of(2025, 3, 17),
                            LocalTime.of(10, 30, 30)
                    ))
                    .build();

            intakeHistoryRepository.save(historyOfAnotherMember);
            intakeHistoryRepository.save(historyOfMember);

            // when
            List<IntakeHistory> actual = intakeHistoryRepository.findAllByMemberId(savedMember.getId());

            // then
            assertSoftly(softAssertions -> {
                        assertThat(actual).hasSize(1);
                        assertThat(actual).contains(historyOfMember);
                        assertThat(actual).doesNotContain(historyOfAnotherMember);
                    }
            );
        }

    }
}
