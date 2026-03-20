package backend.mulkkam.cup.domain.vo;

import static backend.mulkkam.common.exception.errorCode.BadRequestErrorCode.INVALID_CUP_EMOJI;

import backend.mulkkam.common.exception.CommonException;
import jakarta.persistence.Embeddable;

@Embeddable
public record CupEmojiUrl(String value) {

    public CupEmojiUrl {
        if (value == null || value.isBlank()) {
            throw new CommonException(INVALID_CUP_EMOJI);
        }
    }
}
