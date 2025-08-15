package backend.mulkkam.auth.repository;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRefreshTokenRepository extends JpaRepository<AccountRefreshToken, Long> {

    Optional<AccountRefreshToken> findByAccount(OauthAccount account);
    void deleteByAccount(OauthAccount account);
}
