package backend.mulkkam.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AccountRefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "account_id", nullable = false)
    @ManyToOne
    private OauthAccount account;

    @Column(nullable = false)
    private String refreshToken;

    public AccountRefreshToken(OauthAccount account, String refreshToken) {
        this(null, account, refreshToken);
    }

    public boolean isMatchWith(String otherToken) {
        return refreshToken.equals(otherToken);
    }

    public void reissueToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
