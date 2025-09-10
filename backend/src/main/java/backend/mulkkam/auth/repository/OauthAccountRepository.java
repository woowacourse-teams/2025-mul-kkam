package backend.mulkkam.auth.repository;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.auth.domain.OauthProvider;
import backend.mulkkam.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OauthAccountRepository extends JpaRepository<OauthAccount, Long> {

    Optional<OauthAccount> findByOauthId(String oauthId);

    Optional<OauthAccount> findByOauthIdAndOauthProvider(String oauthId, OauthProvider oauthProvider);

    @Query("""
                SELECT o
                FROM OauthAccount o
                JOIN FETCH o.member m
                WHERE o.id = :id
            """)
    Optional<OauthAccount> findByIdWithMember(Long id);

    Optional<OauthAccount> findByMember(Member member);
}
