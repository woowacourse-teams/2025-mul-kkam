package backend.mulkkam.member.repository;

import backend.mulkkam.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberNicknameValue(String value);

    Optional<Member> findByOauthId(String oauthId);
}
