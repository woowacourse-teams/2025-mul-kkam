package backend.mulkkam.auth.repository;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRefreshTokenRepository extends JpaRepository<AccountRefreshToken, Long> {

    Optional<AccountRefreshToken> findByAccount(OauthAccount account);
    void deleteByAccountId(Long accountId);
}
