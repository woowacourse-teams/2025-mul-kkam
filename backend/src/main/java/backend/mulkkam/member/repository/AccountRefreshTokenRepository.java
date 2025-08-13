package backend.mulkkam.member.repository;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import backend.mulkkam.auth.domain.OauthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRefreshTokenRepository extends JpaRepository<AccountRefreshToken, Long> {
    void deleteByAccount(OauthAccount account);
}
