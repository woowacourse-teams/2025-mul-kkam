package backend.mulkkam.cup.dto.response;

import backend.mulkkam.cup.domain.Cup;
import backend.mulkkam.cup.domain.IntakeType;

public record CupResponse(
        Long id,
        String cupNickname,
        Integer cupAmount,
        Integer cupRank,
        IntakeType intakeType,
        String emoji
) {

    public CupResponse(Cup cup) {
        this(
                cup.getId(),
                cup.getNickname().value(),
                cup.getCupAmount().value(),
                cup.getCupRank().value(),
                cup.getIntakeType(),
                cup.getEmoji()
        );
    }
}
