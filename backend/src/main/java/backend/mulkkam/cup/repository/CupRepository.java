package backend.mulkkam.cup.repository;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CupRepository extends JpaRepository<Cup, Long> {

    int countByMemberId(Long memberId);

    List<Cup> findAllByMember(Member member);

    List<Cup> findAllByMemberOrderByCupRankAsc(Member member);

    Optional<Cup> findByIdAndMember(Long id, Member member);

    void deleteByMember(Member member);
}
