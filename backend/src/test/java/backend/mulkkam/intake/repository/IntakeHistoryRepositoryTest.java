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

        @DisplayName("memberId와 날짜 범위에 해당하는 음용 기록을 조회할 때")
        @Nested
        class FindAllByMemberIdAndDateTimeBetween {

            @DisplayName("날짜의 범위에 맞는 기록만 조회된다")
            @Test
            void success_containsOnlyInDateRange() {
                // given
                Member member = new MemberFixture().build();
                Member savedMember = memberRepository.save(member);

                LocalDate startDate = LocalDate.of(2025, 10, 20);
                LocalDate endDate = LocalDate.of(2025, 10, 23);

                IntakeHistory firstHistoryInRange = new IntakeHistoryFixture()
                        .member(member)
                        .dateTime(LocalDateTime.of(
                                LocalDate.of(2025, 10, 20),
                                LocalTime.of(10, 30, 30)
                        ))
                        .build();

                IntakeHistory secondHistoryInRange = new IntakeHistoryFixture()
                        .member(member)
                        .dateTime(LocalDateTime.of(
                                LocalDate.of(2025, 10, 21),
                                LocalTime.of(10, 30, 30)
                        ))
                        .build();

                IntakeHistory thirdHistoryInRange = new IntakeHistoryFixture()
                        .member(member)
                        .dateTime(LocalDateTime.of(
                                LocalDate.of(2025, 10, 23),
                                LocalTime.of(23, 59, 59)
                        ))
                        .build();

                IntakeHistory firstHistoryNotInRange = new IntakeHistoryFixture()
                        .member(member)
                        .dateTime(LocalDateTime.of(
                                LocalDate.of(2025, 10, 24),
                                LocalTime.of(10, 30, 30)
                        ))
                        .build();

                IntakeHistory secondHistoryNotInRange = new IntakeHistoryFixture()
                        .member(member)
                        .dateTime(LocalDateTime.of(
                                LocalDate.of(2025, 10, 26),
                                LocalTime.of(10, 30, 30)
                        ))
                        .build();

                intakeHistoryRepository.save(firstHistoryInRange);
                intakeHistoryRepository.save(secondHistoryInRange);
                intakeHistoryRepository.save(thirdHistoryInRange);
                intakeHistoryRepository.save(firstHistoryNotInRange);
                intakeHistoryRepository.save(secondHistoryNotInRange);

                // when
                List<IntakeHistory> actual = intakeHistoryRepository.findAllByMemberIdAndDateTimeBetween(
                        savedMember.getId(),
                        startDate.atStartOfDay(),
                        endDate.atTime(LocalTime.MAX)
                );

                // then
                assertSoftly(softly -> {
                            assertThat(actual).contains(firstHistoryInRange);
                            assertThat(actual).contains(secondHistoryInRange);
                            assertThat(actual).contains(thirdHistoryInRange);
                            assertThat(actual).doesNotContain(firstHistoryNotInRange);
                            assertThat(actual).doesNotContain(secondHistoryNotInRange);
                        }
                );
            }

            @DisplayName("시작 날짜와 종료 날짜가 동일한 경우 해당 일자의 기록이 전부 반환된다")
            @Test
            void success_startDateAndEndDateIsSame() {
                // given
                Member member = new MemberFixture().build();
                Member savedMember = memberRepository.save(member);

                LocalDate startDate = LocalDate.of(2025, 10, 20);
                LocalDate endDate = LocalDate.of(2025, 10, 20);

                IntakeHistory firstHistoryInRange = new IntakeHistoryFixture()
                        .member(member)
                        .dateTime(LocalDateTime.of(
                                LocalDate.of(2025, 10, 20),
                                LocalTime.of(10, 30, 30)
                        ))
                        .build();

                IntakeHistory secondHistoryInRange = new IntakeHistoryFixture()
                        .member(member)
                        .dateTime(LocalDateTime.of(
                                LocalDate.of(2025, 10, 20),
                                LocalTime.of(23, 30, 30)
                        ))
                        .build();

                IntakeHistory firstHistoryNotInRange = new IntakeHistoryFixture()
                        .member(member)
                        .dateTime(LocalDateTime.of(
                                LocalDate.of(2025, 10, 22),
                                LocalTime.of(23, 50, 59)
                        ))
                        .build();

                intakeHistoryRepository.save(firstHistoryInRange);
                intakeHistoryRepository.save(secondHistoryInRange);
                intakeHistoryRepository.save(firstHistoryNotInRange);

                // when
                List<IntakeHistory> actual = intakeHistoryRepository.findAllByMemberIdAndDateTimeBetween(
                        savedMember.getId(),
                        startDate.atStartOfDay(),
                        endDate.atTime(LocalTime.MAX)
                );

                // then
                assertSoftly(softly -> {
                            assertThat(actual).contains(firstHistoryInRange);
                            assertThat(actual).contains(secondHistoryInRange);
                            assertThat(actual).doesNotContain(firstHistoryNotInRange);
                        }
                );

            }

            @DisplayName("해당 멤버의 기록이 아닌 경우 조회되지 않는다")
            @Test
            void success_containsOnlyHistoryOfMember() {
                Member member = new MemberFixture().build();
                Member savedMember = memberRepository.save(member);

                Member anotherMember = new MemberFixture()
                        .memberNickname(new MemberNickname("칼리"))
                        .build();
                Member savedAnotherMember = memberRepository.save(anotherMember);

                LocalDate startDate = LocalDate.of(2025, 10, 20);
                LocalDate endDate = LocalDate.of(2025, 10, 21);

                IntakeHistory historyOfAnotherMember = new IntakeHistoryFixture()
                        .member(savedAnotherMember)
                        .dateTime(LocalDateTime.of(
                                LocalDate.of(2025, 10, 20),
                                LocalTime.of(10, 30, 30)
                        ))
                        .build();

                IntakeHistory historyOfMember = new IntakeHistoryFixture()
                        .member(savedMember)
                        .dateTime(LocalDateTime.of(
                                LocalDate.of(2025, 10, 21),
                                LocalTime.of(10, 30, 30)
                        ))
                        .build();

                intakeHistoryRepository.save(historyOfAnotherMember);
                intakeHistoryRepository.save(historyOfMember);

                // when
                List<IntakeHistory> actual = intakeHistoryRepository.findAllByMemberIdAndDateTimeBetween(
                        savedMember.getId(),
                        startDate.atStartOfDay(),
                        endDate.atTime(LocalTime.MAX)
                );

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
}
