package backend.mulkkam.intake.repository;

import static org.assertj.core.api.Assertions.assertThat;

import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import backend.mulkkam.intake.domain.vo.Amount;
import backend.mulkkam.member.domain.Member;
import backend.mulkkam.member.repository.MemberRepository;
import backend.mulkkam.support.MemberFixtureBuilder;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class TargetAmountSnapshotRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TargetAmountSnapshotRepository targetAmountSnapshotRepository;

    @DisplayName("사용자의 스냅샷을 찾을 때")
    @Nested
    class Find {

        private Member savedMember;

        @BeforeEach
        void setUp() {
            Member member = MemberFixtureBuilder
                    .builder()
                    .build();
            savedMember = memberRepository.save(member);
        }

        @DisplayName("기준 날보다 과거면서 가장 최근의 스냅샷을 가져온다")
        @Test
        void success_lastestSnapshotIsExisting() {
            // given
            LocalDate pastUpdatedAt = LocalDate.of(2025, 7, 25);
            LocalDate updatedAt = LocalDate.of(2025, 7, 28);
            TargetAmountSnapshot targetAmountSnapshot1 = new TargetAmountSnapshot(savedMember, pastUpdatedAt,
                    new Amount(1_000));
            TargetAmountSnapshot targetAmountSnapshot2 = new TargetAmountSnapshot(savedMember, updatedAt,
                    new Amount(2_000));

            targetAmountSnapshotRepository.save(targetAmountSnapshot1);
            targetAmountSnapshotRepository.save(targetAmountSnapshot2);

            // when
            Optional<Integer> findAmount = targetAmountSnapshotRepository.findLatestTargetAmountValueByMemberIdBeforeDate(
                    savedMember.getId(),
                    LocalDate.of(2025, 7, 26));

            // then
            assertThat(findAmount.get()).isEqualTo(1_000);
        }
    }
}
