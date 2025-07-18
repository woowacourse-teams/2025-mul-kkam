package backend.mulkkam.member;

import jakarta.persistence.Embeddable;

@Embeddable
public record MemberNickname(
        String content
) {

    public static final int MAX_LENGTH = 10;
    public static final int MIN_LENGTH = 2;

    public MemberNickname {
        if (content.length() > MAX_LENGTH || content.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("오류임 ");
        }
    }
}
