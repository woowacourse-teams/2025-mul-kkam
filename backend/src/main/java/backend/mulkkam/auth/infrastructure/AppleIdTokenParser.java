package backend.mulkkam.auth.infrastructure;

import backend.mulkkam.auth.dto.response.AppleUserInfo;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import org.springframework.stereotype.Component;

@Component
public class AppleIdTokenParser {

    public AppleUserInfo parse(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet payload = signedJWT.getJWTClaimsSet();

            String appleUserId = payload.getSubject();
            String email = payload.getStringClaim("email");

            return new AppleUserInfo(appleUserId, email);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse Apple id_token", e);
        }
    }
}
