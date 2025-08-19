package backend.mulkkam.auth.repository;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OauthAccountRepository extends JpaRepository<OauthAccount, Long> {

    Optional<OauthAccount> findByOauthId(String oauthId);

    @Query("""
                SELECT o
                FROM OauthAccount o
                JOIN FETCH o.member m
                WHERE o.id = :id
            """)
    Optional<OauthAccount> findByIdWithMember(Long id);

    Optional<OauthAccount> findByMember(Member member);
}
