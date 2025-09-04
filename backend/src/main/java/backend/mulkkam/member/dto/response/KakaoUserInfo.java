package backend.mulkkam.member.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfo(
        @JsonProperty("id")
        String oauthMemberId
) {
}
