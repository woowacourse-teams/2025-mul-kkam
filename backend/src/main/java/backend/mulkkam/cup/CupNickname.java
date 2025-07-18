package backend.mulkkam.cup;

import jakarta.persistence.Embeddable;

@Embeddable
public record CupNickname(
        String content
) {
    public static final int MAX_LENGTH = 5;

    public CupNickname {
        if (content.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("오류임 ");
        }
    }
}
