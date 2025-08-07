package backend.mulkkam.intake.repository;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import backend.mulkkam.intake.domain.IntakeHistory;
import backend.mulkkam.intake.domain.IntakeHistoryDetail;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.IntakeDetailFixtureBuilder;
import backend.mulkkam.support.IntakeHistoryFixtureBuilder;
import backend.mulkkam.support.MemberFixtureBuilder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class IntakeHistoryDetailRepositoryTest {

    @Autowired
    private IntakeHistoryRepository intakeHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IntakeHistoryDetailRepository intakeDetailRepository;

    @DisplayName("음용 세부 기록을 조회할 때에")
    @Nested
    class FindAll {

        @DisplayName("날짜와 멤버에 맞게 조회에 성공한다")
        @Test
        void success_memberAndDateAreValid() {
            // given
            LocalDate date = LocalDate.of(2025, 7, 15);
            Member member = MemberFixtureBuilder
                    .builder()
                    .build();
            memberRepository.save(member);

            IntakeHistory firstIntakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(date)
                    .build();
            intakeHistoryRepository.save(firstIntakeHistory);

            IntakeHistory secondIntakeHistory = IntakeHistoryFixtureBuilder
                    .withMember(member)
                    .date(date.plusDays(1))
                    .build();
            intakeHistoryRepository.save(secondIntakeHistory);

            IntakeHistoryDetail firstIntakeDetail = IntakeDetailFixtureBuilder
                    .withIntakeHistory(firstIntakeHistory)
                    .time(LocalTime.of(10, 0))
                    .build();

            IntakeHistoryDetail secondIntakeDetail = IntakeDetailFixtureBuilder
                    .withIntakeHistory(firstIntakeHistory)
                    .time(LocalTime.of(11, 0))
                    .build();

            IntakeHistoryDetail thirdIntakeDetail = IntakeDetailFixtureBuilder
                    .withIntakeHistory(secondIntakeHistory)
                    .time(LocalTime.of(15, 0))
                    .build();

            IntakeHistoryDetail fourthIntakeDetail = IntakeDetailFixtureBuilder
                    .withIntakeHistory(secondIntakeHistory)
                    .time(LocalTime.of(13, 0))
                    .build();

            intakeDetailRepository.saveAll(
                    List.of(firstIntakeDetail, secondIntakeDetail, thirdIntakeDetail, fourthIntakeDetail));

            // when
            List<IntakeHistoryDetail> firstDetails = intakeDetailRepository.findAllByMemberIdAndDateRange(
                    member.getId(),
                    date,
                    date
            );
            List<IntakeHistoryDetail> secondDetails = intakeDetailRepository.findAllByMemberIdAndDateRange(
                    member.getId(),
                    date.plusDays(1),
                    date.plusDays(1));
            List<IntakeHistoryDetail> allDetails = intakeDetailRepository.findAllByMemberIdAndDateRange(member.getId(),
                    date,
                    date.plusDays(1));

            // then
            assertSoftly(softly -> {
                softly.assertThat(firstDetails).hasSize(2);
                softly.assertThat(secondDetails).hasSize(2);
                softly.assertThat(allDetails).hasSize(4);
            });
        }

    }
}
