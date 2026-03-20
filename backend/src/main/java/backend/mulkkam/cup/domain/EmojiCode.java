package backend.mulkkam.cup.domain;

import static backend.mulkkam.common.exception.errorCode.InternalServerErrorErrorCode.INVALID_EMOJI_CODE_FORMAT;

import backend.mulkkam.common.exception.CommonException;
import lombok.Getter;

import java.util.Objects;

@Getter
public class EmojiCode {

    private static final String CODE_DELIMITER = "\\.";

    private final IntakeType intakeType;
    private final EmojiType emojiType;

    private EmojiCode(IntakeType intakeType, EmojiType emojiType) {
        if (intakeType == null || emojiType == null) {
            throw new CommonException(INVALID_EMOJI_CODE_FORMAT);
        }
        this.intakeType = intakeType;
        this.emojiType = emojiType;
    }

    public static EmojiCode of(IntakeType type, EmojiType emojiType) {
        return new EmojiCode(type, emojiType);
    }

    public static EmojiCode of(String value) {
        try {
            if (value == null) {
                return null;
            }
            String[] split = value.split(CODE_DELIMITER);
            if (split.length != 2) {
                throw new IllegalArgumentException();
            }
            IntakeType type = IntakeType.valueOf(split[1].trim().toUpperCase());
            EmojiType emojiType = EmojiType.of(split[0].trim());
            return new EmojiCode(type, emojiType);
        } catch (Exception e) {
            throw new CommonException(INVALID_EMOJI_CODE_FORMAT);
        }
    }

    public String getValue() {
        return String.join(".", emojiType.name().toLowerCase(), intakeType.name().toLowerCase());
    }

    public EmojiCode modifyEmojiType(EmojiType emojiType) {
        return new EmojiCode(intakeType, emojiType);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EmojiCode emojiCode = (EmojiCode) object;
        return intakeType == emojiCode.intakeType && emojiType == emojiCode.emojiType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(intakeType, emojiType);
    }
}
