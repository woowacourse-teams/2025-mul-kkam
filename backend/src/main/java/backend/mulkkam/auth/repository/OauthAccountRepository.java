package backend.mulkkam.auth.repository;

import backend.mulkkam.auth.domain.OauthAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthAccountRepository extends JpaRepository<OauthAccount, Long> {

    Optional<OauthAccount> findByOauthId(String oauthId);
}
