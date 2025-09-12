package backend.mulkkam.cup.domain;

import static backend.mulkkam.common.exception.errorCode.InternalServerErrorErrorCode.INVALID_EMOJI_CODE_FORMAT;

import backend.mulkkam.common.exception.CommonException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EmojiType {

    DEFAULT,
    COMMON,
    ;

    public static EmojiType of(String text) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new CommonException(INVALID_EMOJI_CODE_FORMAT));
    }
}
