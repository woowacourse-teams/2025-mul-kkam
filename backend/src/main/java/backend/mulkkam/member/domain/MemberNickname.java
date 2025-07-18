package backend.mulkkam.member.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record MemberNickname(
        String value
) {

    public static final int MAX_LENGTH = 10;
    public static final int MIN_LENGTH = 2;

    public MemberNickname {
        if (value.length() > MAX_LENGTH || value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("오류임 ");
        }
    }
}
