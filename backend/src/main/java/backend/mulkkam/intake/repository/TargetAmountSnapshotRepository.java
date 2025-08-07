package backend.mulkkam.intake.repository;

import backend.mulkkam.intake.domain.TargetAmountSnapshot;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TargetAmountSnapshotRepository extends JpaRepository<TargetAmountSnapshot, Long> {

    @Query("""
                SELECT t.targetAmount.value
                FROM TargetAmountSnapshot t
                WHERE t.member.id = :memberId
                  AND t.updatedAt = (
                      SELECT MAX(t2.updatedAt)
                      FROM TargetAmountSnapshot t2
                      WHERE t2.member.id = :memberId
                        AND t2.updatedAt < :before
                  )
            """)
    Optional<Integer> findLatestTargetAmountValueByMemberIdBeforeDate(
            @Param("memberId") Long memberId,
            @Param("before") LocalDate before
    );

    Optional<TargetAmountSnapshot> findByMemberIdAndUpdatedAt(
            Long memberId,
            LocalDate updatedAt
    );
}
