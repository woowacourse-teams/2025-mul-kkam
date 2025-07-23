package backend.mulkkam.cup.domain.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record CupNickname(
        String value
) {

    public static final int MAX_LENGTH = 10;
    public static final int MIN_LENGTH = 2;

    public CupNickname {
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("컵 이름 형식이 올바르지 않습니다.");
        }
    }
}
