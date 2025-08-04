package backend.mulkkam.auth.service;

import backend.mulkkam.auth.domain.OauthAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class OauthJwtTokenHandler {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expire-length}")
    private long expireLengthInMilliseconds;

    private JwtParser parser;

    @PostConstruct
    public void init() {
        this.parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build();
    }

    public String createToken(OauthAccount account) {
        Claims claims = Jwts.claims()
                .subject(account.getId().toString())
                .build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + expireLengthInMilliseconds);
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public Long getSubject(String token) {
        try {
            Claims claims = getBodyWithValidation(token);
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            // TODO: CustomException 에러 반환 (컨벤션 변경으로 인해 보류)
            throw new IllegalArgumentException("INVALID_TOKEN");
        }
    }

    private Claims getBodyWithValidation(final String token) {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            // TODO: CustomException 에러 반환 (컨벤션 변경으로 인해 보류)
            throw new IllegalArgumentException("INVALID_TOKEN");
        }
    }
}
