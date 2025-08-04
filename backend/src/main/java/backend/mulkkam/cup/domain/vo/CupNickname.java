package backend.mulkkam.cup.domain.vo;

import backend.mulkkam.common.exception.CommonException;
import jakarta.persistence.Embeddable;

import static backend.mulkkam.common.exception.errorCode.ErrorCode.INVALID_CUP_NICKNAME;

@Embeddable
public record CupNickname(
        String value
) {

    public static final int MAX_LENGTH = 10;
    public static final int MIN_LENGTH = 2;

    public CupNickname {
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new CommonException(INVALID_CUP_NICKNAME);
        }
    }
}
