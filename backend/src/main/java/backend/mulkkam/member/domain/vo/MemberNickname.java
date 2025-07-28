package backend.mulkkam.member.domain.vo;

import backend.mulkkam.common.exception.CommonException;
import backend.mulkkam.common.exception.errorCode.BadRequestErrorCode;
import jakarta.persistence.Embeddable;

@Embeddable
public record MemberNickname(
        String value
) {

    public static final int MAX_LENGTH = 10;
    public static final int MIN_LENGTH = 2;

    public MemberNickname {
        if (value.length() > MAX_LENGTH || value.length() < MIN_LENGTH) {
            throw new CommonException(BadRequestErrorCode.INVALID_MEMBER_NICKNAME);
        }
    }
}
