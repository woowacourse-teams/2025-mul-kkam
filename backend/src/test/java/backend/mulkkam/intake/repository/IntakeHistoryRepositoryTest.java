package backend.mulkkam.intake.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.dto.request.DateRangeRequest;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.domain.vo.MemberNickname;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.fixture.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.fixture.MemberFixtureBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
class IntakeHistoryRepositoryTest {

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("Member 의 oauthMemberId 을 통해 음용 기록을 찾을 때")
    @Nested
    class FindAllByMemberId {

        private Member savedMember;

        @BeforeEach
        void setUp() {
            Member member = MemberFixtureBuilder.builder().build();
            savedMember = memberRepository.save(member);
        }

        @DisplayName("정상적으로 모든 기록들이 조회된다")
        @Test
        void success_withExistedMemberId() {
            // given

            IntakeHistory firstIntakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 3, 16))
                    .build();

            IntakeHistory secondIntakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 3, 16))
                    .build();

            intakeHistoryRepository.saveAll(List.of(
                    firstIntakeHistory,
                    secondIntakeHistory
            ));

            List<IntakeHistory> expected = List.of(firstIntakeHistory, secondIntakeHistory);

            // when
            List<IntakeHistory> actual = intakeHistoryRepository.findAllByMember(savedMember);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @DisplayName("해당 멤버의 기록이 아닌 경우 조회되지 않는다")
        @Test
        void success_containsOnlyHistoryOfMember() {
            // given
            Member anotherMember = MemberFixtureBuilder.builder()
                    .memberNickname(new MemberNickname("칼리"))
                    .build();
            Member savedAnotherMember = memberRepository.save(anotherMember);

            IntakeHistory historyOfAnotherMember = IntakeHistoryFixtureBuilder
                    .withMember(savedAnotherMember)
                    .date(LocalDate.of(2025, 3, 16))

                    .build();

            IntakeHistory historyOfMember = IntakeHistoryFixtureBuilder
                    .withMember(savedMember)
                    .date(LocalDate.of(2025, 3, 16))
                    .build();

            intakeHistoryRepository.saveAll(List.of(
                    historyOfAnotherMember,
                    historyOfMember
            ));

            intakeHistoryRepository.save(historyOfAnotherMember);
            intakeHistoryRepository.save(historyOfMember);

            // when
            List<IntakeHistory> actual = intakeHistoryRepository.findAllByMember(savedMember);

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
                LocalDate startDate = LocalDate.of(2025, 10, 20);
                LocalDate endDate = LocalDate.of(2025, 10, 23);

                IntakeHistory firstHistoryInRange = IntakeHistoryFixtureBuilder
                        .withMember(savedMember)
                        .date(LocalDate.of(2025, 10, 20))
                        .build();

                IntakeHistory secondHistoryInRange = IntakeHistoryFixtureBuilder
                        .withMember(savedMember)
                        .date(LocalDate.of(2025, 10, 21))
                        .build();

                IntakeHistory thirdHistoryInRange = IntakeHistoryFixtureBuilder
                        .withMember(savedMember)
                        .date(LocalDate.of(2025, 10, 22))
                        .build();

                IntakeHistory firstHistoryNotInRange = IntakeHistoryFixtureBuilder
                        .withMember(savedMember)
                        .date(LocalDate.of(2025, 10, 24))
                        .build();

                IntakeHistory secondHistoryNotInRange = IntakeHistoryFixtureBuilder
                        .withMember(savedMember)
                        .date(LocalDate.of(2025, 10, 25))
                        .build();

                intakeHistoryRepository.saveAll(List.of(
                        firstHistoryInRange,
                        secondHistoryInRange,
                        thirdHistoryInRange,
                        firstHistoryNotInRange,
                        secondHistoryNotInRange
                ));

                DateRangeRequest dateRangeRequest = new DateRangeRequest(
                        startDate,
                        endDate
                );

                // when
                List<IntakeHistory> actual = intakeHistoryRepository.findAllByMemberAndHistoryDateBetween(
                        savedMember,
                        dateRangeRequest.from(),
                        dateRangeRequest.to()
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
                LocalDate startDate = LocalDate.of(2025, 10, 20);
                LocalDate endDate = LocalDate.of(2025, 10, 20);

                IntakeHistory firstHistoryInRange = IntakeHistoryFixtureBuilder
                        .withMember(savedMember)
                        .date(LocalDate.of(2025, 10, 20))
                        .build();

                IntakeHistory secondHistoryInRange = IntakeHistoryFixtureBuilder
                        .withMember(savedMember)
                        .date(LocalDate.of(2025, 10, 20))
                        .build();

                IntakeHistory firstHistoryNotInRange = IntakeHistoryFixtureBuilder
                        .withMember(savedMember)
                        .date(LocalDate.of(2025, 10, 21))
                        .build();

                intakeHistoryRepository.saveAll(List.of(
                        firstHistoryInRange,
                        secondHistoryInRange,
                        firstHistoryNotInRange));

                DateRangeRequest dateRangeRequest = new DateRangeRequest(
                        startDate,
                        endDate
                );

                // when
                List<IntakeHistory> actual = intakeHistoryRepository.findAllByMemberAndHistoryDateBetween(
                        savedMember,
                        dateRangeRequest.from(),
                        dateRangeRequest.to()
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
                Member anotherMember = MemberFixtureBuilder.builder()
                        .memberNickname(new MemberNickname("칼리"))
                        .build();
                Member savedAnotherMember = memberRepository.save(anotherMember);

                LocalDate startDate = LocalDate.of(2025, 10, 20);
                LocalDate endDate = LocalDate.of(2025, 10, 21);

                IntakeHistory historyOfAnotherMember = IntakeHistoryFixtureBuilder
                        .withMember(savedAnotherMember)
                        .date(LocalDate.of(2025, 10, 20))
                        .build();

                IntakeHistory historyOfMember = IntakeHistoryFixtureBuilder
                        .withMember(savedMember)
                        .date(LocalDate.of(2025, 10, 21))
                        .build();

                intakeHistoryRepository.saveAll(List.of(
                        historyOfAnotherMember,
                        historyOfMember
                ));

                DateRangeRequest dateRangeRequest = new DateRangeRequest(
                        startDate,
                        endDate
                );

                // when
                List<IntakeHistory> actual = intakeHistoryRepository.findAllByMemberAndHistoryDateBetween(
                        savedMember,
                        dateRangeRequest.from(),
                        dateRangeRequest.to()
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
