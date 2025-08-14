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

    public String createAccessToken(OauthAccount account) {
        Claims claims = generateClaims(account);

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessExpireInMilliseconds);
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public String createRefreshToken(OauthAccount account) {
        Claims claims = generateClaims(account);

        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshExpireInMilliseconds);
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    private Claims generateClaims(OauthAccount account) {
        return Jwts.claims()
                .subject(account.getId().toString())
                .id(UUID.randomUUID().toString())
                .build();
    }

    public Long getSubject(String token) {
        try {
            Claims claims = getClaims(token);
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new InvalidTokenException();
        }
    }

    private Claims getClaims(String token) {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }
}
