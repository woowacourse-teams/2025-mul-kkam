package backend.mulkkam.cup.domain.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record CupNickname(
        String value
) {
    public static final int MAX_LENGTH = 5;

    public CupNickname {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("오류임 ");
        }
    }
}
