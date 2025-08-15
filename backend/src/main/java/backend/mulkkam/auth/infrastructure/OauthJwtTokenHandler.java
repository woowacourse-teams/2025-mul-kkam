package backend.mulkkam.auth.infrastructure;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.common.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.UUID;

@Component
public class OauthJwtTokenHandler {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access.expire-length}")
    private Long accessExpireInMilliseconds;

    @Value("${security.jwt.refresh.expire-length}")
    private Long refreshExpireInMilliseconds;

    private JwtParser parser;

    @PostConstruct
    public void init() {
        this.parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build();
    }

    public Long getAccountId(String token) throws InvalidTokenException {
        try {
            Claims claims = getClaims(token);
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new InvalidTokenException();
        }
    }

    public @Nullable Long getMemberId(String token) throws InvalidTokenException {
        try {
            Claims claims = getClaims(token);
            return claims.get("memberId", Long.class);
        } catch (NumberFormatException e) {
            throw new InvalidTokenException();
        }
    }

    private Claims getClaims(String token) throws InvalidTokenException {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    public String createAccessToken(OauthAccount account) {
        Claims claims = generateClaims(account);

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessExpireInMilliseconds);
        return getCompactedJwt(claims, now, validity);
    }

    public String createRefreshToken(OauthAccount account) {
        Claims claims = generateClaims(account);

        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshExpireInMilliseconds);
        return getCompactedJwt(claims, now, validity);
    }

    private Claims generateClaims(OauthAccount account) {
        Long memberId = null;
        if (account.finishedOnboarding()) {
            memberId = account.getMember().getId();
        }
        return Jwts.claims()
                .subject(account.getId().toString())
                .id(UUID.randomUUID().toString())
                .add("memberId", memberId)
                .build();
    }

    private String getCompactedJwt(Claims claims, Date now, Date validity) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }
}
