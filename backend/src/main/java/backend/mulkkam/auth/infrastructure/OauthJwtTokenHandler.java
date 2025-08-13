package backend.mulkkam.auth.infrastructure;

import static backend.mulkkam.common.exception.errorCode.UnauthorizedErrorCode.INVALID_TOKEN;

import backend.mulkkam.auth.domain.OauthAccount;
import backend.mulkkam.common.exception.CommonException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OauthJwtTokenHandler {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expire-length}")
    private Long expireLengthInMilliseconds;

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
            Claims claims = getClaims(token);
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new CommonException(INVALID_TOKEN);
        }
    }

    private Claims getClaims(String token) {
        try {
            return parser.parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new CommonException(INVALID_TOKEN);
        }
    }
}
