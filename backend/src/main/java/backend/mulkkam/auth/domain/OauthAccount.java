package backend.mulkkam.auth.domain;

import backend.mulkkam.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class OauthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private Member member;

    @Column(nullable = false)
    private String oauthId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OauthProvider oauthProvider;

    public OauthAccount(Member member, String oauthId, OauthProvider oauthProvider) {
        this.member = member;
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
    }

    public OauthAccount(
            String oauthId,
            OauthProvider oauthProvider
    ) {
        this(null, oauthId, oauthProvider);
    }
}
