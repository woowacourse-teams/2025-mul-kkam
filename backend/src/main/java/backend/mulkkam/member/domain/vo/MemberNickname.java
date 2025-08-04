package backend.mulkkam.member.domain.vo;

import backend.mulkkam.common.exception.CommonException;
import jakarta.persistence.Embeddable;

import static backend.mulkkam.common.exception.errorCode.ErrorCode.INVALID_MEMBER_NICKNAME;

@Embeddable
public record MemberNickname(
        String value
) {

    public static final int MAX_LENGTH = 10;
    public static final int MIN_LENGTH = 2;

    public MemberNickname {
        if (value.length() > MAX_LENGTH || value.length() < MIN_LENGTH) {
            throw new CommonException(INVALID_MEMBER_NICKNAME);
        }
    }
}
