package backend.mulkkam.auth.infrastructure;

import backend.mulkkam.auth.config.AppleOauthConfig;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppleClientSecretGenerator {

    private final AppleOauthConfig appleOauthConfig;

    public String generate() {
        LocalDateTime now = LocalDateTime.now();

        return Jwts.builder()
            .header()
            .keyId(appleOauthConfig.getKeyId())
            .and()
            .issuer(appleOauthConfig.getTeamId())
            .issuedAt(toDate(now))
            .expiration(toDate(now.plusMonths(3)))
            .audience().add("https://appleid.apple.com").and()
            .subject(appleOauthConfig.getClientId())
            .signWith(getPrivateKey(), Jwts.SIG.ES256)
            .compact();
    }

    private Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private PrivateKey getPrivateKey() {
        try {
            String pemContent = new String(Base64.getDecoder().decode(appleOauthConfig.getPrivateKey()));

            Reader pemReader = new StringReader(pemContent);
            PEMParser pemParser = new PEMParser(pemReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();

            return converter.getPrivateKey(privateKeyInfo);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Apple private key", e);
        }
    }
}
