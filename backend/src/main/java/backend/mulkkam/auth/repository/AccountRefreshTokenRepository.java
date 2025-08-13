package backend.mulkkam.auth.repository;

import backend.mulkkam.auth.domain.AccountRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRefreshTokenRepository extends JpaRepository<AccountRefreshToken, Long> {
}
